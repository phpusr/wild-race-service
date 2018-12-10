package com.phpusr.wildrace.service

import com.phpusr.wildrace.domain.dto.StatDto
import com.phpusr.wildrace.domain.vk.Post
import com.phpusr.wildrace.domain.vk.PostRepo
import com.phpusr.wildrace.domain.vk.Profile
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.NoSuchElementException

@Service
class StatService(private val postRepo: PostRepo) {

    fun calcStat(typeForm: String, startRange: String?, endRange: String?): StatDto {
        val stat = StatDto()

        if (typeForm == "date") {
            val df = SimpleDateFormat("yyyy-MM-dd")
            try {
                stat.startDate = df.parse(startRange)
            } catch (ignored: ParseException) {}
            try {
                // Изменение времени на окончание дня
                stat.endDate = Date(df.parse(endRange).time + (24 * 3600 * 1000 - 1))
            } catch (ignored: ParseException) {}
        } else if (typeForm == "distance") {
            stat.startDistance = startRange?.toIntOrNull()
            stat.endDistance = endRange?.toIntOrNull()
        }

        val firstRunning = getOneRunning(Sort.Direction.ASC)
        val lastRunning = getOneRunning(Sort.Direction.DESC)

        if (firstRunning == null || lastRunning == null) {
            throw NoSuchElementException("not_found_posts")
        }

        val firstIntRunning = getOneRunning(Sort.Direction.ASC, stat.startDate, stat.endDate)
        if (stat.startDate == null) {
            stat.startDate = firstIntRunning?.date
        }
        val lastIntRunning = getOneRunning(Sort.Direction.DESC, stat.startDate, stat.endDate)
        if (stat.endDate == null) {
            stat.endDate = lastIntRunning?.date
        }

        val runnersMap = mutableMapOf<Profile, MutableMap<String, Int>>()
        val intRunnersMap = mutableMapOf<Profile, MutableMap<String, Int>>()

        stat.trainingCountAll = lastRunning.number ?: -1

        stat.daysCountAll = getCountDays(firstRunning.date, lastRunning.date)
        stat.daysCountInterval = getCountDays(stat.startDate, stat.endDate)

        stat.distanceAll = lastRunning.sumDistance ?: -1

        stat.countTraining = listOf()

        stat.runnersCountAll = -1
        stat.runnersCountInterval = -1
        stat.newRunners = listOf()

        stat.topAllRunners = calcTopRunners(runnersMap)
        stat.topIntervalRunners = calcTopRunners(intRunnersMap)

        return stat
    }

    fun getOneRunning(direction: Sort.Direction, startDate: Date? = null, endDate: Date? = null): Post? {
        val sort = Sort(direction, "date")
        val pageable = PageRequest.of(0, 1, sort)

        return postRepo.findRunningPage(pageable, startDate, endDate).firstOrNull()
    }

    private fun calcTopRunners(runners: Map<Profile, Map<String, Int>>): List<Map<String, Any>> {
        val list = runners.map {
            mapOf("profile" to it.key, "distance" to (it.value["distance"] ?: 0))
        }.sortedBy { it["distance"] as Int * -1 }

        if (list.size > 5) {
            return list.subList(0, 5)
        }

        return list
    }

    private fun getCountDays(startDate: Date?, endDate: Date?): Int {
        if (startDate == null || endDate == null) {
            return 0
        }

        return Duration.of(endDate.time - startDate.time, ChronoUnit.MILLIS).toDays().toInt() + 1
    }

    //TODO move it to postService
    fun getLastRunning(): Post {
        val sort = Sort(Sort.Direction.DESC, "date")
        val pageable = PageRequest.of(0, 1, sort)

        return postRepo.findRunningPage(pageable).first()
    }

}