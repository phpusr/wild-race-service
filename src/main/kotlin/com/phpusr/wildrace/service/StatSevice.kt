package com.phpusr.wildrace.service

import com.phpusr.wildrace.domain.dto.StatDto
import com.phpusr.wildrace.domain.vk.Post
import com.phpusr.wildrace.domain.vk.PostRepo
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.*

@Service
class StatService(private val postRepo: PostRepo) {

    fun calcStat(typeForm: String, startRange: String?, endRange: String?): StatDto {
        var startDate: Date?
        var endDate: Date?
        if (typeForm == "distance") {
            startDate = getStartDate(startRange?.toInt())
            endDate = getEndDate(endRange?.toInt())
        } else {
            val df = SimpleDateFormat("yyyy-MM-dd")
            startDate = df.parse(startRange)
            // Изменение времени на окончание дня
            endDate = Date(df.parse(endRange).time + (24 * 3600 * 1000 - 1))
        }

        val firstRunning = getFirstRunning()
        if (startDate == null) {
            startDate = firstRunning.date
        }

        val lastRunning = getLastRunning()
        if (endDate == null) {
            endDate = lastRunning.date
        }

        val sort = Sort(Sort.Direction.ASC, "date")
        var pageable: Pageable = PageRequest.of(0, 1000, sort)
        var runningPage = postRepo.findRunningPage(pageable)

        var index = 0
        while(pageable.isPaged) {
            println("Time: ${++index}, count: ${runningPage.count()}")
            //TODO continue calculating
            pageable = runningPage.nextPageable()
            runningPage = postRepo.findRunningPage(pageable)
        }



        val stat = StatDto(
                startDistance = startRange?.toIntOrNull(),
                endDistance = endRange?.toIntOrNull(),
                startDate = startDate,
                endDate = endDate,

                daysCountAll = 0,
                daysCountInterval = 0,

                distancePerDayAvg = 0f,
                distancePerTrainingAvg = 0f,

                trainingCountAll = runningPage.totalElements,
                countTraining = listOf(),

                runnersCountAll = 0,
                runnersCountInterval = 0,
                newRunners = listOf(),

                topAllRunners = listOf(),
                topIntervalRunners = listOf()
        )

        return stat
    }

    fun getStartDate(startDistance: Int?): Date? {
        if (startDistance == null) {
            return null
        }

        return postRepo.findStartDateList(startDistance, PageRequest.of(0, 1)).firstOrNull()
    }

    fun getEndDate(endDistance: Int?): Date? {
        if (endDistance == null) {
            return null
        }

        return postRepo.findEndDateList(endDistance, PageRequest.of(0, 1)).firstOrNull()
    }

    fun getFirstRunning(): Post {
        val sort = Sort(Sort.Direction.ASC, "date")
        val pageable = PageRequest.of(0, 1, sort)

        return postRepo.findRunningPage(pageable).first()
    }

    fun getLastRunning(): Post {
        val sort = Sort(Sort.Direction.DESC, "date")
        val pageable = PageRequest.of(0, 1, sort)

        return postRepo.findRunningPage(pageable).first()
    }

}