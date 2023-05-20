package ru.sergo

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBodilessEntity
import java.net.URI
import kotlin.system.measureTimeMillis

private val log = LoggerFactory.getLogger("SpringWebClientSend")

fun testSpringWebClientSend(requestDelayMs: Long, numOfRequests: Int) {
    log.info("Start Test WebClient send with delay $requestDelayMs")
    val client = WebClient.builder().build()
    val request = client
        .get()
        .uri(URI("http://localhost:8080/sync/$requestDelayMs"))
    measureTimeMillis {
        runBlocking {
            repeat(numOfRequests) {
                launch {
                    log.info("Start $it request")
                    val response = request.retrieve().awaitBodilessEntity()
                    log.info("End $it request with response ${response.statusCode}")
                }
            }
        }
    }.let {
        log.info("End Test HttpClient send for $it ms")
    }
}
