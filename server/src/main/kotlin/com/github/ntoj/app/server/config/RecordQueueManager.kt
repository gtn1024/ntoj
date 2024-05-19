package com.github.ntoj.app.server.config

import com.github.ntoj.app.server.model.entities.Record
import com.github.ntoj.app.server.service.RecordService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicLong

@Component
@EnableScheduling
class RecordQueueManager(
    private val recordService: RecordService,
) {
    private val lastId: AtomicLong = AtomicLong(0)
    private val queue: Queue<Record> = ConcurrentLinkedQueue()
    private val logger: Logger = LoggerFactory.getLogger(RecordQueueManager::class.java)

    @Scheduled(fixedRate = 2000)
    private fun processQueue() {
        logger.debug("Current record queue size: {}", queue.size)
        if (queue.size < 40) {
            val records = recordService.getPendingRecords(10, (lastId.get() + 1).toString())
            if (records.isNotEmpty()) {
                lastId.set(records.last().recordId!!.toLong())
                logger.debug("Adding {} records to queue", records.size)
                queue.addAll(records)
            }
        }
    }

    fun getOneOrNull(): Record? {
        return queue.poll()
    }
}
