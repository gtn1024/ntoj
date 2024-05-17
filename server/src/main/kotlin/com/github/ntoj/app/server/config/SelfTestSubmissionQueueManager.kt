package com.github.ntoj.app.server.config

import com.github.ntoj.app.server.model.entities.SelfTestSubmission
import com.github.ntoj.app.server.service.SelfTestSubmissionService
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
class SelfTestSubmissionQueueManager(
    private val selfTestSubmissionService: SelfTestSubmissionService,
) {
    private val lastId: AtomicLong = AtomicLong(0)
    private val deque: Queue<SelfTestSubmission> = ConcurrentLinkedQueue()
    private val logger: Logger = LoggerFactory.getLogger(SelfTestSubmissionQueueManager::class.java)

    @Scheduled(fixedRate = 2000)
    private fun processQueue() {
        logger.debug("Current self test submission queue size: {}", deque.size)
        if (deque.size < 40) {
            val submissions = selfTestSubmissionService.getPendingSubmissions(10, lastId.get() + 1)
            if (submissions.isNotEmpty()) {
                lastId.set(submissions.last().selfTestSubmissionId!!)
                logger.debug("Adding {} submissions to queue", submissions.size)
                deque.addAll(submissions)
            }
        }
    }

    fun getOneOrNull(): SelfTestSubmission? {
        return deque.poll()
    }
}
