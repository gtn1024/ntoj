package com.github.ntoj.app.server.config

import com.github.ntoj.app.server.model.entities.Submission
import com.github.ntoj.app.server.service.SubmissionService
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
class SubmissionQueueManager(
    private val submissionService: SubmissionService,
) {
    private val lastId: AtomicLong = AtomicLong(0)
    private val deque: Queue<Submission> = ConcurrentLinkedQueue()
    private val logger: Logger = LoggerFactory.getLogger(SubmissionQueueManager::class.java)

    @Scheduled(fixedRate = 2000)
    private fun processQueue() {
        logger.debug("Current submission queue size: {}", deque.size)
        if (deque.size < 40) {
            val submissions = submissionService.getPendingSubmissions(10, lastId.get() + 1)
            if (submissions.isNotEmpty()) {
                lastId.set(submissions.last().submissionId!!)
                logger.debug("Adding {} submissions to queue", submissions.size)
                deque.addAll(submissions)
            }
        }
    }

    fun getOneOrNull(): Submission? {
        return deque.poll()
    }
}
