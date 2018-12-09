package com.phpusr.wildrace.service

import com.phpusr.wildrace.domain.dto.StatDto
import com.phpusr.wildrace.domain.vk.Post
import com.phpusr.wildrace.domain.vk.PostRepo
import com.phpusr.wildrace.domain.vk.Profile
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.*

@Service
class StatService(private val postRepo: PostRepo) {

    fun calcStat(typeForm: String, startRange: String?, endRange: String?): StatDto {
        val stat = StatDto()

        if (typeForm == "date") {
            val df = SimpleDateFormat("yyyy-MM-dd")
            stat.startDate = df.parse(startRange)
            // Изменение времени на окончание дня
            stat.endDate = Date(df.parse(endRange).time + (24 * 3600 * 1000 - 1))
        } else if (typeForm == "distance") {
            stat.startDistance = startRange?.toIntOrNull()
            stat.endDistance = endRange?.toIntOrNull()
        }

        val startDate = stat.startDate
        val endDate = stat.endDate
        val startDistance = stat.startDistance
        val endDistance = stat.endDistance

        val sort = Sort(Sort.Direction.ASC, "date")
        var pageable: Pageable = PageRequest.of(0, 1000, sort)
        var runningPage = postRepo.findRunningPage(pageable)
        stat.trainingCountAll = runningPage.totalElements.toInt()

        val firstRunning = runningPage.first()
        var firstIntRunning: Post? = null
        var lastIntRunning: Post? = null
        val runnersMap = mutableMapOf<Profile, MutableMap<String, Int>>()
        val intRunnersMap = mutableMapOf<Profile, MutableMap<String, Int>>()

        while(pageable.isPaged) {
            runningPage.stream().forEach { running ->
                if (firstIntRunning == null) {
                    if (startDate != null  && running.date >= startDate || startDistance != null && running.sumDistance!! > startDistance) {
                        firstIntRunning = running
                    }
                }

                addRunning(running, runnersMap)
                if (firstIntRunning != null && (endDate != null && running.date <= endDate || lastIntRunning == null)) {
                    addRunning(running, intRunnersMap)
                }

                if (firstIntRunning != null) {
                    if (endDate != null && running.date <= endDate || lastIntRunning == null && endDistance != null && running.sumDistance!! >= endDistance) {
                        lastIntRunning = running
                    }
                }
            }

            pageable = runningPage.nextPageable()
            runningPage = postRepo.findRunningPage(pageable)
        }

        val lastRunning = runningPage.last()

        stat.daysCountAll = getCountDays(firstRunning.date, lastRunning.date)
        stat.daysCountInterval = getCountDays(startDate ?: firstIntRunning?.date, endDate ?: lastIntRunning?.date)

        stat.distanceAll = lastRunning.sumDistance!!

        stat.countTraining = listOf()

        stat.runnersCountAll = 0
        stat.runnersCountInterval = 0
        stat.newRunners = listOf()

        stat.topAllRunners = calcTopRunners(runnersMap)
        stat.topIntervalRunners = calcTopRunners(intRunnersMap)

        return stat
    }

    private fun addRunning(running: Post, runnersMap: MutableMap<Profile, MutableMap<String, Int>>) {
        val map = runnersMap[running.from] ?: mutableMapOf()
        map["count"] = (map["count"] ?: 0) + 1
        map["distance"] = (map["distance"] ?: 0) + running.distance!!
        runnersMap[running.from] = map
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