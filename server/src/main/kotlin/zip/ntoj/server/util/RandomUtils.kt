package zip.ntoj.server.util

import java.util.UUID

fun randomString() = UUID.randomUUID().toString().replace("-", "")
