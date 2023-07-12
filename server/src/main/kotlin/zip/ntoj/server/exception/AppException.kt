package zip.ntoj.server.exception

class AppException(
    override val message: String,
    val code: Int,
) : RuntimeException(message)
