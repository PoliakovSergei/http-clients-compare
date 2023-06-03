package ru.sergo

import feign.*
import feign.okhttp.OkHttpClient
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture
import kotlin.system.measureTimeMillis

private val log = LoggerFactory.getLogger("OkHttpFeignAsync")

fun testOkHttpClientAsyncWithFeign(requestDelayMs: Long, numOfRequests: Int) {
    log.info("Start Test testOkHttpClientAsyncWithFeign send with delay $requestDelayMs")
    val client = AsyncFeign
        .builder<OkHttpClient>()
        .target(AsyncFeignTestClient::class.java, "http://localhost:8080/")
    measureTimeMillis {
        runBlocking {
            repeat(numOfRequests) {
                launch {
                    log.info("Start $it request")
                    val response = client
                        .async(requestDelayMs)
                        .await()
                    log.info("End $it request with response ${response.status()}")
                }
            }
        }
    }.let {
        log.info("End Test testOkHttpClientAsyncWithFeign sendAsync for $it ms")
    }
}

interface AsyncFeignTestClient {
    @RequestLine("GET /sync/{delayMs}")
    fun async(@Param delayMs: Long): CompletableFuture<Response>
}
