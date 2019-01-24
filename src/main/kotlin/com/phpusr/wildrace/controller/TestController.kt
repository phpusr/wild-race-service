package com.phpusr.wildrace.controller

import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@Transactional(readOnly = true)
@RequestMapping("test")
@RestController
class TestController(
) {

    @GetMapping()
    fun test() {

    }

}