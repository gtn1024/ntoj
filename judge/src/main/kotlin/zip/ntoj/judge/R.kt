package zip.ntoj.judge

data class R<T>(
    val code: Int,
    val message: String,
    val data: T? = null,
    val uuid: String? = null,
)
