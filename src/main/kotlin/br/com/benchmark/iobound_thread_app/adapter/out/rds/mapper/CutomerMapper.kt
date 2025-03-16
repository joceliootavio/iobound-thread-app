package br.com.benchmark.iobound_thread_app.adapter.out.rds.mapper

import br.com.benchmark.iobound_thread_app.adapter.out.rds.entity.CustomerEntity
import br.com.benchmark.iobound_thread_app.adapter.out.rds.entity.Status
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet
import java.util.*

class CustomerMapper : RowMapper<CustomerEntity> {
    override fun mapRow(rs: ResultSet, rowNum: Int): CustomerEntity {
        return CustomerEntity(
            id = UUID.fromString(rs.getString("id")),
            name = rs.getString("name"),
            email = rs.getString("email"),
            phone = rs.getString("phone"),
            birthDate = rs.getDate("birth_date")?.toLocalDate(),
            createdAt = rs.getTimestamp("created_at").toLocalDateTime(),
            updatedAt = rs.getTimestamp("updated_at").toLocalDateTime(),
            status = Status.valueOf(rs.getString("status")),
            address = rs.getString("address"),
            city = rs.getString("city")
        )
    }
}
