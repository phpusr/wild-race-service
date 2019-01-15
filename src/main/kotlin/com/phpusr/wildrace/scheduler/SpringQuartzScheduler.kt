package com.phpusr.wildrace.scheduler

import org.quartz.JobDetail
import org.quartz.Trigger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.scheduling.quartz.SchedulerFactoryBean
import org.springframework.scheduling.quartz.SpringBeanJobFactory
import javax.annotation.PostConstruct


/**
 * src: https://github.com/eugenp/tutorials/tree/master/spring-quartz
 */
@Configuration
@ConditionalOnExpression("'\${using.spring.schedulerFactory}'=='true'")
class SpringQuartzScheduler(private val applicationContext: ApplicationContext) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @PostConstruct
    private fun init() {
        logger.debug("SpringQuartzScheduler init")
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

}