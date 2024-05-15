package com.github.ntoj.app.server.repository

import com.github.ntoj.app.server.model.entities.PermissionRole
import org.springframework.data.jpa.repository.JpaRepository

interface PermissionRoleRepository : JpaRepository<PermissionRole, String>
