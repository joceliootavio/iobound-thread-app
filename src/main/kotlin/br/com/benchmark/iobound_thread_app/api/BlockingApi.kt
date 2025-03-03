package br.com.benchmark.iobound_thread_app.api

import br.com.benchmark.iobound_thread_app.adapter.out.feign.FeignWebClient
import br.com.benchmark.iobound_thread_app.adapter.out.rds.DatabaseService
import br.com.benchmark.iobound_thread_app.api.response.User
import br.com.benchmark.iobound_thread_app.application.service.MemoryOpsService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.concurrent.Executors
import java.util.concurrent.Future

@RestController
@RequestMapping("/api/blocking")
class BlockingApi(
    val databaseService: DatabaseService,
    val feignClient: FeignWebClient,
    val memoryOpsService: MemoryOpsService
) {

    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val executor = Executors.newVirtualThreadPerTaskExecutor()

    @GetMapping("/from-rds/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun fetchRecord(
        @PathVariable("id") id: Int
    ) {
        val start = System.currentTimeMillis()
        databaseService.findById(id)
        logger.info("endpoint from-rds executado em ${System.currentTimeMillis() - start}ms")
    }

    @GetMapping("/from-api", produces = ["application/json"])
    @ResponseStatus(HttpStatus.OK)
    fun getFromAnotherApi(
        @RequestParam("delay") delay: Long,
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
        @RequestParam("userFromRds") userId: Int? = null,
        @RequestParam("memoryOps") memoryOps: Boolean = false,
        @RequestParam("times") times: Long = 1,
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
                futureList.add(executor.submit<User> {
                    logger.info("chamando $it mock/user async")
                    feignClient.getUser(delay)
                })
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
                }
        }
    }

}