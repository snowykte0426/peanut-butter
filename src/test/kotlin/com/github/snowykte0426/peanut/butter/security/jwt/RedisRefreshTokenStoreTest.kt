package com.github.snowykte0426.peanut.butter.security.jwt

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import java.time.Instant
import java.util.concurrent.TimeUnit

class RedisRefreshTokenStoreTest : FunSpec({

    lateinit var redisTemplate: RedisTemplate<String, String>
    lateinit var valueOperations: ValueOperations<String, String>
    lateinit var objectMapper: ObjectMapper
    lateinit var redisRefreshTokenStore: RedisRefreshTokenStore

    beforeEach {
        redisTemplate = mockk()
        valueOperations = mockk()
        objectMapper = ObjectMapper().apply { findAndRegisterModules() }

        every { redisTemplate.opsForValue() } returns valueOperations

        redisRefreshTokenStore = RedisRefreshTokenStore(redisTemplate, objectMapper)
    }

    test("storeRefreshToken should store token in redis with TTL") {
        val tokenId = "token123"
        val subject = "user123"
        val expiration = Instant.now().plusSeconds(3600)
        every { valueOperations.set(any(), any(), any(), any<TimeUnit>()) } returns Unit

        redisRefreshTokenStore.storeRefreshToken(tokenId, subject, expiration)

        verify {
            valueOperations.set(
                "jwt:refresh:$tokenId",
                any(),
                any(),
                TimeUnit.SECONDS
            )
        }
    }

    test("isRefreshTokenValid should return true when token exists and not blacklisted") {
        val tokenId = "token123"
        every { redisTemplate.hasKey("jwt:refresh:$tokenId") } returns true
        every { redisTemplate.hasKey("jwt:blacklist:$tokenId") } returns false

        val isValid = redisRefreshTokenStore.isRefreshTokenValid(tokenId)

        isValid shouldBe true
        verify { redisTemplate.hasKey("jwt:refresh:$tokenId") }
        verify { redisTemplate.hasKey("jwt:blacklist:$tokenId") }
    }

    test("isRefreshTokenValid should return false when token does not exist") {
        val tokenId = "token123"
        every { redisTemplate.hasKey("jwt:refresh:$tokenId") } returns false
        every { redisTemplate.hasKey("jwt:blacklist:$tokenId") } returns false

        val isValid = redisRefreshTokenStore.isRefreshTokenValid(tokenId)

        isValid shouldBe false
        verify { redisTemplate.hasKey("jwt:refresh:$tokenId") }
    }

    test("isRefreshTokenValid should return false when token is blacklisted") {
        val tokenId = "token123"
        every { redisTemplate.hasKey("jwt:refresh:$tokenId") } returns true
        every { redisTemplate.hasKey("jwt:blacklist:$tokenId") } returns true

        val isValid = redisRefreshTokenStore.isRefreshTokenValid(tokenId)

        isValid shouldBe false
        verify { redisTemplate.hasKey("jwt:refresh:$tokenId") }
        verify { redisTemplate.hasKey("jwt:blacklist:$tokenId") }
    }

    test("removeRefreshToken should delete token from redis") {
        val tokenId = "token123"
        every { redisTemplate.delete("jwt:refresh:$tokenId") } returns true

        redisRefreshTokenStore.removeRefreshToken(tokenId)

        verify { redisTemplate.delete("jwt:refresh:$tokenId") }
    }

    test("blacklistRefreshToken should add token to blacklist with proper TTL") {
        val tokenId = "token123"
        val expiration = Instant.now().plusSeconds(3600)
        val tokenRecord = RefreshTokenRecord(tokenId, "user123", expiration)
        val tokenJson = objectMapper.writeValueAsString(tokenRecord)

        every { valueOperations.get("jwt:refresh:$tokenId") } returns tokenJson
        every { valueOperations.set(any(), any(), any(), any<TimeUnit>()) } returns Unit

        redisRefreshTokenStore.blacklistRefreshToken(tokenId)

        verify { valueOperations.get("jwt:refresh:$tokenId") }
        verify {
            valueOperations.set(
                "jwt:blacklist:$tokenId",
                "blacklisted",
                any(),
                TimeUnit.SECONDS
            )
        }
    }

    test("blacklistRefreshToken should do nothing when token does not exist") {
        val tokenId = "token123"
        every { valueOperations.get("jwt:refresh:$tokenId") } returns null

        redisRefreshTokenStore.blacklistRefreshToken(tokenId)

        verify { valueOperations.get("jwt:refresh:$tokenId") }
        verify(exactly = 0) {
            valueOperations.set(
                "jwt:blacklist:$tokenId",
                any(),
                any(),
                any<TimeUnit>()
            )
        }
    }

    test("blacklistRefreshToken should handle expired tokens gracefully") {
        val tokenId = "token123"
        val expiration = Instant.now().minusSeconds(3600) // 이미 만료된 토큰
        val tokenRecord = RefreshTokenRecord(tokenId, "user123", expiration)
        val tokenJson = objectMapper.writeValueAsString(tokenRecord)

        every { valueOperations.get("jwt:refresh:$tokenId") } returns tokenJson

        redisRefreshTokenStore.blacklistRefreshToken(tokenId)

        verify { valueOperations.get("jwt:refresh:$tokenId") }
        // TTL이 0보다 작거나 같으면 blacklist에 추가하지 않음
        verify(exactly = 0) {
            valueOperations.set(
                "jwt:blacklist:$tokenId",
                any(),
                any(),
                any<TimeUnit>()
            )
        }
    }

    test("cleanupExpiredTokens should do nothing as Redis handles expiration automatically") {
        // Redis handles expiration automatically, so this method should not perform any operations
        redisRefreshTokenStore.cleanupExpiredTokens()

        // No verification needed as the method should be empty
        verify(exactly = 0) { redisTemplate.delete(any<String>()) }
        verify(exactly = 0) { valueOperations.set(any(), any(), any(), any<TimeUnit>()) }
    }

    test("should handle JSON serialization and deserialization correctly") {
        val tokenRecord = RefreshTokenRecord("test-token", "test-user", Instant.now())
        val json = objectMapper.writeValueAsString(tokenRecord)
        val deserialized = objectMapper.readValue(json, RefreshTokenRecord::class.java)

        deserialized.tokenId shouldBe tokenRecord.tokenId
        deserialized.subject shouldBe tokenRecord.subject
        deserialized.expiration shouldBe tokenRecord.expiration
        deserialized.isBlacklisted shouldBe tokenRecord.isBlacklisted
    }
})
