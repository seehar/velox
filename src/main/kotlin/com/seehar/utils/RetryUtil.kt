package com.seehar.utils

import org.slf4j.LoggerFactory

/**
 * 重试
 */
abstract class RetryUtil<T> {
    private val log = LoggerFactory.getLogger(this::class.java)

    private var retryTime = DEFAULT_RETRY_TIME

    // 重试的睡眠时间
    private var sleepTime = 0

    fun setSleepTime(sleepTime: Int): RetryUtil<T> {
        require(sleepTime >= 0) { "sleepTime should equal or bigger than 0" }
        this.sleepTime = sleepTime
        return this
    }

    fun setRetryCount(retryCount: Int): RetryUtil<T> {
        require(retryCount > 0) { "retryTime should bigger than 0" }
        this.retryTime = retryCount
        return this
    }

    /**
     * 重试的业务执行代码
     * 失败时请抛出一个异常
     *
     * todo 确定返回的封装类，根据返回结果的状态来判定是否需要重试
     *
     * @return
     */
    @Throws(Exception::class)
    protected abstract fun doBiz(): T

    @Throws(InterruptedException::class)
    fun execute(): T? {
        for (i in 0..retryTime) {
            try {
                return doBiz()
            } catch (e: Exception) {
                log.error("业务执行出现异常，正在 $i/$retryTime 重试，e: $e")
                Thread.sleep(sleepTime.toLong())
            }
        }
        return null
    }

    companion object {
        private const val DEFAULT_RETRY_TIME = 1
    }
}
