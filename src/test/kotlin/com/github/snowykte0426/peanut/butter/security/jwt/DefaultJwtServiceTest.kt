package com.github.snowykte0426.peanut.butter.security.jwt

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldNotBeEmpty
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.Duration

class DefaultJwtServiceTest : FunSpec({

    val properties = JwtProperties(
        secret = "test-secret-key-that-is-at-least-256-bits-long-for-hmac-sha256-algorithm",
        accessTokenExpiry = Duration.ofMinutes(15),
        refreshTokenExpiry = Duration.ofHours(1),
        refreshTokenEnabled = true,
        refreshTokenRotationEnabled = false
    )

    val mockRefreshTokenStore = mock<RefreshTokenStore>()
    val jwtService = DefaultJwtService(properties, mockRefreshTokenStore)

    test("should generate valid access token") {
        val subject = "test-user"
        val claims = mapOf("role" to "USER", "department" to "IT")
        
        val token = jwtService.generateAccessToken(subject, claims)
        
        token.shouldNotBeEmpty()
        jwtService.validateToken(token) shouldBe true
        jwtService.extractSubject(token) shouldBe subject
        
        val extractedClaims = jwtService.extractClaims(token)
        extractedClaims?.get("role") shouldBe "USER"
        extractedClaims?.get("department") shouldBe "IT"
    }

    test("should generate valid refresh token when enabled") {
        val subject = "test-user"
        
        val refreshToken = jwtService.generateRefreshToken(subject)
        
        refreshToken.shouldNotBeEmpty()
        jwtService.validateToken(refreshToken) shouldBe true
        jwtService.extractSubject(refreshToken) shouldBe subject
    }

    test("should throw exception when generating refresh token when disabled") {
        val disabledProperties = properties.copy(refreshTokenEnabled = false)
        val disabledService = DefaultJwtService(disabledProperties, mockRefreshTokenStore)
        
        kotlin.runCatching {
            disabledService.generateRefreshToken("test-user")
        }.isFailure shouldBe true
    }

    test("should validate tokens correctly") {
        val token = jwtService.generateAccessToken("test-user")
        
        jwtService.validateToken(token) shouldBe true
        jwtService.validateToken("invalid-token") shouldBe false
        jwtService.validateToken("") shouldBe false
    }

    test("should extract token information correctly") {
        val subject = "test-user"
        val claims = mapOf("role" to "ADMIN")
        val token = jwtService.generateAccessToken(subject, claims)
        
        jwtService.extractSubject(token) shouldBe subject
        jwtService.extractClaims(token)?.get("role") shouldBe "ADMIN"
        jwtService.extractExpiration(token).shouldNotBeNull()
        jwtService.isTokenExpired(token) shouldBe false
    }

    test("should handle token expiration correctly") {
        val shortProperties = properties.copy(accessTokenExpiry = Duration.ofMillis(1))
        val shortService = DefaultJwtService(shortProperties, mockRefreshTokenStore)
        val token = shortService.generateAccessToken("test-user")
        
        Thread.sleep(10)
        
        shortService.isTokenExpired(token) shouldBe true
        shortService.validateToken(token) shouldBe false
    }

    test("should refresh tokens when refresh token is valid") {
        whenever(mockRefreshTokenStore.isRefreshTokenValid(any())).thenReturn(true)
        
        val storeProperties = properties.copy(refreshTokenMode = RefreshTokenMode.STORE_AND_VALIDATE)
        val storeService = DefaultJwtService(storeProperties, mockRefreshTokenStore)
        
        val refreshToken = storeService.generateRefreshToken("test-user")
        val tokenPair = storeService.refreshTokens(refreshToken)
        
        tokenPair.shouldNotBeNull()
        tokenPair.accessToken.shouldNotBeEmpty()
        tokenPair.refreshToken shouldBe refreshToken
    }

    test("should return null when refresh token is invalid") {
        whenever(mockRefreshTokenStore.isRefreshTokenValid(any())).thenReturn(false)
        
        val storeProperties = properties.copy(refreshTokenMode = RefreshTokenMode.STORE_AND_VALIDATE)
        val storeService = DefaultJwtService(storeProperties, mockRefreshTokenStore)
        
        val refreshToken = storeService.generateRefreshToken("test-user")
        val tokenPair = storeService.refreshTokens(refreshToken)
        
        tokenPair shouldBe null
    }

    test("should generate new refresh token when rotation is enabled") {
        whenever(mockRefreshTokenStore.isRefreshTokenValid(any())).thenReturn(true)
        
        val rotationProperties = properties.copy(
            refreshTokenMode = RefreshTokenMode.STORE_AND_VALIDATE,
            refreshTokenRotationEnabled = true
        )
        val rotationService = DefaultJwtService(rotationProperties, mockRefreshTokenStore)
        
        val originalRefreshToken = rotationService.generateRefreshToken("test-user")
        val tokenPair = rotationService.refreshTokens(originalRefreshToken)
        
        tokenPair.shouldNotBeNull()
        tokenPair.refreshToken shouldNotBe originalRefreshToken
        tokenPair.refreshToken.shouldNotBeEmpty()
    }

    test("should handle invalid tokens gracefully") {
        jwtService.extractSubject("invalid-token") shouldBe null
        jwtService.extractClaims("invalid-token") shouldBe null
        jwtService.extractExpiration("invalid-token") shouldBe null
        jwtService.isTokenExpired("invalid-token") shouldBe true
    }
})