package com.github.ntoj.app.server.service

import com.github.ntoj.app.server.model.entities.PERM_DEFAULT
import com.github.ntoj.app.server.model.entities.PERM_GUEST
import com.github.ntoj.app.server.model.entities.PERM_ROOT
import com.github.ntoj.app.server.model.entities.PermissionRole
import com.github.ntoj.app.server.repository.PermissionRoleRepository
import com.github.ntoj.app.server.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.math.BigInteger
import kotlin.jvm.optionals.getOrNull

interface PermissionRoleService {
    fun getAll(): List<PermissionRole>

    fun save(permissionRole: PermissionRole): PermissionRole

    fun remove(name: String)

    fun get(name: String): PermissionRole?

    fun getPermission(name: String): BigInteger
}

@Service
class PermissionRoleServiceImpl(
    private val permissionRoleRepository: PermissionRoleRepository,
    private val userRepository: UserRepository,
) : PermissionRoleService {
    override fun getAll(): List<PermissionRole> {
        return permissionRoleRepository.findAll()
    }

    override fun save(permissionRole: PermissionRole): PermissionRole {
        return permissionRoleRepository.save(permissionRole)
    }

    @Transactional
    override fun remove(name: String) {
        if (get(name) == null) {
            return
        }
        if (!isBuiltInRole(name)) {
            userRepository.setDefaultRoleForUsersWithRole(name)
        }
        permissionRoleRepository.deleteById(name)
    }

    override fun get(name: String): PermissionRole? {
        return permissionRoleRepository.findById(name).getOrNull()
    }

    override fun getPermission(name: String): BigInteger {
        val permission =
            when (name) {
                "root" -> PERM_ROOT
                "banned" -> PERM_GUEST
                else -> get(name)?.permission ?: PERM_DEFAULT
            }
        return permission
    }

    private fun isBuiltInRole(name: String): Boolean {
        return name == "root" || name == "default" || name == "guest"
    }
}
