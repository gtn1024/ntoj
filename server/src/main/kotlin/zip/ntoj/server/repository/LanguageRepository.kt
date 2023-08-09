package zip.ntoj.server.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import zip.ntoj.server.model.Language

interface LanguageRepository : JpaRepository<Language, Long>, JpaSpecificationExecutor<Language>
