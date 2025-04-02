package br.com.benchmark.iobound_thread_app.adapter.`in`.api

import br.com.benchmark.iobound_thread_app.adapter.`in`.api.response.User
import br.com.benchmark.iobound_thread_app.adapter.out.feign.FeignWebClient
import br.com.benchmark.iobound_thread_app.adapter.out.rds.DatabaseService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/coroutine")
class CoroutineApi(
    val databaseService: DatabaseService,
    val feignClient: FeignWebClient
) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @GetMapping("/from-api/user", produces = ["application/json"])
    @ResponseStatus(HttpStatus.OK)
    suspend fun getFromAnotherApiSuspended(
        @RequestParam("delay") delay: Long,
        @RequestParam("times") times: Long = 1,
        @RequestParam("async") async: Boolean = false
    ): User {
        val start = System.currentTimeMillis()
        return withContext(Dispatchers.IO) {
            (1..times).map {
                async {
                    logger.info("chamando /mock/user")
                    feignClient.getUser(delay)
                }.also {
                    if (!async)
                        it.await()
                }

            }.awaitAll()
                .last()
                .also {
                    logger.info("endpoint /suspended executado em ${System.currentTimeMillis() - start}ms")
                }
        }
    }

}