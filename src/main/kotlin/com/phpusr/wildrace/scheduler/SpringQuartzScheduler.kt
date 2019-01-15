package com.phpusr.wildrace.scheduler

import org.quartz.JobDetail
import org.quartz.SimpleTrigger
import org.quartz.Trigger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.scheduling.quartz.JobDetailFactoryBean
import org.springframework.scheduling.quartz.SchedulerFactoryBean
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean
import org.springframework.scheduling.quartz.SpringBeanJobFactory
import javax.annotation.PostConstruct


@Configuration
@ConditionalOnExpression("'\${using.spring.schedulerFactory}'=='true'")
class SpringQuartzScheduler(private val applicationContext: ApplicationContext) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @PostConstruct
    private fun init() {
        logger.info("Hello world from Spring...")
    }

    @Bean
    fun springBeanJobFactory(): SpringBeanJobFactory {
        logger.debug("Configuring Job factory")
        return AutoWiringSpringBeanJobFactory().apply {
            setApplicationContext(applicationContext)
        }
    }

    @Bean
    fun scheduler(trigger: Trigger, job: JobDetail): SchedulerFactoryBean {
        logger.debug("Setting the Scheduler up")
        return SchedulerFactoryBean().apply {
            setConfigLocation(ClassPathResource("quartz.properties"))
            setJobFactory(springBeanJobFactory())
            setJobDetails(job)
            setTriggers(trigger)
        }
    }

    @Bean
    fun jobDetail(): JobDetailFactoryBean {
        return JobDetailFactoryBean().apply {
            setJobClass(SyncJob::class.java)
            setName("Qrtz_Job_Detail")
            setDescription("Invoke Sample Job service...")
            setDurability(true)
        }
    }

    @Bean
    fun trigger(job: JobDetail): SimpleTriggerFactoryBean {
        val frequencyInSec = 10L
        logger.info("Configuring trigger to fire every {} seconds", frequencyInSec)

        return SimpleTriggerFactoryBean().apply {
            setJobDetail(job)
            setRepeatInterval(frequencyInSec * 1000)
            setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY)
            setName("Qrtz_Trigger")
        }
    }
}