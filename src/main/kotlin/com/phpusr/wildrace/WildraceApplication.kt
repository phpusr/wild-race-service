package com.phpusr.wildrace

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WildraceApplication

fun main(args: Array<String>) {
    runApplication<WildraceApplication>(*args)
}
