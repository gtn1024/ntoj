package zip.ntoj.server.model

import org.springframework.http.ResponseEntity

data class R<T>(
    val code: Int,
    val message: String,
    val data: T? = null,
    val uuid: String? = null,
) {
    companion object {
        fun <T> success(code: Int, message: String, data: T? = null): ResponseEntity<R<T>> {
            return ResponseEntity.status(code).body(R(code, message, data))
        }

        fun fail(code: Int, message: String, requestId: String? = null): ResponseEntity<R<Void>> {
            return ResponseEntity.status(code).body(R(code, message, null, requestId))
        }
    }
}
