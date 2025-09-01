package com.github.snowykte0426.peanut.butter.security.jwt

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import jakarta.servlet.http.HttpServletRequest
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

class JwtCurrentUserProviderTest : StringSpec({

    data class TestUser(val id: String, val name: String)

    val jwtService = mock<JwtService>()
    val userResolver = mock<JwtUserResolver<TestUser>>()
    val currentUserProvider = JwtCurrentUserProvider(jwtService, userResolver)

    val mockRequest = mock<HttpServletRequest>()
    val mockRequestAttributes = mock<ServletRequestAttributes>()

    beforeEach {
        whenever(mockRequestAttributes.request).thenReturn(mockRequest)
        RequestContextHolder.setRequestAttributes(mockRequestAttributes)
    }

    afterEach {
        RequestContextHolder.resetRequestAttributes()
    }

    "should extract user ID from valid JWT token" {
        val token = "valid.jwt.token"
        val expectedSubject = "user-123"
        
        whenever(mockRequest.getHeader("Authorization")).thenReturn("Bearer $token")
        whenever(jwtService.validateToken(token)).thenReturn(true)
        whenever(jwtService.extractSubject(token)).thenReturn(expectedSubject)
        
        val userId = currentUserProvider.getCurrentUserId()
        
        userId shouldBe expectedSubject
    }

    "should return null when no authorization header" {
        whenever(mockRequest.getHeader("Authorization")).thenReturn(null)
        
        val userId = currentUserProvider.getCurrentUserId()
        
        userId shouldBe null
    }

    "should return null when authorization header doesn't start with Bearer" {
        whenever(mockRequest.getHeader("Authorization")).thenReturn("Basic credentials")
        
        val userId = currentUserProvider.getCurrentUserId()
        
        userId shouldBe null
    }

    "should return null when token is invalid" {
        val token = "invalid.jwt.token"
        
        whenever(mockRequest.getHeader("Authorization")).thenReturn("Bearer $token")
        whenever(jwtService.validateToken(token)).thenReturn(false)
        
        val userId = currentUserProvider.getCurrentUserId()
        
        userId shouldBe null
    }

    "should extract claims from valid JWT token" {
        val token = "valid.jwt.token"
        val expectedClaims = mapOf("role" to "USER", "department" to "IT")
        
        whenever(mockRequest.getHeader("Authorization")).thenReturn("Bearer $token")
        whenever(jwtService.validateToken(token)).thenReturn(true)
        whenever(jwtService.extractClaims(token)).thenReturn(expectedClaims)
        
        val claims = currentUserProvider.getCurrentUserClaims()
        
        claims shouldBe expectedClaims
    }

    "should resolve current user using user resolver" {
        val token = "valid.jwt.token"
        val subject = "user-123"
        val claims = mapOf("name" to "John Doe")
        val expectedUser = TestUser("user-123", "John Doe")
        
        whenever(mockRequest.getHeader("Authorization")).thenReturn("Bearer $token")
        whenever(jwtService.validateToken(token)).thenReturn(true)
        whenever(jwtService.extractSubject(token)).thenReturn(subject)
        whenever(jwtService.extractClaims(token)).thenReturn(claims)
        whenever(userResolver.resolveUser(subject, claims)).thenReturn(expectedUser)
        
        val currentUser = currentUserProvider.getCurrentUser()
        
        currentUser shouldBe expectedUser
    }

    "should return null when user resolver returns null" {
        val token = "valid.jwt.token"
        val subject = "user-123"
        val claims = mapOf<String, Any>()
        
        whenever(mockRequest.getHeader("Authorization")).thenReturn("Bearer $token")
        whenever(jwtService.validateToken(token)).thenReturn(true)
        whenever(jwtService.extractSubject(token)).thenReturn(subject)
        whenever(jwtService.extractClaims(token)).thenReturn(claims)
        whenever(userResolver.resolveUser(subject, claims)).thenReturn(null)
        
        val currentUser = currentUserProvider.getCurrentUser()
        
        currentUser shouldBe null
    }

    "should return null when no request context" {
        RequestContextHolder.resetRequestAttributes()
        
        val userId = currentUserProvider.getCurrentUserId()
        val claims = currentUserProvider.getCurrentUserClaims()
        val user = currentUserProvider.getCurrentUser()
        
        userId shouldBe null
        claims shouldBe null
        user shouldBe null
    }

    "should handle malformed authorization header gracefully" {
        whenever(mockRequest.getHeader("Authorization")).thenReturn("Bearer")
        
        val userId = currentUserProvider.getCurrentUserId()
        
        userId shouldBe null
    }

    "should handle JWT service exceptions gracefully" {
        val token = "problematic.jwt.token"
        
        whenever(mockRequest.getHeader("Authorization")).thenReturn("Bearer $token")
        whenever(jwtService.validateToken(token)).thenThrow(RuntimeException("JWT parsing error"))
        
        val userId = currentUserProvider.getCurrentUserId()
        
        userId shouldBe null
    }
})