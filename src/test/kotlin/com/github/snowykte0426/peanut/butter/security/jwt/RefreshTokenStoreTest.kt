package com.github.snowykte0426.peanut.butter.security.jwt

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.time.Instant

class RefreshTokenStoreTest : FunSpec({

    class TestRefreshTokenStore : RefreshTokenStore {
        private val tokens = mutableMapOf<String, RefreshTokenRecord>()
        private val blacklist = mutableSetOf<String>()

        override fun storeRefreshToken(tokenId: String, subject: String, expiration: Instant) {
            tokens[tokenId] = RefreshTokenRecord(tokenId, subject, expiration)
        }

        override fun isRefreshTokenValid(tokenId: String): Boolean {
            val token = tokens[tokenId] ?: return false
            return !blacklist.contains(tokenId) && token.expiration.isAfter(Instant.now())
        }

        override fun removeRefreshToken(tokenId: String) {
            tokens.remove(tokenId)
        }

        override fun blacklistRefreshToken(tokenId: String) {
            blacklist.add(tokenId)
        }

        override fun cleanupExpiredTokens() {
            val now = Instant.now()
            tokens.entries.removeIf { it.value.expiration.isBefore(now) }
        }
    }

    test("RefreshTokenRecord should be created with correct properties") {
        val tokenId = "token123"
        val subject = "user123"
        val expiration = Instant.now().plusSeconds(3600)

        val record = RefreshTokenRecord(tokenId, subject, expiration)

        record.tokenId shouldBe tokenId
        record.subject shouldBe subject
        record.expiration shouldBe expiration
        record.isBlacklisted shouldBe false
    }

    test("RefreshTokenRecord should allow setting blacklisted flag") {
        val tokenId = "token123"
        val subject = "user123"
        val expiration = Instant.now().plusSeconds(3600)

        val record = RefreshTokenRecord(tokenId, subject, expiration, true)

        record.isBlacklisted shouldBe true
    }

    test("RefreshTokenStore implementation should store and validate tokens") {
        val store = TestRefreshTokenStore()
        val tokenId = "token123"
        val subject = "user123"
        val expiration = Instant.now().plusSeconds(3600)

        store.storeRefreshToken(tokenId, subject, expiration)

        store.isRefreshTokenValid(tokenId) shouldBe true
    }

    test("RefreshTokenStore should return false for non-existent tokens") {
        val store = TestRefreshTokenStore()

        store.isRefreshTokenValid("nonexistent") shouldBe false
    }

    test("RefreshTokenStore should remove tokens") {
        val store = TestRefreshTokenStore()
        val tokenId = "token123"
        val subject = "user123"
        val expiration = Instant.now().plusSeconds(3600)

        store.storeRefreshToken(tokenId, subject, expiration)
        store.removeRefreshToken(tokenId)

        store.isRefreshTokenValid(tokenId) shouldBe false
    }

    test("RefreshTokenStore should blacklist tokens") {
        val store = TestRefreshTokenStore()
        val tokenId = "token123"
        val subject = "user123"
        val expiration = Instant.now().plusSeconds(3600)

        store.storeRefreshToken(tokenId, subject, expiration)
        store.blacklistRefreshToken(tokenId)

        store.isRefreshTokenValid(tokenId) shouldBe false
    }

    test("RefreshTokenStore should cleanup expired tokens") {
        val store = TestRefreshTokenStore()
        val tokenId1 = "token1"
        val tokenId2 = "token2"
        val subject = "user123"
        val pastExpiration = Instant.now().minusSeconds(3600)
        val futureExpiration = Instant.now().plusSeconds(3600)

        store.storeRefreshToken(tokenId1, subject, pastExpiration)
        store.storeRefreshToken(tokenId2, subject, futureExpiration)
        store.cleanupExpiredTokens()

        store.isRefreshTokenValid(tokenId1) shouldBe false
        store.isRefreshTokenValid(tokenId2) shouldBe true
    }
})
