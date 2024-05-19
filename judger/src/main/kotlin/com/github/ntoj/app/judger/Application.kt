package com.github.ntoj.app.judger

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.net.ConnectException
import java.util.Timer
import java.util.TimerTask

private val LOGGER = LoggerFactory.getLogger("com.github.ntoj.app.judger.Application")

fun showMessage() {
    LOGGER.info("NTOJ Judger v${Configuration.VERSION}")
    LOGGER.info("Server Host:    ${Configuration.SERVER_HOST}")
    LOGGER.info("Sandbox Server: ${Configuration.SANDBOX_SERVER}")
    LOGGER.info("Judger ID:      ${Configuration.JUDGER_ID}")
    LOGGER.info("Thread Count:   ${Configuration.THREAD_COUNT}")
    LOGGER.info("System info:")
    LOGGER.info("  OS:           ${Configuration.OS}")
    LOGGER.info("  Kernel:       ${Configuration.KERNEL}")
    LOGGER.info("  Memory Used:  ${Configuration.memoryUsed()}")
    LOGGER.info("  Memory Total: ${Configuration.memoryTotal()}")
}

suspend fun sandboxAvailable(): Boolean {
    return try {
        Client.Sandbox.version()
        true
    } catch (e: Exception) {
        false
    }
}

suspend fun run(id: Int) {
    var connected = false
    while (true) {
        delay(300)
        if (Configuration.token == null) {
            refreshToken()
            delay(5000)
            continue
        }
        try {
            if (!sandboxAvailable()) {
                LOGGER.error("(#$id) Sandbox server connect failed. Retry in 5s.")
                connected = false
                delay(5000)
                continue
            }
            val record = Client.Backend.get()
            if (!connected) {
                LOGGER.info("(#$id) Connected! Waiting for submission.")
            }
            connected = true
            if (record == null) {
                delay(1000)
                continue
            }
            TestRunner.runTest(
                record.recordId,
                record.code,
                record.lang,
                record.timeLimit,
                record.memoryLimit,
                record.origin,
                record.input,
                record.testcase?.fileId,
                record.testcase?.hash,
            )
        } catch (e: IllegalStateException) {
            throw e
        } catch (e: ConnectException) {
            connected = false
            LOGGER.error("(#$id) Sandbox server connect failed. Retry in 5s.")
            delay(5000)
        } catch (e: Exception) {
            connected = false
            LOGGER.error("(#$id) Unknown error!", e)
        } finally {
            delay(1000)
        }
    }
}

suspend fun refreshToken() {
    val token = Client.Backend.getToken()
    Configuration.token = token
    LOGGER.info("Token refreshed.")
}

val timer = Timer()

fun registerTokenRefresh() {
    timer.scheduleAtFixedRate(
        object : TimerTask() {
            override fun run() {
                runBlocking {
                    refreshToken()
                }
            }
        },
        1000,
        8 * 60 * 60 * 1000,
    )
}

fun main() {
    showMessage()
    registerTokenRefresh()
    runBlocking {
        repeat(Configuration.THREAD_COUNT) { id ->
            launch {
                delay(5000)
                run(id)
            }
        }
    }
}
