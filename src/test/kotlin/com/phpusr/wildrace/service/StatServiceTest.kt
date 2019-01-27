package com.phpusr.wildrace.service

import com.phpusr.wildrace.enum.StatType
import org.hamcrest.Matchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ErrorCollector
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit4.SpringRunner
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

@TestPropertySource("/application-test.properties")
@Sql(value = ["/create-data-before.sql"])
@RunWith(SpringRunner::class)
@SpringBootTest
internal class StatServiceTest {

    @Autowired
    private lateinit var statService: StatService

    @get:Rule
    val collector = ErrorCollector()

    @Test
    fun calcStatWithoutParamsTest() {
        val stat = statService.calcStat(null, null, null)
        collector.checkThat(stat.startDistance, nullValue())
        collector.checkThat(stat.endDistance, nullValue())

        // Dates
        val startDate = Date.from(ZonedDateTime.of(2015, 9, 1, 3, 56, 9, 0, ZoneId.of("UTC")).toInstant())
        collector.checkThat(stat.startDate, comparesEqualTo(startDate))
        val endDate = Date.from(ZonedDateTime.of(2015, 9, 24, 10, 52, 34, 0, ZoneId.of("UTC")).toInstant())
        collector.checkThat(stat.endDate, comparesEqualTo(endDate))
        collector.checkThat(stat.daysCountAll, equalTo(24))
        collector.checkThat(stat.daysCountInterval, equalTo(24))

        // Distance
        collector.checkThat(stat.distanceAll, equalTo(1003L))
        collector.checkThat(stat.distancePerDayAvg.toDouble(), closeTo(41.792, 0.001))
        collector.checkThat(stat.distancePerTrainingAvg.toDouble(), closeTo(6.965, 0.001))
        collector.checkThat(stat.distanceMaxOneMan.profile.id, equalTo(63399502L))
        collector.checkThat(stat.distanceMaxOneMan.numberOfRuns, equalTo(12))
        collector.checkThat(stat.distanceMaxOneMan.sumDistance, equalTo(98))

        // Trainings
        collector.checkThat(stat.trainingCountAll, equalTo(144))
        collector.checkThat(stat.trainingCountPerDayAvg.toDouble(), closeTo(6.0, 0.001))
        collector.checkThat(stat.trainingMaxOneMan.profile.id, equalTo(63399502L))
        collector.checkThat(stat.trainingMaxOneMan.numberOfRuns, equalTo(12))
        collector.checkThat(stat.trainingMaxOneMan.sumDistance, equalTo(98))

        // Runners
        collector.checkThat(stat.runnersCountAll, equalTo(35))
        collector.checkThat(stat.runnersCountInterval, equalTo(35))
        collector.checkThat(stat.newRunners, iterableWithSize(25))
        val newRunnersIdsActual = stat.newRunners.joinToString(", ") { it.id.toString() }
        val newRunnersIdsExpect = "39752943, 8429458, 84566235, 82121484, 18273238, 2437792, 11351451, 63399502, 117963335, 258765420, 282180599, 10811344, 1553750, 52649788, 151575104, 6465387, 53388774, 139202307, 67799362, 5488260, 18530540, 122104881, 179202944, 24237989, 293509406"
        collector.checkThat(newRunnersIdsActual, equalTo(newRunnersIdsExpect))
        collector.checkThat(stat.countNewRunners, equalTo(35))

        collector.checkThat(stat.topAllRunners, iterableWithSize(5))
        val topAllRunnersActually = stat.topAllRunners.joinToString(", ") { "(${it.profile.id},${it.numberOfRuns},${it.sumDistance})" }
        val topAllRunnersExpect = "(63399502,12,98), (12274403,4,85), (5488260,3,74), (82121484,11,71), (2437792,7,54)"
        collector.checkThat(topAllRunnersActually, equalTo(topAllRunnersExpect))

        collector.checkThat(stat.topIntervalRunners, iterableWithSize(5))
        val topIntervalRunnersActually = stat.topAllRunners.joinToString(", ") { "(${it.profile.id},${it.numberOfRuns},${it.sumDistance})" }
        collector.checkThat(topIntervalRunnersActually, equalTo(topAllRunnersExpect))

        collector.checkThat(stat.type, equalTo(StatType.Date))
    }
}