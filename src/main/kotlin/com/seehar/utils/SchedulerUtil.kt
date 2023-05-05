package com.seehar.utils

import org.quartz.Job
import org.quartz.JobBuilder
import org.quartz.Scheduler
import org.quartz.SimpleScheduleBuilder
import org.quartz.TriggerBuilder

class SchedulerUtil {

    companion object {

        /**
         * 增加一个任务调度
         *
         * @param scheduler
         * @param name
         * @param group
         * @param jobClass
         */
        @JvmStatic
        fun addJob(scheduler: Scheduler, name: String, group: String, jobClass: Class<out Job>) {
            val scheduleBuilder = SimpleScheduleBuilder
                .simpleSchedule()
                .withIntervalInSeconds(5)
                .repeatForever()

            addJob(scheduler, name, group, jobClass, scheduleBuilder)
        }

        @JvmStatic
        fun addJob(
            scheduler: Scheduler,
            name: String,
            group: String,
            jobClass: Class<out Job>,
            scheduleBuilder: SimpleScheduleBuilder
        ) {
            val job = JobBuilder.newJob(jobClass)
                .withIdentity(name, group)
                .build()

            val jobTrigger = TriggerBuilder.newTrigger()
                .withIdentity("${name}Trigger", group)
                .startNow()
                .withSchedule(scheduleBuilder)
                .build()
            scheduler.scheduleJob(job, jobTrigger)
        }
    }
}
