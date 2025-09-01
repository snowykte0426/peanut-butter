package com.github.snowykte0426.peanut.butter.security.jwt

import org.springframework.stereotype.Component
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

@Component
class InMemoryRefreshTokenStore : RefreshTokenStore {
    private val tokens = ConcurrentHashMap<String, RefreshTokenRecord>()

    override fun storeRefreshToken(tokenId: String, subject: String, expiration: Instant) {
        tokens[tokenId] = RefreshTokenRecord(tokenId, subject, expiration)
    }

    override fun isRefreshTokenValid(tokenId: String): Boolean {
        val record = tokens[tokenId] ?: return false
        return !record.isBlacklisted && record.expiration.isAfter(Instant.now())
    }

    override fun removeRefreshToken(tokenId: String) {
        tokens.remove(tokenId)
    }

    override fun blacklistRefreshToken(tokenId: String) {
        val record = tokens[tokenId]
        if (record != null) {
            tokens[tokenId] = record.copy(isBlacklisted = true)
        }
    }

    override fun cleanupExpiredTokens() {
        val now = Instant.now()
        tokens.entries.removeIf { it.value.expiration.isBefore(now) }
    }
}