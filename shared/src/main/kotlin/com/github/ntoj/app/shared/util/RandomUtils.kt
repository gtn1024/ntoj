package com.github.ntoj.app.shared.util

import cn.hutool.core.util.IdUtil
import java.util.UUID

fun randomString() = UUID.randomUUID().toString().replace("-", "")

fun getSnowflakeId(): String {
    return IdUtil.getSnowflakeNextIdStr()
}
