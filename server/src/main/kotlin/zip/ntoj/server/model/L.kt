package zip.ntoj.server.model

data class L<T>(
    val total: Long,
    val page: Int,
    val list: Collection<T>,
)
