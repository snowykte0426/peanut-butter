package com.github.snowykte0426.peanut.butter.security.jwt

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.mockito.kotlin.*
import org.springframework.security.core.context.SecurityContextHolder

class JwtAuthenticationFilterTest : FunSpec({
    
    lateinit var jwtService: JwtService
    lateinit var request: HttpServletRequest
    lateinit var response: HttpServletResponse
    lateinit var filterChain: FilterChain
    
    beforeEach {
        jwtService = mock<JwtService>()
        request = mock<HttpServletRequest>()
        response = mock<HttpServletResponse>()
        filterChain = mock<FilterChain>()
        SecurityContextHolder.clearContext()
    }
    
    afterEach {
        SecurityContextHolder.clearContext()
    }

    test("should process valid JWT token and set authentication") {
        val token = "valid.jwt.token"
        val subject = "user123"
        val claims = mapOf(
            "roles" to listOf("USER", "ADMIN"),
            "authorities" to listOf("READ", "WRITE")
        )
        
        whenever(request.getHeader("Authorization")).thenReturn("Bearer $token")
        whenever(request.requestURI).thenReturn("/api/secure")
        whenever(jwtService.validateToken(token)).thenReturn(true)
        whenever(jwtService.extractSubject(token)).thenReturn(subject)
        whenever(jwtService.extractClaims(token)).thenReturn(claims)
        
        val filter = JwtAuthenticationFilter(jwtService)
        filter.doFilter(request, response, filterChain)
        
        val authentication = SecurityContextHolder.getContext().authentication
        authentication.shouldNotBeNull()
        authentication.principal shouldBe subject
        authentication.authorities should { authorities ->
            authorities.map { it.authority }.toSet() == setOf("ROLE_USER", "ROLE_ADMIN", "READ", "WRITE")
        }
        
        verify(filterChain).doFilter(request, response)
    }

    test("should skip authentication for excluded paths") {
        val excludedPaths = listOf("/api/public/**", "/health/*")
        
        whenever(request.requestURI).thenReturn("/api/public/info")
        whenever(request.getHeader("Authorization")).thenReturn("Bearer valid.token")
        
        val filter = JwtAuthenticationFilter(jwtService, excludedPaths)
        filter.doFilter(request, response, filterChain)
        
        val authentication = SecurityContextHolder.getContext().authentication
        authentication.shouldBeNull()
        
        verify(jwtService, never()).validateToken(any())
        verify(filterChain).doFilter(request, response)
    }

    test("should continue filter chain when no Authorization header present") {
        whenever(request.requestURI).thenReturn("/api/secure")
        whenever(request.getHeader("Authorization")).thenReturn(null)
        
        val filter = JwtAuthenticationFilter(jwtService)
        filter.doFilter(request, response, filterChain)
        
        val authentication = SecurityContextHolder.getContext().authentication
        authentication.shouldBeNull()
        
        verify(jwtService, never()).validateToken(any())
        verify(filterChain).doFilter(request, response)
    }

    test("should continue filter chain when Authorization header does not start with Bearer") {
        whenever(request.requestURI).thenReturn("/api/secure")
        whenever(request.getHeader("Authorization")).thenReturn("Basic credentials")
        
        val filter = JwtAuthenticationFilter(jwtService)
        filter.doFilter(request, response, filterChain)
        
        val authentication = SecurityContextHolder.getContext().authentication
        authentication.shouldBeNull()
        
        verify(jwtService, never()).validateToken(any())
        verify(filterChain).doFilter(request, response)
    }

    test("should continue filter chain when JWT token is invalid") {
        val token = "invalid.jwt.token"
        
        whenever(request.getHeader("Authorization")).thenReturn("Bearer $token")
        whenever(request.requestURI).thenReturn("/api/secure")
        whenever(jwtService.validateToken(token)).thenReturn(false)
        
        val filter = JwtAuthenticationFilter(jwtService)
        filter.doFilter(request, response, filterChain)
        
        val authentication = SecurityContextHolder.getContext().authentication
        authentication.shouldBeNull()
        
        verify(jwtService).validateToken(token)
        verify(jwtService, never()).extractSubject(any())
        verify(filterChain).doFilter(request, response)
    }

    test("should handle JWT validation exception gracefully") {
        val token = "malformed.token"
        
        whenever(request.getHeader("Authorization")).thenReturn("Bearer $token")
        whenever(request.requestURI).thenReturn("/api/secure")
        whenever(jwtService.validateToken(token)).thenThrow(RuntimeException("Invalid token"))
        
        val filter = JwtAuthenticationFilter(jwtService)
        filter.doFilter(request, response, filterChain)
        
        val authentication = SecurityContextHolder.getContext().authentication
        authentication.shouldBeNull()
        
        verify(filterChain).doFilter(request, response)
    }

    test("should not set authentication when subject is null") {
        val token = "valid.jwt.token"
        
        whenever(request.getHeader("Authorization")).thenReturn("Bearer $token")
        whenever(request.requestURI).thenReturn("/api/secure")
        whenever(jwtService.validateToken(token)).thenReturn(true)
        whenever(jwtService.extractSubject(token)).thenReturn(null)
        
        val filter = JwtAuthenticationFilter(jwtService)
        filter.doFilter(request, response, filterChain)
        
        val authentication = SecurityContextHolder.getContext().authentication
        authentication.shouldBeNull()
        
        verify(filterChain).doFilter(request, response)
    }

    test("should handle empty roles and authorities claims") {
        val token = "valid.jwt.token"
        val subject = "user123"
        val claims = mapOf<String, Any>()
        
        whenever(request.getHeader("Authorization")).thenReturn("Bearer $token")
        whenever(request.requestURI).thenReturn("/api/secure")
        whenever(jwtService.validateToken(token)).thenReturn(true)
        whenever(jwtService.extractSubject(token)).thenReturn(subject)
        whenever(jwtService.extractClaims(token)).thenReturn(claims)
        
        val filter = JwtAuthenticationFilter(jwtService)
        filter.doFilter(request, response, filterChain)
        
        val authentication = SecurityContextHolder.getContext().authentication
        authentication.shouldNotBeNull()
        authentication.authorities.size shouldBe 0
        
        verify(filterChain).doFilter(request, response)
    }

    test("should handle single role and authority as string") {
        val token = "valid.jwt.token"
        val subject = "user123"
        val claims = mapOf(
            "roles" to "USER",
            "authorities" to "READ"
        )
        
        whenever(request.getHeader("Authorization")).thenReturn("Bearer $token")
        whenever(request.requestURI).thenReturn("/api/secure")
        whenever(jwtService.validateToken(token)).thenReturn(true)
        whenever(jwtService.extractSubject(token)).thenReturn(subject)
        whenever(jwtService.extractClaims(token)).thenReturn(claims)
        
        val filter = JwtAuthenticationFilter(jwtService)
        filter.doFilter(request, response, filterChain)
        
        val authentication = SecurityContextHolder.getContext().authentication
        authentication.shouldNotBeNull()
        authentication.authorities.map { it.authority }.toSet() shouldBe setOf("ROLE_USER", "READ")
        
        verify(filterChain).doFilter(request, response)
    }

    test("should match excluded paths correctly with Ant patterns") {
        val excludedPaths = listOf("/api/public/**", "/health", "/admin/*/status")
        val filter = JwtAuthenticationFilter(jwtService, excludedPaths)
        
        // Test excluded path
        whenever(request.requestURI).thenReturn("/api/public/info")
        whenever(request.getHeader("Authorization")).thenReturn("Bearer token")
        
        filter.doFilter(request, response, filterChain)
        
        verify(jwtService, never()).validateToken(any())
        verify(filterChain).doFilter(request, response)
        
        // Reset mocks
        reset(jwtService, filterChain)
        
        // Test non-excluded path
        whenever(request.requestURI).thenReturn("/api/private/data")
        whenever(request.getHeader("Authorization")).thenReturn("Bearer token")
        whenever(jwtService.validateToken("token")).thenReturn(false)
        
        filter.doFilter(request, response, filterChain)
        
        verify(jwtService).validateToken("token")
        verify(filterChain).doFilter(request, response)
    }
})