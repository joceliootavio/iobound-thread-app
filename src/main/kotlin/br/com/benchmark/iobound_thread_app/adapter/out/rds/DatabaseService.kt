package br.com.benchmark.iobound_thread_app.adapter.out.rds

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class DatabaseService(private val jdbcTemplate: JdbcTemplate) {

    fun findById(id: Int): Map<String, Any>? {
        val sql = "SELECT * FROM public.test_table where id = ?"
        println("${LocalDateTime.now()} [${Thread.currentThread().name}]: executando consulta sql")
        return jdbcTemplate.queryForMap(sql, id)
    }

}