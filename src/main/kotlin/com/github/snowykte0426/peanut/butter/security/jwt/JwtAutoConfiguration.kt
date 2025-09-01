package com.github.snowykte0426.peanut.butter.security.jwt

import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled

@AutoConfiguration
@EnableConfigurationProperties(JwtProperties::class)
@Import(JwtConfiguration::class)
@EnableScheduling
class JwtAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(
        name = ["peanut-butter.jwt.refresh-token-store-type"],
        havingValue = "IN_MEMORY",
        matchIfMissing = true
    )
    fun inMemoryRefreshTokenStore(): RefreshTokenStore {
        return InMemoryRefreshTokenStore()
    }

    @Bean
    @ConditionalOnMissingBean
    fun defaultJwtService(
        jwtProperties: JwtProperties,
        refreshTokenStore: RefreshTokenStore?
    ): JwtService {
        return DefaultJwtService(jwtProperties, refreshTokenStore)
    }

    @Bean
    @ConditionalOnMissingBean
    fun <T> jwtCurrentUserProvider(
        jwtService: JwtService,
        jwtUserResolver: JwtUserResolver<T>?
    ): CurrentUserProvider<T> {
        return JwtCurrentUserProvider(jwtService, jwtUserResolver)
    }

    @Bean
    @ConditionalOnProperty(
        name = ["peanut-butter.jwt.cleanup.enabled"],
        havingValue = "true",
        matchIfMissing = true
    )
    fun refreshTokenCleanupTask(refreshTokenStore: RefreshTokenStore?): RefreshTokenCleanupTask {
        return RefreshTokenCleanupTask(refreshTokenStore)
    }
}

class RefreshTokenCleanupTask(
    private val refreshTokenStore: RefreshTokenStore?
) {
    
    @Scheduled(fixedRate = 3600000) // 1 hour
    fun cleanupExpiredTokens() {
        refreshTokenStore?.cleanupExpiredTokens()
    }
}