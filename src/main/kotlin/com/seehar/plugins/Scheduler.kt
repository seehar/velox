package com.seehar.plugins

import com.seehar.scheduler.SchedulerJob
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.quartz.JobBuilder
import org.quartz.JobKey
import org.quartz.SimpleScheduleBuilder
import org.quartz.TriggerBuilder
import org.quartz.impl.StdSchedulerFactory
import org.quartz.impl.matchers.GroupMatcher

fun Application.configureScheduler() {
    val scheduler = StdSchedulerFactory().scheduler

    val schedulerJob = JobBuilder.newJob(SchedulerJob::class.java)
        .withIdentity("schedulerJobName", "test")
        .build()

    val schedulerJobTrigger = TriggerBuilder.newTrigger()
        .withIdentity("schedulerJobTrigger", "test")
        .startNow()
        .withSchedule(
            SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInSeconds(5)
                .repeatForever()
        )
        .build()

    scheduler.scheduleJob(schedulerJob, schedulerJobTrigger)
    scheduler.start()


    routing {
        get("/scheduler") {
            val jobGroupNames = scheduler.jobGroupNames
            val jobNames = jobGroupNames.flatMap { groupName ->
                scheduler.getJobKeys(GroupMatcher.groupEquals(groupName))
                    .map { jobKey -> jobKey.name }
            }
            val jobs = jobNames.associateWith { jobName ->
                mapOf(
                    "jobName" to jobName,
                    "jobKey" to JobKey.jobKey(jobName),
                )
            }

            call.respond(mapOf("jobs" to jobs))
        }
    }
}