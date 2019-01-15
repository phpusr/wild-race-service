package com.phpusr.wildrace.scheduler

import org.quartz.spi.TriggerFiredBundle
import org.springframework.beans.factory.config.AutowireCapableBeanFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.scheduling.quartz.SpringBeanJobFactory


/**
 * Adds auto-wiring support to quartz jobs.
 * @see "https://gist.github.com/jelies/5085593"
 */
class AutoWiringSpringBeanJobFactory : SpringBeanJobFactory(), ApplicationContextAware {

    private lateinit var beanFactory: AutowireCapableBeanFactory

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        beanFactory = applicationContext.autowireCapableBeanFactory
    }

    override fun createJobInstance(bundle: TriggerFiredBundle): Any {
        val job = super.createJobInstance(bundle)
        beanFactory.autowireBean(job)
        return job
    }

}