package com.github.ntoj.app.server.config

import cn.dev33.satoken.error.SaErrorCode
import cn.dev33.satoken.exception.NotPermissionException
import cn.dev33.satoken.jwt.StpLogicJwtForStateless

class CustomStpLogicJwtForStateless : StpLogicJwtForStateless() {
    override fun checkPermissionAnd(vararg permissionArray: String) {
        // 先获取当前是哪个账号id
        val loginId: Any = getLoginId(-1L)

        // 如果没有指定权限，那么直接跳过
        if (permissionArray.isEmpty()) {
            return
        }

        // 开始校验
        val permissionList = getPermissionList(loginId)
        for (permission in permissionArray) {
            if (!hasElement(permissionList, permission)) {
                throw NotPermissionException(permission, this.loginType).setCode(SaErrorCode.CODE_11051)
            }
        }
    }
}
