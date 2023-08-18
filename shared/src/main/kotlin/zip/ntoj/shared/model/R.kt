package zip.ntoj.shared.model

data class R<T>(
    val code: Int,
    val message: String,
    val data: T? = null,
    val uuid: String? = null,
) {
    companion object
}
