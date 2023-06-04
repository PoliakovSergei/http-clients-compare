package ru.sergo

import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder

@SpringBootApplication
class Client

fun main() {
    val ctx = SpringApplicationBuilder(Client::class.java)
        .web(WebApplicationType.NONE)
        .run()
    val requestDelayMs = 500L
    val numOfRequests = 1000
    println()
    println()
    testHttpClientSend(requestDelayMs, 5)
    println()
    println()
    testHttpClientSendAsync(requestDelayMs, numOfRequests)
    println()
    println()
    testSpringWebClientSend(requestDelayMs, numOfRequests)
    println()
    println()
    testSpringOpenFeignClientSync(requestDelayMs, 5, ctx)
    println()
    println()
    testSpringOpenFeignClientAsync(requestDelayMs, numOfRequests, ctx)
    println()
    println()
    testOkHttpClientWithFeign(requestDelayMs, 5)
    println()
    println()
    testOkHttpClientAsyncWithFeign(requestDelayMs, numOfRequests)
}
