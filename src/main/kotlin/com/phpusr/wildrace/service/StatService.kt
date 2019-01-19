package com.phpusr.wildrace.service

import com.phpusr.wildrace.domain.Post
import com.phpusr.wildrace.domain.PostRepo
import com.phpusr.wildrace.domain.Profile
import com.phpusr.wildrace.domain.TempDataRepo
import com.phpusr.wildrace.dto.EventType
import com.phpusr.wildrace.dto.RunnerDto
import com.phpusr.wildrace.dto.StatDto
import com.phpusr.wildrace.enum.StatType
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.function.BiConsumer
import kotlin.NoSuchElementException

@Service
class StatService(
        private val postRepo: PostRepo,
        private val tempDataRepo: TempDataRepo,
        private val statSender: BiConsumer<EventType, Map<String, Any>>,
        private val lastSyncDateSender: BiConsumer<EventType, Long>
) {

    fun calcStat(statType: StatType?, startRange: String?, endRange: String?): StatDto {
        val stat = StatDto()

        if (statType == StatType.Date) {
            val df = SimpleDateFormat("yyyy-MM-dd")
            try {
                stat.startDate = df.parse(startRange)
            } catch (ignored: ParseException) {}
            try {
                // Change time at end of day
                stat.endDate = Date(df.parse(endRange).time + (24 * 3600 * 1000 - 1))
            } catch (ignored: ParseException) {}
        } else if (statType == StatType.Distance) {
            stat.startDistance = startRange?.toIntOrNull()
            stat.endDistance = endRange?.toIntOrNull()
        }

        val firstRunning = getOneRunning(Sort.Direction.ASC)
        val lastRunning = getOneRunning(Sort.Direction.DESC)

        if (firstRunning == null || lastRunning == null) {
            throw NoSuchElementException("not_found_posts")
        }

        val firstIntRunning = getOneRunning(Sort.Direction.ASC, stat)
        if (stat.startDate == null) {
            stat.startDate = firstIntRunning?.date
        }
        val lastIntRunning = getOneRunning(Sort.Direction.DESC, stat)
        if (stat.endDate == null) {
            stat.endDate = lastIntRunning?.date
        }

        val runners = getRunners()
        stat.topAllRunners = getTopRunners(runners)
        val intRunners = getRunners(firstIntRunning, lastIntRunning)
        stat.topIntervalRunners = getTopRunners(intRunners)

        stat.daysCountAll = getCountDays(firstRunning.date, lastRunning.date)
        stat.daysCountInterval = getCountDays(stat.startDate, stat.endDate)

        stat.distanceAll = lastRunning.sumDistance ?: -1
        stat.distanceMaxOneMan = runners.first()

        stat.runnersCountAll = runners.size
        stat.runnersCountInterval = intRunners.size
        setNewRunners(stat, intRunners, stat.startDate)

        stat.trainingCountAll = lastRunning.number ?: -1
        stat.trainingMaxOneMan = runners.sortedBy { it.numberOfRuns * -1 }.first()

        return stat
    }

    fun getOneRunning(direction: Sort.Direction, stat: StatDto? = null): Post? {
        val sort = Sort(direction, "date")
        val pageable = PageRequest.of(0, 1, sort)

        return postRepo.findRunningPage(pageable, stat?.startDate, stat?.endDate, stat?.startDistance, stat?.endDistance).firstOrNull()
    }

    private fun getRunners(firstIntRunning: Post? = null, lastIntRunning: Post? = null): List<RunnerDto> {
        return postRepo.calcSumDistanceForRunners(firstIntRunning?.date, lastIntRunning?.date).map{
            val el = it as Array<*>
            RunnerDto(el[0] as Profile, el[1] as Long, el[2] as Long)
        }
    }

    private fun getTopRunners(runners: List<RunnerDto>): List<RunnerDto> {
        if (runners.size > 5) {
            return runners.subList(0, 5)
        }

        return runners
    }

    private fun setNewRunners(stat: StatDto, runners: List<RunnerDto>, startDate: Date?) {
        if (startDate == null) {
            stat.newRunners = listOf()
            return
        }

        val newRunners = runners.filter {
            it.profile.joinDate >= startDate
        }.map{ it.profile }.sortedBy { it.joinDate }

        stat.countNewRunners = newRunners.size

        val max = 25
        if (newRunners.size > max) {
            stat.newRunners = newRunners.subList(0, max)
            return
        }

        stat.newRunners = newRunners
    }

    private fun getCountDays(startDate: Date?, endDate: Date?): Int {
        if (startDate == null || endDate == null) {
            return 0
        }

        return Duration.of(endDate.time - startDate.time, ChronoUnit.MILLIS).toDays().toInt() + 1
    }

    fun getStat(): Map<String, Any> {
        val lastPost = getOneRunning(Sort.Direction.DESC)

        return mapOf(
                "sumDistance" to (lastPost?.sumDistance ?: 0),
                "numberOfRuns" to (lastPost?.number ?: 0),
                "numberOfPosts" to postRepo.count()
        )
    }

    fun updateStat() {
        val tempData = tempDataRepo.save(tempDataRepo.get().copy(lastSyncDate = Date()))
        lastSyncDateSender.accept(EventType.Update, tempData.lastSyncDate.time)
        statSender.accept(EventType.Update, getStat())
    }

}