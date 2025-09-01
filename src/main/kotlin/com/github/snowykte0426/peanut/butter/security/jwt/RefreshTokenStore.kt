package com.github.snowykte0426.peanut.butter.security.jwt

import java.time.Instant

interface RefreshTokenStore {
    fun storeRefreshToken(tokenId: String, subject: String, expiration: Instant)
    fun isRefreshTokenValid(tokenId: String): Boolean
    fun removeRefreshToken(tokenId: String)
    fun blacklistRefreshToken(tokenId: String)
    fun cleanupExpiredTokens()
}

data class RefreshTokenRecord(
    val tokenId: String,
    val subject: String,
    val expiration: Instant,
    val isBlacklisted: Boolean = false
)