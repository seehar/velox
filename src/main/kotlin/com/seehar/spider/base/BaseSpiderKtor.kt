package com.seehar.spider.base

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

class BaseSpiderKtor {
    private val log = LoggerFactory.getLogger(this::class.java)
    private val client = HttpClient(CIO) {
        install(HttpTimeout) {
            requestTimeoutMillis = 30000
        }
        install(HttpCookies)
    }

    suspend fun get(url: String, params: Map<String, String>? = null): HttpResponse {
        val response: HttpResponse = client.get(url) {
            url {
                params?.forEach { (key, value) ->
                    run {
                        parameters.append(key, value)
                    }
                }
            }
            headers {
                append(HttpHeaders.UserAgent, getUserAgent())
            }
        }
        return response
    }

    suspend fun post(url: String, data: Map<String, String>? = null): HttpResponse {
        log.info("ContentType.Application.Json.toString(): ${ContentType.Application.Json}")
        val response: HttpResponse = client.post(url) {
            contentType(ContentType.Application.Json)
            if (!data.isNullOrEmpty()) {
                setBody(Json.encodeToString(data))
            }
            headers {
                append(HttpHeaders.UserAgent, getUserAgent())
                // append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            }
        }
        return response
    }

    private fun getUserAgent() = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) " +
            "Chrome/111.0.0.0 Safari/537.36"

    fun close() {
        client.close()
    }
}
