package br.com.benchmark.iobound_thread_app.adapter.out.rds

import br.com.benchmark.iobound_thread_app.adapter.out.rds.entity.CustomerEntity
import br.com.benchmark.iobound_thread_app.adapter.out.rds.mapper.CustomerMapper
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import java.util.*

@Service
class DatabaseService(private val jdbcTemplate: JdbcTemplate) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    fun findById(id: String): CustomerEntity? {
        val userId = if (id == "random") UUID.randomUUID() else UUID.fromString(id)

        val sql = "SELECT * FROM public.customer where id = ?"
        logger.info("executando consulta sql para userId = $userId")
        return jdbcTemplate.query(sql, CustomerMapper(), userId).firstOrNull()
            ?.also {
                logger.info("customer retornado: $it")
            }
    }

}