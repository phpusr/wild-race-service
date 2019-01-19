package com.phpusr.wildrace.service

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.env.Environment
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@ActiveProfiles(profiles = [""])
@SpringBootTest
class DefaultEnvironmentServiceTest {
    @Autowired
    private lateinit var environmentService: EnvironmentService

    @Autowired
    private lateinit var environment: Environment

    @Test
    fun isDevelopmentTest() {
        Assert.assertEquals(environment.activeProfiles.size, 0)
        Assert.assertTrue(environmentService.isDevelopment())
    }
}

@RunWith(SpringRunner::class)
@ActiveProfiles(profiles = ["prod"])
@SpringBootTest
class ProdEnvironmentServiceTest {
    @Autowired
    private lateinit var environmentService: EnvironmentService

    @Autowired
    private lateinit var environment: Environment

    @Test
    fun isDevelopmentTest() {
        Assert.assertEquals(environment.activeProfiles.size, 1)
        Assert.assertTrue(!environmentService.isDevelopment())
    }
}

@RunWith(SpringRunner::class)
@ActiveProfiles(profiles = ["dev"])
@SpringBootTest
class DevEnvironmentServiceTest {
    @Autowired
    private lateinit var environmentService: EnvironmentService

    @Autowired
    private lateinit var environment: Environment

    @Test
    fun isDevelopmentTest() {
        Assert.assertEquals(environment.activeProfiles.size, 1)
        Assert.assertTrue(environmentService.isDevelopment())
    }
}
