package zip.ntoj.judge.config

import zip.ntoj.shared.util.randomString

val NTOJ_SERVER_URL = System.getenv("NTOJ_SERVER_URL") ?: "http://localhost:18080"
val NTOJ_CLIENT_ID = System.getenv("NTOJ_CLIENT_ID") ?: randomString()
