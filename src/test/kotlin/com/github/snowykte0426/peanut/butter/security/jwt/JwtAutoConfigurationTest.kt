package com.github.snowykte0426.peanut.butter.security.jwt

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf

class JwtAutoConfigurationTest : StringSpec({

    "JwtAutoConfiguration should have correct configuration" {
        val configuration = JwtAutoConfiguration()
        configuration shouldNotBe null
    }

    "should create InMemoryRefreshTokenStore by default" {
        val configuration = JwtAutoConfiguration()
        val store = configuration.inMemoryRefreshTokenStore()
        store.shouldBeInstanceOf<InMemoryRefreshTokenStore>()
    }

    "should create DefaultJwtService with properties and store" {
        val configuration = JwtAutoConfiguration()
        val properties = JwtProperties()
        val store = InMemoryRefreshTokenStore()
        
        val jwtService = configuration.defaultJwtService(properties, store)
        jwtService.shouldBeInstanceOf<DefaultJwtService>()
    }

    "should create JwtCurrentUserProvider" {
        val configuration = JwtAutoConfiguration()
        val properties = JwtProperties(
            secret = "test-secret-key-that-is-at-least-256-bits-long-for-hmac-sha256"
        )
        val jwtService = DefaultJwtService(properties, null)
        val userResolver: JwtUserResolver<Any>? = null
        
        val userProvider = configuration.jwtCurrentUserProvider(jwtService, userResolver)
        userProvider shouldNotBe null
    }

    "RefreshTokenCleanupTask should be created with store" {
        val configuration = JwtAutoConfiguration()
        val store = InMemoryRefreshTokenStore()
        
        val cleanupTask = configuration.refreshTokenCleanupTask(store)
        cleanupTask shouldNotBe null
    }

    "RefreshTokenCleanupTask should handle null store gracefully" {
        val configuration = JwtAutoConfiguration()
        
        val cleanupTask = configuration.refreshTokenCleanupTask(null)
        cleanupTask shouldNotBe null
    }
})