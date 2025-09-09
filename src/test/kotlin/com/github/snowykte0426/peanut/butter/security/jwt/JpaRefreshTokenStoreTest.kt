package com.github.snowykte0426.peanut.butter.security.jwt

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.Instant

class JpaRefreshTokenStoreTest : FunSpec({

    lateinit var refreshTokenRepository: RefreshTokenRepository
    lateinit var jpaRefreshTokenStore: JpaRefreshTokenStore

    beforeEach {
        refreshTokenRepository = mockk()
        jpaRefreshTokenStore = JpaRefreshTokenStore(refreshTokenRepository)
    }

    test("storeRefreshToken should save token entity") {
        val tokenId = "token123"
        val subject = "user123"
        val expiration = Instant.now().plusSeconds(3600)
        every { refreshTokenRepository.save(any()) } returns mockk()

        jpaRefreshTokenStore.storeRefreshToken(tokenId, subject, expiration)

        verify { refreshTokenRepository.save(RefreshTokenEntity(tokenId, subject, expiration, false)) }
    }

    test("isRefreshTokenValid should return true for valid token") {
        val tokenId = "token123"
        every { refreshTokenRepository.isTokenValid(tokenId, any()) } returns true

        val isValid = jpaRefreshTokenStore.isRefreshTokenValid(tokenId)

        isValid shouldBe true
        verify { refreshTokenRepository.isTokenValid(tokenId, any()) }
    }

    test("isRefreshTokenValid should return false for invalid token") {
        val tokenId = "token123"
        every { refreshTokenRepository.isTokenValid(tokenId, any()) } returns false

        val isValid = jpaRefreshTokenStore.isRefreshTokenValid(tokenId)

        isValid shouldBe false
        verify { refreshTokenRepository.isTokenValid(tokenId, any()) }
    }

    test("removeRefreshToken should delete token by id") {
        val tokenId = "token123"
        every { refreshTokenRepository.deleteById(tokenId) } returns Unit

        jpaRefreshTokenStore.removeRefreshToken(tokenId)

        verify { refreshTokenRepository.deleteById(tokenId) }
    }

    test("blacklistRefreshToken should blacklist token") {
        val tokenId = "token123"
        every { refreshTokenRepository.blacklistToken(tokenId) } returns Unit

        jpaRefreshTokenStore.blacklistRefreshToken(tokenId)

        verify { refreshTokenRepository.blacklistToken(tokenId) }
    }

    test("cleanupExpiredTokens should delete expired tokens") {
        every { refreshTokenRepository.deleteExpiredTokens(any()) } returns Unit

        jpaRefreshTokenStore.cleanupExpiredTokens()

        verify { refreshTokenRepository.deleteExpiredTokens(any()) }
    }

    test("RefreshTokenEntity should be created with correct properties") {
        val tokenId = "token123"
        val subject = "user123"
        val expiration = Instant.now().plusSeconds(3600)
        val isBlacklisted = false

        val entity = RefreshTokenEntity(tokenId, subject, expiration, isBlacklisted)

        entity.tokenId shouldBe tokenId
        entity.subject shouldBe subject
        entity.expiration shouldBe expiration
        entity.isBlacklisted shouldBe isBlacklisted
    }

    test("RefreshTokenEntity should support blacklisted state") {
        val tokenId = "token123"
        val subject = "user123"
        val expiration = Instant.now().plusSeconds(3600)

        val entity = RefreshTokenEntity(tokenId, subject, expiration, true)

        entity.isBlacklisted shouldBe true
    }

    test("RefreshTokenRepository methods should have correct signatures") {
        val repository = mockk<RefreshTokenRepository>()
        val tokenId = "test-token"
        val now = Instant.now()

        every { repository.findByTokenId(tokenId) } returns null
        every { repository.isTokenValid(tokenId, now) } returns false
        every { repository.blacklistToken(tokenId) } returns Unit
        every { repository.deleteExpiredTokens(now) } returns Unit

        repository.findByTokenId(tokenId) shouldBe null
        repository.isTokenValid(tokenId, now) shouldBe false

        verify { repository.findByTokenId(tokenId) }
        verify { repository.isTokenValid(tokenId, now) }
    }
})
