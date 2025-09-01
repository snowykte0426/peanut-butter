package com.github.snowykte0426.peanut.butter.security.jwt

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.time.Instant

class InMemoryRefreshTokenStoreTest : StringSpec({

    val store = InMemoryRefreshTokenStore()

    "should store and validate refresh tokens" {
        val tokenId = "token-123"
        val subject = "user-123"
        val expiration = Instant.now().plusSeconds(3600)
        
        store.storeRefreshToken(tokenId, subject, expiration)
        store.isRefreshTokenValid(tokenId) shouldBe true
    }

    "should return false for non-existent tokens" {
        store.isRefreshTokenValid("non-existent-token") shouldBe false
    }

    "should return false for expired tokens" {
        val tokenId = "expired-token"
        val subject = "user-123"
        val expiration = Instant.now().minusSeconds(1)
        
        store.storeRefreshToken(tokenId, subject, expiration)
        store.isRefreshTokenValid(tokenId) shouldBe false
    }

    "should remove tokens correctly" {
        val tokenId = "removable-token"
        val subject = "user-123"
        val expiration = Instant.now().plusSeconds(3600)
        
        store.storeRefreshToken(tokenId, subject, expiration)
        store.isRefreshTokenValid(tokenId) shouldBe true
        
        store.removeRefreshToken(tokenId)
        store.isRefreshTokenValid(tokenId) shouldBe false
    }

    "should blacklist tokens correctly" {
        val tokenId = "blacklist-token"
        val subject = "user-123"
        val expiration = Instant.now().plusSeconds(3600)
        
        store.storeRefreshToken(tokenId, subject, expiration)
        store.isRefreshTokenValid(tokenId) shouldBe true
        
        store.blacklistRefreshToken(tokenId)
        store.isRefreshTokenValid(tokenId) shouldBe false
    }

    "should cleanup expired tokens" {
        val validTokenId = "valid-token"
        val expiredTokenId = "expired-token"
        val subject = "user-123"
        val futureExpiration = Instant.now().plusSeconds(3600)
        val pastExpiration = Instant.now().minusSeconds(1)
        
        store.storeRefreshToken(validTokenId, subject, futureExpiration)
        store.storeRefreshToken(expiredTokenId, subject, pastExpiration)
        
        store.isRefreshTokenValid(validTokenId) shouldBe true
        store.isRefreshTokenValid(expiredTokenId) shouldBe false
        
        store.cleanupExpiredTokens()
        
        store.isRefreshTokenValid(validTokenId) shouldBe true
        store.isRefreshTokenValid(expiredTokenId) shouldBe false
    }

    "should handle concurrent access" {
        val tokens = (1..100).map { "token-$it" }
        val subject = "user-123"
        val expiration = Instant.now().plusSeconds(3600)
        
        // Store tokens concurrently
        tokens.parallelStream().forEach { tokenId ->
            store.storeRefreshToken(tokenId, subject, expiration)
        }
        
        // Validate tokens concurrently
        val results = tokens.parallelStream().map { tokenId ->
            store.isRefreshTokenValid(tokenId)
        }.toList()
        
        results.all { it } shouldBe true
    }

    "should handle edge cases" {
        val tokenId = "edge-case-token"
        val subject = "user-123"
        val expiration = Instant.now().plusSeconds(3600)
        
        // Try to remove non-existent token
        store.removeRefreshToken("non-existent")
        
        // Try to blacklist non-existent token
        store.blacklistRefreshToken("non-existent")
        
        store.storeRefreshToken(tokenId, subject, expiration)
        store.isRefreshTokenValid(tokenId) shouldBe true
        
        // Try to blacklist twice
        store.blacklistRefreshToken(tokenId)
        store.blacklistRefreshToken(tokenId)
        
        store.isRefreshTokenValid(tokenId) shouldBe false
    }
})