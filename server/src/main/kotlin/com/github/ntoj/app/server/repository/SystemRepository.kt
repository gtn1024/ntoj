package com.github.ntoj.app.server.repository

import com.github.ntoj.app.server.model.entities.System
import org.springframework.data.repository.CrudRepository

interface SystemRepository : CrudRepository<System, String>
