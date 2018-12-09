package com.phpusr.wildrace.service

import com.phpusr.wildrace.domain.dto.StatDto
import com.phpusr.wildrace.domain.vk.Post
import com.phpusr.wildrace.domain.vk.PostRepo
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

    private lateinit var firstRunning: Post
    private lateinit var lastRunning: Post
    private var trainingCountAll: Int = 0

    fun calcStat(typeForm: String, startRange: String?, endRange: String?): StatDto {
        var startDate: Date? = null
        var endDate: Date? = null

        if (typeForm == "date") {
            val df = SimpleDateFormat("yyyy-MM-dd")
            startDate = df.parse(startRange)
            // Изменение времени на окончание дня
            endDate = Date(df.parse(endRange).time + (24 * 3600 * 1000 - 1))
        }

        handleList()

        val daysCountAll = Duration.of(lastRunning.date.time - firstRunning.date.time, ChronoUnit.MILLIS).toDays().toInt() + 1
        val stat = StatDto(
                startDistance = startRange?.toIntOrNull(),
                endDistance = endRange?.toIntOrNull(),
                startDate = startDate,
                endDate = endDate,

                daysCountAll = daysCountAll,
                daysCountInterval = 0,

                distanceAll = lastRunning.sumDistance ?: 0,

                trainingCountAll = trainingCountAll,
                countTraining = listOf(),

                runnersCountAll = 0,
                runnersCountInterval = 0,
                newRunners = listOf(),

                topAllRunners = listOf(),
                topIntervalRunners = listOf()
        )

        return stat
    }

    private fun handleList() {
        val sort = Sort(Sort.Direction.ASC, "date")
        var pageable: Pageable = PageRequest.of(0, 1000, sort)
        var runningPage = postRepo.findRunningPage(pageable)
        trainingCountAll = runningPage.totalElements.toInt()
        firstRunning = runningPage.first()

        var index = 0
        while(pageable.isPaged) {
            println("Time: ${++index}, count: ${runningPage.count()}")
            //TODO continue calculating
            pageable = runningPage.nextPageable()
            runningPage = postRepo.findRunningPage(pageable)
        }

        lastRunning = runningPage.last()
    }

    //TODO move it to postService
    fun getLastRunning(): Post {
        val sort = Sort(Sort.Direction.DESC, "date")
        val pageable = PageRequest.of(0, 1, sort)

        return postRepo.findRunningPage(pageable).first()
    }

}