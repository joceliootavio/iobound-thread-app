package br.com.benchmark.iobound_thread_app.application.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class MemoryOpsService {

    val uuidList: List<String> = List(100) { UUID.randomUUID().toString() }

    private val logger = LoggerFactory.getLogger(this.javaClass)

    fun simulateOperation(memoryOpsType: String = "linear") {
        logger.info("memory operation $memoryOpsType - START")

        for (element in uuidList) {
            if (memoryOpsType == "quadratic")
                uuidList.indexOf(element)
                    .run {
                        logger.debug("${if (this != -1) "" else "not "}found element $element at index $this")
                    }
        }

        logger.info("memory operation $memoryOpsType - END")
    }
}
