package com.phpusr.wildrace

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class WildraceApplication

fun main(args: Array<String>) {
    runApplication<WildraceApplication>(*args)
}
