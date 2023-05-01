package com.seehar.scheduler

import org.quartz.DisallowConcurrentExecution
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.slf4j.LoggerFactory

@DisallowConcurrentExecution
class SchedulerJob : Job {
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun execute(context: JobExecutionContext?) {
        log.info("任务正在运行...")
        Thread.sleep(10000)
        log.info("任务执行完成！")
    }
}
