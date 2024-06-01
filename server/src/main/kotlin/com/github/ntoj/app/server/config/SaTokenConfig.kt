package com.github.ntoj.app.server.config

import cn.dev33.satoken.context.SaHolder
import cn.dev33.satoken.filter.SaServletFilter
import cn.dev33.satoken.jwt.SaJwtUtil
import cn.dev33.satoken.router.SaHttpMethod
import cn.dev33.satoken.router.SaRouter
import cn.dev33.satoken.stp.StpLogic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SaTokenConfig {
    @Bean
    fun getStpLogicJwt(): StpLogic? {
        return CustomStpLogicJwtForStateless()
    }

    @Autowired
    fun setSaJwtTemplate() {
        SaJwtUtil.setSaJwtTemplate(CustomSaJwtTemplate())
    }

    @Bean
    fun getSaServletFilter(): SaServletFilter {
        return SaServletFilter()
            .addInclude("/**")
            .addExclude("/favicon.ico")
            .setBeforeAuth {
                SaHolder.getResponse()
                    .setHeader("Access-Control-Allow-Origin", "*")
                    .setHeader("Access-Control-Allow-Methods", "GET, POST, PATCH, PUT, DELETE, OPTIONS")
                    .setHeader("Access-Control-Max-Age", "3600")
                    .setHeader("Access-Control-Allow-Headers", "*")

                SaRouter.match(SaHttpMethod.OPTIONS)
                    .free { }
                    .back()
            }
    }
}
