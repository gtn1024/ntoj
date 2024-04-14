package com.github.ntoj.app.server.model

import jakarta.persistence.MappedSuperclass
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import java.time.Instant

@MappedSuperclass
open class BaseEntity(
    var createdAt: Instant? = null,
    var updatedAt: Instant? = null,
) {
    @PrePersist
    fun prePersist() {
        val t = Instant.now()
        createdAt = t
        updatedAt = t
    }

    @PreUpdate
    fun preUpdate() {
        updatedAt = Instant.now()
    }
}
