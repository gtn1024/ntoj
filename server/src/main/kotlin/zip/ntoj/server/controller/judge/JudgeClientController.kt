package zip.ntoj.server.controller.judge

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import zip.ntoj.server.model.R
import java.time.Instant

@RestController
@RequestMapping("/judge_client")
class JudgeClientController {
    @GetMapping("/ping")
    fun ping() : ResponseEntity<R<PingResponse>> {
        return R.success(200, "Pong", PingResponse("Pong"))
    }
}

data class PingResponse(
    val message: String,
    val currentTime: Instant = Instant.now(),
)
