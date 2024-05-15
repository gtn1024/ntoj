package com.github.ntoj.app.server.controller

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import com.github.ntoj.app.server.config.Constant
import com.github.ntoj.app.server.config.InformationConfig
import com.github.ntoj.app.server.ext.success
import com.github.ntoj.app.server.model.entities.PERM_DEFAULT
import com.github.ntoj.app.server.model.entities.PERM_GUEST
import com.github.ntoj.app.server.model.entities.PERM_ROOT
import com.github.ntoj.app.server.service.PermissionRoleService
import com.github.ntoj.app.shared.model.R
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigInteger
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/")
class WelcomeController(
    val informationConfig: InformationConfig,
    val permissionRoleService: PermissionRoleService,
) {
    @RequestMapping("")
    fun welcome(): Map<String, String> {
        return mapOf(
            "message" to "Welcome to NTOJ Server!",
            "serverTime" to
                LocalDateTime.now(ZoneId.of("Asia/Shanghai")).format(
                    DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"),
                ),
            "version" to Constant.VERSION,
        )
    }

    @GetMapping("/info")
    fun info(): ResponseEntity<R<InformationConfig>> {
        return R.success(200, "获取成功", informationConfig)
    }

    @GetMapping("/roles")
    fun roles(): ResponseEntity<R<List<PermissionRole>>> {
        val rolesMap = permissionRoleService.getAll().associate { it.name to it.permission }.toMutableMap()
        rolesMap["root"] = PERM_ROOT
        if (rolesMap["default"] == null) {
            rolesMap["default"] = PERM_DEFAULT
        }
        if (rolesMap["guest"] == null) {
            rolesMap["guest"] = PERM_GUEST
        }

        val list = rolesMap.map { PermissionRole(it.key, it.value) }

        return R.success(200, "获取成功", list)
    }

    data class PermissionRole(
        val name: String,
        @field:JsonSerialize(using = ToStringSerializer::class) val permission: BigInteger,
    )
}
