package com.github.ntoj.app.server.config.cache

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.Ticker
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCache
import org.springframework.cache.support.SimpleCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration
@EnableCaching
class CacheConfig {
    @Bean
    fun cacheManager(ticker: Ticker): CacheManager {
        val manager = SimpleCacheManager()
        manager.setCaches(
            listOf(
                buildCache("contestStatistic", ticker, 5, TimeUnit.SECONDS),
                buildCache("contestStanding", ticker, 5, TimeUnit.SECONDS),
                buildCache("user", ticker, 1, TimeUnit.MINUTES),
            ),
        )
        return manager
    }

    private fun buildCache(
        name: String,
        ticker: Ticker,
        expireTime: Long,
        timeUnit: TimeUnit,
    ): CaffeineCache {
        return CaffeineCache(
            name,
            Caffeine.newBuilder()
                .expireAfterWrite(expireTime, timeUnit)
                .maximumSize(100)
                .ticker(ticker)
                .build(),
        )
    }

    @Bean
    fun ticker(): Ticker {
        return Ticker.systemTicker()
    }
}
