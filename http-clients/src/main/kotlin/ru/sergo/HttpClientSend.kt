package ru.sergo

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import kotlin.system.measureTimeMillis

private val log = LoggerFactory.getLogger("HttpClientSend")

fun testHttpClientSend(requestDelayMs: Long, numOfRequests: Int) {
    log.info("Start Test HttpClient send with delay $requestDelayMs")
    val client = HttpClient.newBuilder().build()
    val request = HttpRequest
        .newBuilder(URI("http://localhost:8080/sync/$requestDelayMs"))
        .GET()
        .build()
    measureTimeMillis {
        runBlocking {
            repeat(numOfRequests) {
                launch {
                    log.info("Start $it request")
                    val response = client
                        .send(request, HttpResponse.BodyHandlers.discarding())
                    log.info("End $it request with response ${response.statusCode()}")
                }
            }
        }
    }.let {
        log.info("End Test HttpClient send for $it ms")
    }
}
