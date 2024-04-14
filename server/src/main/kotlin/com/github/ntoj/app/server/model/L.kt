package com.github.ntoj.app.server.model

data class L<T>(
    val total: Long,
    val page: Int,
    val list: Collection<T>,
)
