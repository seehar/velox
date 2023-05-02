package com.seehar.spider.base

import org.junit.Test
import org.slf4j.LoggerFactory
import kotlin.test.assertNotNull

class BaseRequestTest {
    private val log = LoggerFactory.getLogger(this::class.java)
    private val request = BaseRequest()

    @Test
    fun get() {
        val responseTriple = request.get("https://www.baidu.com")
        val responseBody = responseTriple?.first
        assertNotNull(responseBody)
    }

    @Test
    fun post() {
        val url = "https://httpbin.org/post"
        val responseTriple = request.post(url)
        val responseString = responseTriple?.first
        log.info("response: $responseString")
        assertNotNull(responseString)
    }
}
