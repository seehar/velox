package com.seehar.spider

import com.seehar.utils.RetryUtil
import org.slf4j.LoggerFactory

class Spider {
    private val log = LoggerFactory.getLogger(this::class.java)

    @Throws(InterruptedException::class)
    fun request() {
        val ans = object : RetryUtil() {
            @Throws(Exception::class)
            override fun doBiz(): Any {
                return doSomething()
            }
        }
            .setRetryTime(10)
            .setSleepTime(3000)
            .execute()
        log.info("ans: $ans")
    }

    fun doSomething(): Int {
        val temp = (Math.random() * 10).toInt()
        log.info("temp: $temp")
        if (temp > 3) {
            throw Exception("generate value bigger then 3! need retry")
        }
        return temp
    }
}


fun main() {
    Spider().request()
}
