package com.seehar.spider.base
import com.seehar.utils.RetryUtil
import okhttp3.*
import org.slf4j.LoggerFactory
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.concurrent.TimeUnit

/**
 * 基础请求
 *
 * @param timeout 请求超时时间，默认 30 秒
 */
class BaseRequest(timeout: Int = 30) {
    private val log = LoggerFactory.getLogger(this::class.java)
    private val client = OkHttpClient().newBuilder()
        .proxy(Proxy(Proxy.Type.HTTP, InetSocketAddress("192.168.1.100", 8080)))
        .retryOnConnectionFailure(false)
        .connectTimeout(timeout.toLong(), TimeUnit.SECONDS)
        .build()

    /**
     * 包含重试的基础请求
     *
     * @param url
     * @param method
     * @param params
     * @param body
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
        body: RequestBody? = null,
        headers: Map<String, String>? = null,
        retryCount: Int = 3,
        sleepTime: Int = 3000,
    ): Response? {
        val result = object : RetryUtil<Response>() {
            @Throws(Exception::class)
            override fun doBiz(): Response {
                return doRequest(url = url, method = method, params = params, body = body, headers = headers)
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
     * @param body
     * @param headers
     * @return
     */
    fun doRequest(
        url: String,
        params: Map<String, Any> = mapOf(),
        method: String = "GET",
        body: RequestBody? = null,
        headers: Map<String, String>? = null
    ): Response {
        val httpBuilder = HttpUrl.parse(url)!!.newBuilder()
        for (param in params) {
            httpBuilder.addQueryParameter(param.key, param.value.toString())
        }

        val currentHeaders: Map<String, String> = headers
            ?: mapOf(
                "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/111.0.0.0 Safari/537.36",
            )

        val request = Request.Builder()
            .url(httpBuilder.build())
            .method(method, body)
            .headers(Headers.of(currentHeaders))
            .build()

        val response = client.newCall(request).execute()
        log.debug("response code: ${response.code()}")
        return response
    }

    /**
     * get 请求
     *
     * @param url
     * @param params
     * @param body
     * @param headers
     * @param retryCount
     * @param sleepTime
     * @return
     */
    fun get(
        url: String,
        params: Map<String, Any> = mapOf(),
        body: RequestBody? = null,
        headers: Map<String, String>? = null,
        retryCount: Int = 3,
        sleepTime: Int = 3000,
    ): Response? {
        return request(
            url = url,
            params = params,
            method = "GET",
            body = body,
            headers = headers,
            retryCount = retryCount,
            sleepTime = sleepTime
        )
    }

    /**
     * post 请求
     *
     * @param url
     * @param body
     * @param headers
     * @param retryCount
     * @param sleepTime
     * @return
     */
    fun post(
        url: String,
        body: RequestBody? = null,
        headers: Map<String, String>? = null,
        retryCount: Int = 3,
        sleepTime: Int = 3000,
    ): Response? {
        return request(
            url = url,
            method = "POST",
            body = body,
            headers = headers,
            retryCount = retryCount,
            sleepTime = sleepTime
        )
    }
}