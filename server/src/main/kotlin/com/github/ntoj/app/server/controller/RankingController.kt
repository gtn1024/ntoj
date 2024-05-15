package com.github.ntoj.app.server.controller

import cn.dev33.satoken.annotation.SaCheckPermission
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/ranking")
@SaCheckPermission(value = ["PERM_VIEW"])
class RankingController {
    @GetMapping
    fun getRanking() {
        TODO("Not yet implemented")
    }
}
