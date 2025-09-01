package com.github.snowykte0426.peanut.butter.security.jwt

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.DefaultValue
import java.time.Duration

@ConfigurationProperties(prefix = "peanut-butter.jwt")
data class JwtProperties(
    val secret: String = "default-peanut-butter-jwt-secret-key-for-development-only-please-change-in-production",
    @DefaultValue("PT1H")
    val accessTokenExpiry: Duration = Duration.ofHours(1),
    @DefaultValue("PT24H")
    val refreshTokenExpiry: Duration = Duration.ofDays(1),
    @DefaultValue("true")
    val refreshTokenEnabled: Boolean = true,
    @DefaultValue("false")
    val refreshTokenRotationEnabled: Boolean = false,
    @DefaultValue("SIMPLE_VALIDATION")
    val refreshTokenMode: RefreshTokenMode = RefreshTokenMode.SIMPLE_VALIDATION,
    @DefaultValue("IN_MEMORY")
    val refreshTokenStoreType: RefreshTokenStoreType = RefreshTokenStoreType.IN_MEMORY,
    @DefaultValue("REMOVE")
    val usedRefreshTokenHandling: UsedRefreshTokenHandling = UsedRefreshTokenHandling.REMOVE
)

enum class RefreshTokenMode {
    SIMPLE_VALIDATION,
    STORE_AND_VALIDATE
}

enum class RefreshTokenStoreType {
    REDIS,
    IN_MEMORY,
    RDB
}

enum class UsedRefreshTokenHandling {
    REMOVE,
    BLACKLIST
}