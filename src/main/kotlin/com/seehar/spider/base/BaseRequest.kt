package com.seehar.spider.base

import com.seehar.utils.RetryUtil
import okhttp3.FormBody
import okhttp3.Headers
import okhttp3.Headers.Companion.toHeaders
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

/**
 * 基础请求
 *
 * @param timeout 请求超时时间，默认 30 秒
 */
class BaseRequest(timeout: Int = 30) {
    private val log = LoggerFactory.getLogger(this::class.java)
    private val client = OkHttpClient().newBuilder()
        .retryOnConnectionFailure(false)
        .connectTimeout(timeout.toLong(), TimeUnit.SECONDS)
        .build()

    /**
     * 包含重试的基础请求
     *
     * @param url
     * @param method
     * @param params
     * @param data
     * @param headers
     * @param retryCount 重试次数
     * @param sleepTime 每次重试等待时间
     * @return
     */
    @Throws(InterruptedException::class)
    fun request(
        url: String,
        method: String = "GET",
        params: Map<String, Any> = mapOf(),
        data: RequestBody? = null,
        headers: Map<String, String>? = null,
        retryCount: Int = 3,
        sleepTime: Int = 3000,
    ): Triple<String?, Headers, Request>? {
        val result = object : RetryUtil<Triple<String?, Headers, Request>>() {
            @Throws(Exception::class)
            override fun doBiz(): Triple<String?, Headers, Request> {
                return doRequest(url = url, method = method, params = params, data = data, headers = headers)
            }
        }
            .setRetryCount(retryCount)
            .setSleepTime(sleepTime)
            .execute()
        log.info("result: $result")
        return result
    }

    /**
     * 基础请求
     *
     * @param url
     * @param params
     * @param method
     * @param data
     * @param headers
     * @return
     */
    fun doRequest(
        url: String,
        params: Map<String, Any> = mapOf(),
        method: String = "GET",
        data: RequestBody? = null,
        headers: Map<String, String>? = null
    ): Triple<String?, Headers, Request> {
        val httpBuilder = url.toHttpUrlOrNull()!!.newBuilder()
        for (param in params) {
            httpBuilder.addQueryParameter(param.key, param.value.toString())
        }

        val currentHeaders: Map<String, String> = headers ?: mapOf(
            "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Chrome/111.0.0.0 Safari/537.36",
        )

        val request = Request.Builder()
            .url(httpBuilder.build())
            .method(method, data)
            .headers(currentHeaders.toHeaders())
            .build()

        var response: Response? = null
        try {
            response = client.newCall(request).execute()
            log.debug("response code: ${response.code}")
            return Triple(response.body?.string(), response.headers, response.request)
        } finally {
            response?.close()
        }
    }

    /**
     * get 请求
     *
     * @param url
     * @param params
     * @param data
     * @param headers
     * @param retryCount
     * @param sleepTime
     * @return
     */
    fun get(
        url: String,
        params: Map<String, Any> = mapOf(),
        data: RequestBody? = null,
        headers: Map<String, String>? = null,
        retryCount: Int = 3,
        sleepTime: Int = 3000,
    ): Triple<String?, Headers, Request>? {
        return request(
            url = url,
            params = params,
            data = data,
            method = "GET",
            headers = headers,
            retryCount = retryCount,
            sleepTime = sleepTime
        )
    }

    /**
     * post 请求
     *
     * @param url
     * @param data
     * @param headers
     * @param retryCount
     * @param sleepTime
     * @return
     */
    fun post(
        url: String,
        data: Map<String, Any> = mapOf(),
        headers: Map<String, String>? = null,
        retryCount: Int = 3,
        sleepTime: Int = 3000,
    ): Triple<String?, Headers, Request>? {
        return request(
            url = url,
            method = "POST",
            data = handleRequestBody(data),
            headers = headers,
            retryCount = retryCount,
            sleepTime = sleepTime
        )
    }

    /**
     * 处理 post 请求的参数
     *
     * @param data
     * @return
     */
    private fun handleRequestBody(data: Map<String, Any>): RequestBody {
        val body = FormBody.Builder()
        for ((key, value) in data) {
            body.add(key, value.toString())
        }
        return body.build()
    }
}
