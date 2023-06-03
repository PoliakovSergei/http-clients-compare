package ru.sergo

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Configuration
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import kotlin.system.measureTimeMillis

private val log = LoggerFactory.getLogger("SpringOpenFeign")

fun testSpringOpenFeignClientSync(
    requestDelayMs: Long,
    numOfRequests: Int,
    ctx: ConfigurableApplicationContext,
) {
    val client = ctx.getBean(SpringTestClient::class.java)
    measureTimeMillis {
        runBlocking {
            repeat(numOfRequests) {
                launch {
                    log.info("Start $it request")
                    val response = client
                        .sync(requestDelayMs)
                    log.info("End $it request with response ${response.statusCode}")
                }
            }
        }
    }.let {
        log.info("End Test SpringOpenFeignSync for $it ms")
    }
}

@Configuration
@EnableFeignClients
class SpringOpenFeign

@FeignClient(name = "spring-test-client", url = "http://localhost:8080/")
interface SpringTestClient {
    @GetMapping("/sync/{delayMs}")
    fun sync(@PathVariable delayMs: Long): ResponseEntity<String>
}