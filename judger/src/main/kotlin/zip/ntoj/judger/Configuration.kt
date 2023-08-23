package zip.ntoj.judger

import zip.ntoj.shared.util.randomString

object Configuration {
    val SERVER_HOST = System.getenv("SERVER_HOST") ?: "http://127.0.0.1:18080"
    val SANDBOX_SERVER = System.getenv("SANDBOX_SERVER") ?: "http://127.0.0.1:5050"
    val TOKEN = System.getenv("TOKEN") ?: throw IllegalStateException("TOKEN is not set")
    val JUDGER_ID = System.getenv("JUDGER_ID") ?: randomString()
}
