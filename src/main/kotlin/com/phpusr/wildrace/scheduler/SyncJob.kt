package com.phpusr.wildrace.scheduler

import org.quartz.Job
import org.quartz.JobExecutionContext
import org.quartz.SimpleTrigger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

@Component
class SyncJob : Job {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun execute(context: JobExecutionContext?) {
        logger.info("Job ** {} ** fired @ {}", context?.jobDetail?.key?.name, context?.fireTime)

        logger.info(">> Work!")

        logger.info("Next job scheduled @ {}", context?.nextFireTime)
    }
}

@Configuration
class SyncJobConf : SyncJobConfAbstract(
        name = "Sync_Job",
        jobClass = SyncJob::class.java,
        description = "Invoke Sample Job service...",
        durability = true,
        frequencyInSec = 10,
        repeatCount = SimpleTrigger.REPEAT_INDEFINITELY
)