package com.seehar.spider.base

import org.junit.Test
import org.slf4j.LoggerFactory
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class BaseRequestTest {
    private val log = LoggerFactory.getLogger(this::class.java)

    @Test
    fun get() {
        val request = BaseRequest()
        val response = request.get("https://www.baidu.com")
        val responseString = response?.body()?.string()
        val responseCode = response?.code() ?: 400
        log.info("response code: $responseCode")
        assertTrue { responseCode == 200 }
        assertNotNull(responseString)
    }
}