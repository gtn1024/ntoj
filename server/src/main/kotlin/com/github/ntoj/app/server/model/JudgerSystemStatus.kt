package com.github.ntoj.app.server.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity(name = "t_judger_system_status")
class JudgerSystemStatus(
    @Column(unique = true) var judgerId: String,
    var os: String? = null,
    var kernel: String? = null,
    var memoryUsed: Long? = null,
    var memoryTotal: Long? = null,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,
) : BaseEntity()
