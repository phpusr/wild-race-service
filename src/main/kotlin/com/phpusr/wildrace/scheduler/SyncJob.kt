package com.phpusr.wildrace.scheduler

import org.quartz.Job
import org.quartz.JobExecutionContext
import org.slf4j.LoggerFactory
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