package com.seehar.spider.base

import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.slf4j.LoggerFactory
import kotlin.test.assertEquals

class BaseSpiderKtorTest {
    private val log = LoggerFactory.getLogger(this::class.java)

    @Test
    fun get() {
        val url = "https://httpbin.org/get"
        val params = mapOf("a" to "1")
        val baseSpiderKtor = BaseSpiderKtor()
        runBlocking {
            val response = baseSpiderKtor.get(url, params)
            log.debug("response: ${response.bodyAsText()}")
            assertEquals(response.status, HttpStatusCode.OK)
        }
        baseSpiderKtor.close()
    }

    @Test
    fun post() {
        val url = "https://httpbin.org/post"
        val data = mapOf("a" to "1")
        val baseSpiderKtor = BaseSpiderKtor()
        runBlocking {
            val response = baseSpiderKtor.post(url, data)
            log.debug("response: ${response.bodyAsText()}")
            assertEquals(response.status, HttpStatusCode.OK)
        }
        baseSpiderKtor.close()
    }
}
