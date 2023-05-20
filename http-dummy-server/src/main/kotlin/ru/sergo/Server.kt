package ru.sergo

import kotlinx.coroutines.delay
import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class Server

fun main() {
    SpringApplication.run(Server::class.java)
}

@RestController
class DummyDelayController {

    private val log = LoggerFactory.getLogger(DummyDelayController::class.java)

    @GetMapping("/sync/{ms}")
    suspend fun delayedSyncGet(@PathVariable ms: Long) {
        log.info("Старт с задержкой $ms ms")
        delay(ms)
        log.info("Завершение с задержкой $ms ms")
    }
}