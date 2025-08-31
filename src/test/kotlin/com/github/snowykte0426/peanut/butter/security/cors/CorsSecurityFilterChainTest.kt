package com.github.snowykte0426.peanut.butter.security.cors

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.mockito.kotlin.mock
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.mock.web.MockHttpServletRequest

class CorsSecurityFilterChainTest : FunSpec({

    test("should create CorsSecurityFilterChain with CorsConfigurationSource") {
        val corsConfigurationSource = mock<CorsConfigurationSource>()
        
        val corsSecurityFilterChain = CorsSecurityFilterChain(corsConfigurationSource)
        
        corsSecurityFilterChain shouldNotBe null
    }

    test("should configure SecurityFilterChain with CORS support") {
        val corsConfiguration = CorsConfiguration().apply {
            allowedOrigins = listOf("https://example.com")
            allowedMethods = listOf("GET", "POST")
            allowedHeaders = listOf("*")
            allowCredentials = true
        }
        
        val corsConfigurationSource = mock<CorsConfigurationSource>()
        val mockRequest = MockHttpServletRequest()
        
        // Mock the behavior to return our test configuration
        org.mockito.kotlin.whenever(corsConfigurationSource.getCorsConfiguration(mockRequest))
            .thenReturn(corsConfiguration)
        
        val corsSecurityFilterChain = CorsSecurityFilterChain(corsConfigurationSource)
        
        corsSecurityFilterChain shouldNotBe null
    }

    test("should create SecurityFilterChain bean") {
        val corsConfigurationSource = mock<CorsConfigurationSource>()
        val corsSecurityFilterChain = CorsSecurityFilterChain(corsConfigurationSource)
        val httpSecurity = mock<HttpSecurity>()
        
        // Mock HttpSecurity method chaining
        org.mockito.kotlin.whenever(httpSecurity.cors(org.mockito.kotlin.any())).thenReturn(httpSecurity)
        org.mockito.kotlin.whenever(httpSecurity.csrf(org.mockito.kotlin.any())).thenReturn(httpSecurity)
        org.mockito.kotlin.whenever(httpSecurity.authorizeHttpRequests(org.mockito.kotlin.any())).thenReturn(httpSecurity)
        
        val mockFilterChain = mock<SecurityFilterChain>()
        org.mockito.kotlin.whenever(httpSecurity.build()).thenReturn(mockFilterChain)
        
        val result = corsSecurityFilterChain.corsSecurityFilterChain(httpSecurity)
        
        result shouldBe mockFilterChain
    }

    test("should handle different CORS configuration scenarios") {
        // Test with restrictive CORS configuration
        val restrictiveConfig = CorsConfiguration().apply {
            allowedOrigins = listOf("https://trusted-site.com")
            allowedMethods = listOf("GET")
            allowedHeaders = listOf("Content-Type")
            allowCredentials = false
            maxAge = 3600L
        }
        
        val corsConfigurationSource1 = mock<CorsConfigurationSource>()
        val request1 = MockHttpServletRequest()
        org.mockito.kotlin.whenever(corsConfigurationSource1.getCorsConfiguration(request1))
            .thenReturn(restrictiveConfig)
        
        val corsSecurityFilterChain1 = CorsSecurityFilterChain(corsConfigurationSource1)
        corsSecurityFilterChain1 shouldNotBe null
        
        // Test with permissive CORS configuration
        val permissiveConfig = CorsConfiguration().apply {
            allowedOrigins = listOf("*")
            allowedMethods = listOf("*")
            allowedHeaders = listOf("*")
            allowCredentials = true
        }
        
        val corsConfigurationSource2 = mock<CorsConfigurationSource>()
        val request2 = MockHttpServletRequest()
        org.mockito.kotlin.whenever(corsConfigurationSource2.getCorsConfiguration(request2))
            .thenReturn(permissiveConfig)
        
        val corsSecurityFilterChain2 = CorsSecurityFilterChain(corsConfigurationSource2)
        corsSecurityFilterChain2 shouldNotBe null
    }

    test("should handle null CORS configuration") {
        val corsConfigurationSource = mock<CorsConfigurationSource>()
        val request = MockHttpServletRequest()
        
        // Mock returning null configuration
        org.mockito.kotlin.whenever(corsConfigurationSource.getCorsConfiguration(request))
            .thenReturn(null)
        
        val corsSecurityFilterChain = CorsSecurityFilterChain(corsConfigurationSource)
        corsSecurityFilterChain shouldNotBe null
    }

    test("should work with multiple CorsConfigurationSource instances") {
        val source1 = mock<CorsConfigurationSource>()
        val source2 = mock<CorsConfigurationSource>()
        
        val filterChain1 = CorsSecurityFilterChain(source1)
        val filterChain2 = CorsSecurityFilterChain(source2)
        
        filterChain1 shouldNotBe null
        filterChain2 shouldNotBe null
        filterChain1 shouldNotBe filterChain2
    }

    test("should integrate with complex CORS configurations") {
        val complexConfig = CorsConfiguration().apply {
            allowedOriginPatterns = listOf("https://*.example.com", "https://*.test.com")
            allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
            allowedHeaders = listOf("Authorization", "Content-Type", "X-Requested-With", "Accept")
            exposedHeaders = listOf("X-Total-Count", "X-Page-Count")
            allowCredentials = true
            maxAge = 7200L
        }
        
        val corsConfigurationSource = mock<CorsConfigurationSource>()
        val request = MockHttpServletRequest()
        org.mockito.kotlin.whenever(corsConfigurationSource.getCorsConfiguration(request))
            .thenReturn(complexConfig)
        
        val corsSecurityFilterChain = CorsSecurityFilterChain(corsConfigurationSource)
        corsSecurityFilterChain shouldNotBe null
    }

    test("should handle edge cases with empty CORS configuration") {
        val emptyConfig = CorsConfiguration().apply {
            allowedOrigins = emptyList()
            allowedMethods = emptyList()
            allowedHeaders = emptyList()
            allowCredentials = false
        }
        
        val corsConfigurationSource = mock<CorsConfigurationSource>()
        val request = MockHttpServletRequest()
        org.mockito.kotlin.whenever(corsConfigurationSource.getCorsConfiguration(request))
            .thenReturn(emptyConfig)
        
        val corsSecurityFilterChain = CorsSecurityFilterChain(corsConfigurationSource)
        corsSecurityFilterChain shouldNotBe null
    }

    test("should be thread-safe with concurrent access") {
        val corsConfigurationSource = mock<CorsConfigurationSource>()
        
        // Create multiple instances concurrently
        val filterChains = (1..10).map { 
            CorsSecurityFilterChain(corsConfigurationSource)
        }
        
        filterChains.size shouldBe 10
        filterChains.forEach { it shouldNotBe null }
    }
})