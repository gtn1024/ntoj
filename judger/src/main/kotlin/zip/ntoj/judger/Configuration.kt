package zip.ntoj.judger

import zip.ntoj.shared.util.randomString

object Configuration {
    /** 服务端地址 */
    val SERVER_HOST = System.getenv("SERVER_HOST") ?: "http://127.0.0.1:18080"

    /** 沙箱地址 */
    val SANDBOX_SERVER = System.getenv("SANDBOX_SERVER") ?: "http://127.0.0.1:5050"

    /** 评测机与服务端通信的 Token */
    val TOKEN = System.getenv("TOKEN") ?: throw IllegalStateException("TOKEN is not set")

    /** 评测机 ID，默认为随机字符串 */
    val JUDGER_ID = System.getenv("JUDGER_ID") ?: randomString()
}
