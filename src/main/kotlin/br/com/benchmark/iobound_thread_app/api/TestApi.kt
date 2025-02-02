package br.com.benchmark.iobound_thread_app.api

import br.com.benchmark.iobound_thread_app.adapter.out.rds.DatabaseService
import kotlinx.coroutines.runBlocking
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/v1/customers")
class TestApi(
    val databaseService: DatabaseService
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
}