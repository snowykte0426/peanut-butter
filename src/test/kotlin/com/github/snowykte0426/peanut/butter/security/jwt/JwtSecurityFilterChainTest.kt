package com.github.snowykte0426.peanut.butter.security.jwt

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.mockito.kotlin.*
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.DefaultSecurityFilterChain

class JwtSecurityFilterChainTest : FunSpec({

    lateinit var jwtService: JwtService
    lateinit var jwtFilterProperties: JwtFilterProperties
    lateinit var jwtSecurityFilterChain: JwtSecurityFilterChain
    
    beforeEach {
        jwtService = mock<JwtService>()
        jwtFilterProperties = JwtFilterProperties(
            enabled = true,
            excludedPaths = listOf("/api/public/**", "/health/**")
        )
        jwtSecurityFilterChain = JwtSecurityFilterChain(jwtService, jwtFilterProperties)
    }

    test("should create JWT authentication filter with correct configuration") {
        val filter = jwtSecurityFilterChain.jwtAuthenticationFilter()
        
        filter shouldNotBe null
        // Additional verification can be done by testing the filter behavior
        // which is already covered in JwtAuthenticationFilterTest
    }

    test("should create security filter chain with JWT authentication filter") {
        val httpSecurity = mock<HttpSecurity> {
            on { securityMatcher(any<org.springframework.security.web.util.matcher.RequestMatcher>()) } doReturn it
            on { addFilterBefore(any(), any()) } doReturn it
            on { authorizeHttpRequests(any()) } doReturn it
            on { csrf(any()) } doReturn it
            on { sessionManagement(any()) } doReturn it
        }
        val jwtAuthenticationFilter = JwtAuthenticationFilter(jwtService, jwtFilterProperties.excludedPaths)
        
        val mockFilterChain = mock<DefaultSecurityFilterChain>()
        whenever(httpSecurity.build()).thenReturn(mockFilterChain)
        
        val filterChain = jwtSecurityFilterChain.jwtSecurityFilterChain(httpSecurity, jwtAuthenticationFilter)
        
        filterChain shouldBe mockFilterChain
        
        verify(httpSecurity).securityMatcher(any<org.springframework.security.web.util.matcher.RequestMatcher>())
        verify(httpSecurity).addFilterBefore(eq(jwtAuthenticationFilter), any())
        verify(httpSecurity).authorizeHttpRequests(any())
        verify(httpSecurity).csrf(any())
        verify(httpSecurity).sessionManagement(any())
        verify(httpSecurity).build()
    }

    test("should handle empty excluded paths") {
        val emptyExcludedPathsProperties = JwtFilterProperties(
            enabled = true,
            excludedPaths = emptyList()
        )
        val filterChainConfig = JwtSecurityFilterChain(jwtService, emptyExcludedPathsProperties)
        
        val filter = filterChainConfig.jwtAuthenticationFilter()
        filter shouldNotBe null
    }

    test("should create filter properties with default values") {
        val defaultProperties = JwtFilterProperties()
        
        defaultProperties.enabled shouldBe false
        defaultProperties.excludedPaths shouldBe emptyList()
    }

    test("should create filter properties with custom values") {
        val customPaths = listOf("/custom/**", "/special/*")
        val customProperties = JwtFilterProperties(
            enabled = true,
            excludedPaths = customPaths
        )
        
        customProperties.enabled shouldBe true
        customProperties.excludedPaths shouldBe customPaths
    }
})

@TestConfiguration
class JwtSecurityFilterChainTestConfiguration {
    
    @Bean
    fun testJwtService(): JwtService = mock<JwtService>()
    
    @Bean 
    fun testJwtFilterProperties(): JwtFilterProperties = JwtFilterProperties(
        enabled = true,
        excludedPaths = listOf("/test/**")
    )
}