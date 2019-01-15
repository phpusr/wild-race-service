package com.phpusr.wildrace.scheduler

import org.quartz.Job
import org.quartz.JobDetail
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.quartz.JobDetailFactoryBean
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean

abstract class SyncJobConfAbstract(
        private val name: String,
        private val jobClass: Class<out Job>,
        private val description: String,
        private val durability: Boolean,
        private val frequencyInSec: Long,
        private val repeatCount: Int
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Bean
    open fun jobDetail(): JobDetailFactoryBean {
        return JobDetailFactoryBean().apply {
            setJobClass(jobClass)
            setName(name)
            setDescription(description)
            setDurability(durability)
        }
    }

    @Bean
    open fun trigger(job: JobDetail): SimpleTriggerFactoryBean {
        logger.info("Configuring trigger to fire every ${frequencyInSec} seconds")

        return SimpleTriggerFactoryBean().apply {
            setJobDetail(job)
            setRepeatInterval(frequencyInSec * 1000)
            setRepeatCount(repeatCount)
            setName("${name}_Trigger")
        }
    }
}