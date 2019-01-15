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
        val jobFactory = AutoWiringSpringBeanJobFactory()
        logger.debug("Configuring Job factory")
        jobFactory.setApplicationContext(applicationContext)
        return jobFactory
    }

    @Bean
    fun scheduler(trigger: Trigger, job: JobDetail): SchedulerFactoryBean {
        val schedulerFactory = SchedulerFactoryBean()
        schedulerFactory.setConfigLocation(ClassPathResource("quartz.properties"))

        logger.debug("Setting the Scheduler up")
        schedulerFactory.setJobFactory(springBeanJobFactory())
        schedulerFactory.setJobDetails(job)
        schedulerFactory.setTriggers(trigger)

        return schedulerFactory
    }

    @Bean
    fun jobDetail(): JobDetailFactoryBean {
        val jobDetailFactory = JobDetailFactoryBean()
        jobDetailFactory.setJobClass(SyncJob::class.java)
        jobDetailFactory.setName("Qrtz_Job_Detail")
        jobDetailFactory.setDescription("Invoke Sample Job service...")
        jobDetailFactory.setDurability(true)
        return jobDetailFactory
    }

    @Bean
    fun trigger(job: JobDetail): SimpleTriggerFactoryBean {
        val trigger = SimpleTriggerFactoryBean()
        trigger.setJobDetail(job)

        val frequencyInSec = 10L
        logger.info("Configuring trigger to fire every {} seconds", frequencyInSec)

        trigger.setRepeatInterval(frequencyInSec * 1000)
        trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY)
        trigger.setName("Qrtz_Trigger")
        return trigger
    }
}