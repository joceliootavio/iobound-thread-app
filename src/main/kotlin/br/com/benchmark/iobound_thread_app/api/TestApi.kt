package br.com.benchmark.iobound_thread_app.api

import br.com.benchmark.iobound_thread_app.adapter.out.feign.FeignWebClient
import br.com.benchmark.iobound_thread_app.adapter.out.http.MockWebClient
import br.com.benchmark.iobound_thread_app.adapter.out.rds.DatabaseService
import kotlinx.coroutines.runBlocking
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.io.InputStreamReader
import java.lang.Thread.sleep
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/v1/customers")
class TestApi(
    val databaseService: DatabaseService,
    val feignClient: FeignWebClient,
    val resourceLoader: ResourceLoader
) {

    private val resourceMap: MutableMap<String, String> = mutableMapOf()

    @GetMapping("/{id}")
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
        println("${LocalDateTime.now()} [${Thread.currentThread().name}]: endpoint executado em ${System.currentTimeMillis() - start}ms")
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
                println("${LocalDateTime.now()} [${Thread.currentThread().name}]: endpoint GET executado em ${System.currentTimeMillis() - start}ms")
            }
    }

    @GetMapping("/mock", produces = ["application/json"])
    @ResponseStatus(HttpStatus.OK)
    fun getFromAnotherApi(
        @RequestParam("delay") delay: Long,
        @RequestParam("jsonFileName") jsonFileName: String?
    ): String {
        val start = System.currentTimeMillis()
        sleep(delay)

        val response: String?
        if (jsonFileName == null) {
            response = ""
        } else {
            val mapKey = jsonFileName
            if (resourceMap.containsKey(mapKey))
                response = resourceMap[mapKey]
            else {
                val resource: Resource = resourceLoader.getResource("classpath:static/$mapKey")
                response = InputStreamReader(resource.inputStream).readText()
                resourceMap[mapKey] = response
            }
        }

        return response!!
            .also {
                println("${LocalDateTime.now()} [${Thread.currentThread().name}]: endpoint mock executado em ${System.currentTimeMillis() - start}ms")
            }
    }
}