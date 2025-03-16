package br.com.benchmark.iobound_thread_app.api

import br.com.benchmark.iobound_thread_app.adapter.out.feign.FeignWebClient
import br.com.benchmark.iobound_thread_app.adapter.out.rds.DatabaseService
import br.com.benchmark.iobound_thread_app.adapter.out.webclient.WebClientAdapter
import br.com.benchmark.iobound_thread_app.api.response.User
import br.com.benchmark.iobound_thread_app.application.service.MemoryOpsService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future

@RestController
@RequestMapping("/api/reactive")
class ReactiveApi(
    val webClientAdapter: WebClientAdapter,
) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @GetMapping("/from-api/user", produces = ["application/json"])
    @ResponseStatus(HttpStatus.OK)
    fun getFromAnotherApiUser(
        @RequestParam("delay") delay: Long?,
        @RequestParam("times") times: Long = 0,
    ): Mono<User?> {
        val start = System.currentTimeMillis()

        return Mono.zip(
            (0 until times).map {
                logger.info("chamando $it mock/user async")
                webClientAdapter.getUser(delay)
            }
        ) { results ->
            results.firstOrNull() as User
        }.doOnSuccess {
            logger.info("endpoint /user executado async em ${System.currentTimeMillis() - start}ms")
        }
    }

}