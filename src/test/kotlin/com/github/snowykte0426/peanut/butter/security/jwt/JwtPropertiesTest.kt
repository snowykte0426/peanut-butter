package com.github.snowykte0426.peanut.butter.security.jwt

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.time.Duration

class JwtPropertiesTest : StringSpec({

    "should have correct default values" {
        val properties = JwtProperties()
        
        properties.secret shouldBe "default-peanut-butter-jwt-secret-key-for-development-only-please-change-in-production"
        properties.accessTokenExpiry shouldBe Duration.ofHours(1)
        properties.refreshTokenExpiry shouldBe Duration.ofDays(1)
        properties.refreshTokenEnabled shouldBe true
        properties.refreshTokenRotationEnabled shouldBe false
        properties.refreshTokenMode shouldBe RefreshTokenMode.SIMPLE_VALIDATION
        properties.refreshTokenStoreType shouldBe RefreshTokenStoreType.IN_MEMORY
        properties.usedRefreshTokenHandling shouldBe UsedRefreshTokenHandling.REMOVE
    }
    
    "should support custom configuration" {
        val properties = JwtProperties(
            secret = "custom-secret",
            accessTokenExpiry = Duration.ofMinutes(30),
            refreshTokenExpiry = Duration.ofHours(12),
            refreshTokenEnabled = false,
            refreshTokenRotationEnabled = true,
            refreshTokenMode = RefreshTokenMode.STORE_AND_VALIDATE,
            refreshTokenStoreType = RefreshTokenStoreType.REDIS,
            usedRefreshTokenHandling = UsedRefreshTokenHandling.BLACKLIST
        )
        
        properties.secret shouldBe "custom-secret"
        properties.accessTokenExpiry shouldBe Duration.ofMinutes(30)
        properties.refreshTokenExpiry shouldBe Duration.ofHours(12)
        properties.refreshTokenEnabled shouldBe false
        properties.refreshTokenRotationEnabled shouldBe true
        properties.refreshTokenMode shouldBe RefreshTokenMode.STORE_AND_VALIDATE
        properties.refreshTokenStoreType shouldBe RefreshTokenStoreType.REDIS
        properties.usedRefreshTokenHandling shouldBe UsedRefreshTokenHandling.BLACKLIST
    }
    
    "RefreshTokenMode enum should have correct values" {
        RefreshTokenMode.values() shouldBe arrayOf(
            RefreshTokenMode.SIMPLE_VALIDATION,
            RefreshTokenMode.STORE_AND_VALIDATE
        )
    }
    
    "RefreshTokenStoreType enum should have correct values" {
        RefreshTokenStoreType.values() shouldBe arrayOf(
            RefreshTokenStoreType.REDIS,
            RefreshTokenStoreType.IN_MEMORY,
            RefreshTokenStoreType.RDB
        )
    }
    
    "UsedRefreshTokenHandling enum should have correct values" {
        UsedRefreshTokenHandling.values() shouldBe arrayOf(
            UsedRefreshTokenHandling.REMOVE,
            UsedRefreshTokenHandling.BLACKLIST
        )
    }
})