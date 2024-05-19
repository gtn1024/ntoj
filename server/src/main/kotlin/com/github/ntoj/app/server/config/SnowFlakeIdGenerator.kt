package com.github.ntoj.app.server.config

import com.github.ntoj.app.shared.util.getSnowflakeId
import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.id.Configurable
import org.hibernate.id.IdentifierGenerator
import java.io.Serializable

class SnowFlakeIdGenerator : IdentifierGenerator, Configurable {
    override fun generate(
        session: SharedSessionContractImplementor?,
        `object`: Any?,
    ): Serializable {
        return getSnowflakeId()
    }
}
