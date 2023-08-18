package zip.ntoj.judger

import zip.ntoj.shared.util.randomString

object Configuration {
    val SERVER_HOST = System.getenv("SERVER_HOST") ?: "http://localhost:18080"
    val SANDBOX_SERVER = System.getenv("SANDBOX_SERVER") ?: "http://localhost:5050"
    val TOKEN = System.getenv("TOKEN") ?: throw IllegalStateException("TOKEN is not set")
    val JUDGER_ID = System.getenv("JUDGER_ID") ?: randomString()
}

object SourceFilename {
    const val CPP = "a.cc"
    const val C = "a.c"
    const val JAVA = "Main.java"
    const val PYTHON = "main.py"
}

object TargetFilename {
    const val CPP = "a"
    const val C = "a"
    const val JAVA = "Main.jar"
    const val PYTHON = "main.py"
}
