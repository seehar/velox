package com.seehar.plugins

import com.seehar.scheduler.SchedulerJob
import com.seehar.scheduler.SchedulerPoolJob
import com.seehar.utils.SchedulerUtil
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import org.quartz.JobKey
import org.quartz.impl.StdSchedulerFactory
import org.quartz.impl.matchers.GroupMatcher

fun Application.configureScheduler() {
    val scheduler = StdSchedulerFactory().scheduler
    val taskGroup = "test"
    SchedulerUtil.addJob(scheduler, "SchedulerJob", taskGroup, SchedulerJob::class.java)
    SchedulerUtil.addJob(scheduler, "SchedulerPoolJob", taskGroup, SchedulerPoolJob::class.java)
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
