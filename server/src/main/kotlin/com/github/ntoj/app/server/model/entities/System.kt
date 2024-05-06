package com.github.ntoj.app.server.model.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes.JSON

@Entity(name = "system")
class System(
    @Id
    var id: String,
    @JdbcTypeCode(JSON) @Column(nullable = false) var value: SystemValue,
)

data class SystemValue(
    val value: Any,
)
