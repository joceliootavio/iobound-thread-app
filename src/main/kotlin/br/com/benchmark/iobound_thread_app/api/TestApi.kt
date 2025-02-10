package br.com.benchmark.iobound_thread_app.api

import br.com.benchmark.iobound_thread_app.adapter.out.feign.FeignWebClient
import br.com.benchmark.iobound_thread_app.adapter.out.http.MockWebClient
import br.com.benchmark.iobound_thread_app.adapter.out.rds.DatabaseService
import kotlinx.coroutines.runBlocking
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.lang.Thread.sleep
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/v1/customers")
class TestApi(
    val databaseService: DatabaseService,
    val webClient: MockWebClient,
    val feignClient: FeignWebClient
) {

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun fetchRecord(@PathVariable("id") id: Int,
                    @RequestParam("suspended") suspend: Boolean? = false) = runBlocking {
        val start = System.currentTimeMillis()
        if (suspend == true)
            databaseService.suspendedFindById(id)
        else
            databaseService.findById(id)
        println("${LocalDateTime.now()} [${Thread.currentThread().name}]: endpoint executado em ${System.currentTimeMillis() - start}ms")
    }

    @GetMapping("/from-api")
    @ResponseStatus(HttpStatus.OK)
    fun getFromAnotherApi(
        @RequestParam("suspended") suspend: Boolean? = false,
        @RequestParam("delay") delay: Long? = null
    ) {
        val start = System.currentTimeMillis()
        if (suspend == true)
            webClient.getRequestSuspended()
        else
            feignClient.getFromApi(delay ?: 1000)
        println("${LocalDateTime.now()} [${Thread.currentThread().name}]: endpoint GET executado em ${System.currentTimeMillis() - start}ms")
    }

    @GetMapping("/mock")
    @ResponseStatus(HttpStatus.OK)
    fun getFromAnotherApi(@RequestParam("delay") delay: Long) {
        val start = System.currentTimeMillis()
        sleep(delay)
        println("${LocalDateTime.now()} [${Thread.currentThread().name}]: endpoint mock executado em ${System.currentTimeMillis() - start}ms")
    }
}