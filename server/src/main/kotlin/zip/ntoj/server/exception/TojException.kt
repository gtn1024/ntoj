package zip.ntoj.server.exception

class TojException(
    override val message: String,
    val code: Int,
) : RuntimeException(message)
