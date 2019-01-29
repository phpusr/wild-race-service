package com.phpusr.wildrace.config

import org.apache.http.HttpStatus
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultMatcher
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ActiveProfiles(profiles = [""])
@TestPropertySource("/application-test.properties")
@RunWith(SpringRunner::class)
@SpringBootTest
@AutoConfigureMockMvc
class WebSecurityConfigAccessTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    fun checkAccess(method: MockHttpServletRequestBuilder, status: ResultMatcher) {
        mockMvc.perform(method).andDo(print()).andExpect(status)
    }

    @Test
    // Should be access anonymous user to /test and /test/*
    fun accessToActionsTest() {
        checkAccess(get("/wild-race-ws/**"), status().isNotFound)
        checkAccess(get("/"), status().isOk)
        checkAccess(put("/"), status().isUnauthorized)
        checkAccess(get("/login"), status().isNotFound)
        checkAccess(post("/login"), status().isUnauthorized)
        checkAccess(get("/logout"), status().isNotFound)
        checkAccess(post("/logout"), status().isOk)
        checkAccess(put("/authorize"), status().isUnauthorized)

        checkAccess(get("/post"), status().isOk)
        checkAccess(put("/post/1"), status().isUnauthorized)
        checkAccess(delete("/post/1"), status().isUnauthorized)

        checkAccess(get("/stat"), status().isBadRequest)
        checkAccess(get("/stat?type=date"), status().isOk)
        checkAccess(put("/stat"), status().isUnauthorized)

        mockMvc.perform(get("/test")).andDo(print()).andExpect {
            Assert.assertNotEquals(HttpStatus.SC_UNAUTHORIZED, it.response.status)
        }
        mockMvc.perform(get("/test/1")).andDo(print()).andExpect {
            Assert.assertNotEquals(HttpStatus.SC_UNAUTHORIZED, it.response.status)
        }
    }
}

@ActiveProfiles(profiles = ["prod"])
@TestPropertySource("/application-test.properties")
@RunWith(SpringRunner::class)
@SpringBootTest
@AutoConfigureMockMvc
class WebSecurityConfigNoAccessTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    // Should be no access anonymous user to /test and /test/*
    fun accessToActionsTest() {
        mockMvc.perform(get("/test")).andDo(print()).andExpect {
            Assert.assertEquals(HttpStatus.SC_UNAUTHORIZED, it.response.status)
        }
        mockMvc.perform(get("/test/1")).andDo(print()).andExpect {
            Assert.assertEquals(HttpStatus.SC_UNAUTHORIZED, it.response.status)
        }
    }
}