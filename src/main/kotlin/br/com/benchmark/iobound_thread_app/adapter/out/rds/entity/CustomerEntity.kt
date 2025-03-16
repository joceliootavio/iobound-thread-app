package br.com.benchmark.iobound_thread_app.adapter.out.rds.entity

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

data class CustomerEntity(
    val id: UUID,
    val name: String,
    val email: String,
    val phone: String?,
    val birthDate: LocalDate?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val status: Status,
    val address: String?,
    val city: String?
)

enum class Status {
    ACTIVE, INACTIVE
}
