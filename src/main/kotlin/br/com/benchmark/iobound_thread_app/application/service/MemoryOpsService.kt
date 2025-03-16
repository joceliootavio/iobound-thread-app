package br.com.benchmark.iobound_thread_app.application.service

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class MemoryOpsService(private val meterRegistry: MeterRegistry) {

    val uuidList: List<String> = List(100) { UUID.randomUUID().toString() }

    private val logger = LoggerFactory.getLogger(this.javaClass)

    fun simulateQuadraticOperation() {
        logger.info("memory operation - START")

        val timer = Timer.builder("memory.ops.execution.time")
            .description("Tempo de execução da operação quadrática")
            .register(meterRegistry)

        val timeTaken = timer.recordCallable {
            for (i in uuidList.indices) {
                val concatened = uuidList[i] + UUID.randomUUID().toString()
//                for (j in uuidList.indices) {
//                    val concatened = uuidList[i] + uuidList[j] // Operação O(n^2)
//                }
            }
        }

        logger.info("memory operation - END")
    }
}
