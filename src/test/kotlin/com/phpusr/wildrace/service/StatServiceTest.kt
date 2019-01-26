package com.phpusr.wildrace.service

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit4.SpringRunner

@TestPropertySource("/application-test.properties")
@Sql(value = ["/create-data-before.sql"])
@RunWith(SpringRunner::class)
@SpringBootTest
internal class StatServiceTest {

    @Autowired
    private lateinit var statService: StatService

    @Test
    fun calcStat() {
        val stat = statService.calcStat(null, null, null)
        Assert.assertEquals(1003, stat.distanceAll)

    }
}