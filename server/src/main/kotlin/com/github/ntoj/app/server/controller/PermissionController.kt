package com.github.ntoj.app.server.controller

import cn.dev33.satoken.annotation.SaCheckPermission
import com.github.ntoj.app.server.ext.success
import com.github.ntoj.app.server.model.entities.PERM_DEFAULT
import com.github.ntoj.app.server.model.entities.PERM_GUEST
import com.github.ntoj.app.server.model.entities.PermissionRole
import com.github.ntoj.app.server.service.PermissionRoleService
import com.github.ntoj.app.shared.model.R
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.math.BigInteger

@RestController
@RequestMapping("/permission")
class PermissionController(
    private val permissionRoleService: PermissionRoleService,
) {
    @PostMapping
    @SaCheckPermission(value = ["PERM_SET_PERM"])
    fun create(
        @RequestParam name: String,
    ): ResponseEntity<R<Unit>> {
        val permissionRole = PermissionRole(name, PERM_DEFAULT)
        permissionRoleService.save(permissionRole)
        return R.success(200, "创建成功")
    }

    @DeleteMapping
    @SaCheckPermission(value = ["PERM_SET_PERM"])
    fun delete(
        @RequestParam name: String,
    ): ResponseEntity<R<Unit>> {
        permissionRoleService.remove(name)
        return R.success(200, "删除成功")
    }

    @PatchMapping
    @SaCheckPermission(value = ["PERM_SET_PERM"])
    fun update(
        @RequestBody permissions: Map<String, String>,
    ): ResponseEntity<R<Unit>> {
        for (permission in permissions) {
            val p = BigInteger(permission.value)
            val newRole = PermissionRole(permission.key, p)
            if (permission.key == "root") {
                continue
            } else if (permission.key == "default" && p == PERM_DEFAULT) {
                permissionRoleService.remove("default")
            } else if (permission.key == "guest" && p == PERM_GUEST) {
                permissionRoleService.remove("guest")
            } else {
                permissionRoleService.save(newRole)
            }
        }
        return R.success(200, "更新成功", null)
    }
}
