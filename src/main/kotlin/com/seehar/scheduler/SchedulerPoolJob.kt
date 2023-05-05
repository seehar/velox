package com.seehar.scheduler

import org.quartz.DisallowConcurrentExecution
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.slf4j.LoggerFactory
import java.util.concurrent.ScheduledThreadPoolExecutor

@DisallowConcurrentExecution
class SchedulerPoolJob : Job {
    private val log = LoggerFactory.getLogger(this::class.java)

    companion object {
        val pool = ScheduledThreadPoolExecutor(10)
        private var taskCount = 0
    }

    override fun execute(context: JobExecutionContext?) {
        run()
    }

    private fun run() {
        while (true) {
            if (pool.taskCount - pool.completedTaskCount > 20) {
                Thread.sleep(1000)
                log.info("任务充足")
                continue
            }
            log.info("任务不足，需要补充")
            if (taskCount < 2) {
                log.info("正在补充任务 $taskCount")
                (1..50).forEach { pool.submit { task(it) } }
                taskCount++
            } else {
                log.info("任务执行完成 $taskCount")
                break
            }
        }
    }

    private fun task(num: Int) {
        Thread.sleep(1000)
        log.info("task: $num")
    }
}
