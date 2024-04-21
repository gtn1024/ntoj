package com.github.ntoj.app.server.repository

import com.github.ntoj.app.server.model.entities.Language
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface LanguageRepository : JpaRepository<Language, Long>, JpaSpecificationExecutor<Language>
