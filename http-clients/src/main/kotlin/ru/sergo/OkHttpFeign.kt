package ru.sergo

import feign.*
import feign.okhttp.OkHttpClient
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import kotlin.system.measureTimeMillis

private val log = LoggerFactory.getLogger("OkHttpFeign")

fun testOkHttpClientWithFeign(requestDelayMs: Long, numOfRequests: Int) {
    log.info("Start Test testOkHttpClientWithFeign send with delay $requestDelayMs")
    val client = Feign
        .builder()
        .client(OkHttpClient())
        .target(FeignTestClient::class.java, "http://localhost:8080/")
    measureTimeMillis {
        runBlocking {
            repeat(numOfRequests) {
                launch {
                    log.info("Start $it request")
                    val response = client.sync(requestDelayMs)
                    log.info("End $it request with response ${response.status()}")
                }
            }
        }
    }.let {
        log.info("End Test testOkHttpClientWithFeign sendAsync for $it ms")
    }
}

interface FeignTestClient {
    @RequestLine("GET /sync/{delayMs}")
    fun sync(@Param delayMs: Long): Response
}
