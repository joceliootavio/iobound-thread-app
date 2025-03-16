package br.com.benchmark.iobound_thread_app.api

import br.com.benchmark.iobound_thread_app.adapter.out.feign.FeignWebClient
import br.com.benchmark.iobound_thread_app.adapter.out.rds.DatabaseService
import br.com.benchmark.iobound_thread_app.adapter.out.rds.entity.CustomerEntity
import br.com.benchmark.iobound_thread_app.api.response.User
import br.com.benchmark.iobound_thread_app.application.service.MemoryOpsService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future

@RestController
@RequestMapping("/api/blocking")
class BlockingApi(
    val databaseService: DatabaseService,
    val feignClient: FeignWebClient,
    val memoryOpsService: MemoryOpsService,
    @Autowired @Qualifier("myThreadPool")
    val executor: ExecutorService
) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @GetMapping("/from-rds/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun fetchRecord(
        @PathVariable("id") id: String
    ): CustomerEntity? {
        val start = System.currentTimeMillis()
        return databaseService.findById(id)
            .also {
                logger.info("endpoint from-rds executado em ${System.currentTimeMillis() - start}ms")
            }
    }

    @GetMapping("/from-api/user", produces = ["application/json"])
    @ResponseStatus(HttpStatus.OK)
    fun getFromAnotherApiUser(
        @RequestParam("delay") delay: Long?,
        @RequestParam("userFromRds") userId: String? = null,
        @RequestParam("memoryOps") memoryOps: Boolean = false,
        @RequestParam("times") times: Long = 0,
        @RequestParam("async") async: Boolean = false,
    ): User? {
        val start = System.currentTimeMillis()

        userId?.let {
            logger.info("finding user from RDS - START")
            databaseService.findById(userId)
            logger.info("finding user from RDS - END")
        }

        if (memoryOps)
            memoryOpsService.simulateQuadraticOperation()

        if (async) {
            val futureList: MutableList<Future<User>> = mutableListOf()
            repeat(times.toInt()) {
                futureList.add(
                    CompletableFuture.supplyAsync(
                        {
                            logger.info("chamando $it mock/user async")
                            feignClient.getUser(delay)
                        },
                        executor
                    )
                )
            }

            return futureList.map { it.get() }
                .first()
                .also {
                    logger.info("endpoint /user executado async em ${System.currentTimeMillis() - start}ms")
                }
        } else {
            var result: User? = null
            repeat(times.toInt()) {
                logger.info("chamando $it mock/user")
                result = feignClient.getUser(delay)
            }

            return result
                .also {
                    logger.info("endpoint /user executado em ${System.currentTimeMillis() - start}ms")
                    logger.debug("payload retornado: {}", result)
                }
        }
    }

}