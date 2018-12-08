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

        var pageable: Pageable = PageRequest.of(0, 1000)
        var runningPage = postRepo.findRunningPage(pageable)

        var index = 0
        while(runningPage.count() > 0) {
            //TODO  что-то не так
            println("Time: ${++index}, count: ${runningPage.count()}")
            pageable = runningPage.nextPageable()
            runningPage = postRepo.findRunningPage(pageable)
        }



        val stat = StatDto(
                startDistance = startRange?.toInt(),
                endDistance = endRange?.toInt(),
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
                topIntevalRunners = listOf()
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
        val sort = Sort.by(Sort.Order(Sort.Direction.ASC, "date"))
        val pageable = PageRequest.of(0, 1, sort)

        return postRepo.findRunningPage(pageable).first()
    }

    fun getLastRunning(): Post {
        val sort = Sort.by(Sort.Order(Sort.Direction.DESC, "date"))
        val pageable = PageRequest.of(0, 1, sort)

        return postRepo.findRunningPage(pageable).first()
    }

}