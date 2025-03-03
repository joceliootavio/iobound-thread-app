package br.com.benchmark.iobound_thread_app.application.service

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class MemoryOpsService(private val meterRegistry: MeterRegistry) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    fun simulateQuadraticOperation() {
        logger.info("memory operation - START")

        val timer = Timer.builder("memory.ops.execution.time")
            .description("Tempo de execução da operação quadrática")
            .register(meterRegistry)

        val timeTaken = timer.recordCallable {
            val numbers = List(100) { it } // Lista de 100 elementos [0, 1, ..., 99]
            var sum = 0

            for (i in numbers.indices) {
                for (j in numbers.indices) {
                    sum += numbers[i] * numbers[j] // Operação O(n^2)
                }
            }
            sum
        }

        logger.info("memory operation - END")
    }
}
