package com.seehar.spider.base

import org.junit.Test
import org.slf4j.LoggerFactory
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class BaseRequestTest {
    private val log = LoggerFactory.getLogger(this::class.java)
    private val request = BaseRequest()

    @Test
    fun get() {
        val response = request.get("https://www.baidu.com")
        val responseString = response?.body()?.string()
        val responseCode = response?.code() ?: 400
        log.info("response code: $responseCode")
        assertTrue { responseCode == 200 }
        assertNotNull(responseString)
    }

    @Test
    fun post() {
        val url = "https://httpbin.org/post"
        val response = request.post(url)
        val responseString = response?.body()?.string()
        log.info("response: $responseString")
        assertTrue { response?.code() == 200}
        assertNotNull(responseString)
    }
}