package com.github.ntoj.app.server.config

import java.util.ResourceBundle

object Constant {
    val VERSION: String = ResourceBundle.getBundle("version").getString("version").trim()
}
