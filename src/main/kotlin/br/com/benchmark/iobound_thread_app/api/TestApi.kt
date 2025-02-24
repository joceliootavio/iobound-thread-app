package br.com.benchmark.iobound_thread_app.api

import br.com.benchmark.iobound_thread_app.adapter.out.feign.FeignWebClient
import br.com.benchmark.iobound_thread_app.adapter.out.http.MockWebClient
import br.com.benchmark.iobound_thread_app.adapter.out.rds.DatabaseService
import br.com.benchmark.iobound_thread_app.api.response.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.io.InputStreamReader
import java.lang.Thread.sleep
import java.time.LocalDateTime
import kotlin.math.log

@RestController
@RequestMapping("/api/v1/customers")
class TestApi(
    val databaseService: DatabaseService,
    val feignClient: FeignWebClient
) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @GetMapping("/from-rds/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun fetchRecord(
        @PathVariable("id") id: Int,
        @RequestParam("suspended") suspend: Boolean? = false
    ) = runBlocking {
        val start = System.currentTimeMillis()
        if (suspend == true)
            databaseService.suspendedFindById(id)
        else
            databaseService.findById(id)
        logger.info("endpoint from-rds executado em ${System.currentTimeMillis() - start}ms")
    }

    @GetMapping("/from-api", produces = ["application/json"])
    @ResponseStatus(HttpStatus.OK)
    fun getFromAnotherApi(
        @RequestParam("delay") delay: Long,
        @RequestParam("suspended") suspend: Boolean? = false,
        @RequestParam("jsonFileName") jsonFileName: String?
    ): String {
        val start = System.currentTimeMillis()
        return feignClient.getFromApi(delay, jsonFileName)
            .also {
                logger.info("endpoint /from-api executado em ${System.currentTimeMillis() - start}ms")
            }
    }

    @GetMapping("/from-api/user", produces = ["application/json"])
    @ResponseStatus(HttpStatus.OK)
    fun getFromAnotherApiUser(
        @RequestParam("delay") delay: Long,
        @RequestParam("times") times: Long = 1,
    ): User {
        val start = System.currentTimeMillis()
        return feignClient.getUser(delay)
            .also {
                if (times > 1)
                    for(i in 2..times) {
                        logger.info("chamando $i mock/user")
                        feignClient.getUser(delay)
                    }
                logger.info("endpoint /user executado em ${System.currentTimeMillis() - start}ms")
            }
    }

    @GetMapping("/from-api-suspended", produces = ["application/json"])
    @ResponseStatus(HttpStatus.OK)
    suspend fun getFromAnotherApiSuspended(
        @RequestParam("delay") delay: Long
    ): User {
        val start = System.currentTimeMillis()
        return withContext(Dispatchers.IO) {
            feignClient.getUser(delay)
                .also {
                    logger.info("endpoint /suspended executado em ${System.currentTimeMillis() - start}ms")
                }
        }
    }

}