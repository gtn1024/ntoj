package com.github.ntoj.app.judger

import com.github.ntoj.app.shared.util.randomString
import java.io.File
import java.util.ResourceBundle

object Configuration {
    /** 评测程序版本 */
    val VERSION: String = ResourceBundle.getBundle("version").getString("version").trim()

    /** 服务端地址 */
    val SERVER_HOST = System.getenv("SERVER_HOST") ?: "http://127.0.0.1:18080"

    /** 沙箱地址 */
    val SANDBOX_SERVER = System.getenv("SANDBOX_SERVER") ?: "http://127.0.0.1:5050"

    /** 评测机与服务端通信的 Token */
    val TOKEN = System.getenv("TOKEN") ?: throw IllegalStateException("TOKEN is not set")

    /** 评测机 ID */
    val JUDGER_ID = getJudgerId()

    /** 评测线程数 */
    val THREAD_COUNT = System.getenv("THREAD_COUNT")?.toInt() ?: 5

    val OS: String = System.getProperty("os.name")
    val KERNEL: String = System.getProperty("os.version")

    fun memoryUsed(): Long {
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
    }

    fun memoryTotal(): Long {
        return Runtime.getRuntime().totalMemory()
    }

    private fun getJudgerId(): String {
        val file = File(".judger_id")
        if (file.exists()) {
            return file.readText()
        }
        val judgerId = randomString()
        file.writeText(judgerId)
        return judgerId
    }
}
