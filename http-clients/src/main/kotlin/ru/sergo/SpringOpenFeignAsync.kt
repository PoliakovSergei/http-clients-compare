package ru.sergo

import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import kotlin.system.measureTimeMillis


private val log = LoggerFactory.getLogger("SpringOpenFeignAsync")

fun testSpringOpenFeignClientAsync(
    requestDelayMs: Long,
    numOfRequests: Int,
    ctx: ConfigurableApplicationContext,
) {
    val client = ctx.getBean(SpringTestClientForAsync::class.java)
    val service = ctx.getBean(AsyncRequestsExecutor::class.java)
    measureTimeMillis {
        runBlocking {
            repeat(numOfRequests) {
                launch {
                    log.info("Start $it request")
                    val response = service
                        .async(requestDelayMs, client)
                        .await()
                    log.info("End $it request with response ${response.statusCode}")
                }
            }
        }
    }.let {
        log.info("End Test SpringOpenFeignAsync for $it ms")
    }
}

@Configuration
//@EnableFeignClients
@EnableAsync
class SpringOpenFeignAsync {
    @Bean
    fun taskExecutor(): Executor =
        ThreadPoolTaskExecutor().apply {
            corePoolSize = 100
            maxPoolSize = 100
            queueCapacity = 5000
            threadNamePrefix = "Spring-executor-"
            initialize()
        }
}

@Service
@Async
class AsyncRequestsExecutor {

    fun async(delayMs: Long, client: SpringTestClientForAsync): CompletableFuture<ResponseEntity<String>> =
        CompletableFuture.completedFuture(client.sync(delayMs))
}

@FeignClient(name = "spring-test-client-for-async", url = "http://localhost:8080/")
interface SpringTestClientForAsync {
    @GetMapping("/sync/{delayMs}")
    fun sync(@PathVariable delayMs: Long): ResponseEntity<String>
}