package com.github.ntoj.app.server.model.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity(name = "t_users")
class User(
    @Column(nullable = false, unique = true) var username: String,
    @Column(nullable = false) var password: String,
    @Column(nullable = false) var salt: String,
    @Column(nullable = false) var email: String,
    @Column(name = "display_name") var displayName: String? = null,
    var bio: String? = null,
    @Column(name = "role") var userRole: String = "default",
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    var userId: Long? = null,
) : BaseEntity()
