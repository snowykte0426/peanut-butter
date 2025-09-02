package com.github.snowykte0426.peanut.butter.security.jwt

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher

/**
 * Auto-configuration for JWT authentication filter integration with Spring Security.
 * 
 * This configuration automatically sets up JWT authentication filter when enabled
 * and integrates it with the existing Spring Security filter chain.
 */
@Configuration
@ConditionalOnClass(name = ["org.springframework.security.web.SecurityFilterChain"])
@ConditionalOnProperty(
    prefix = "peanut-butter.security.jwt.filter",
    name = ["enabled"],
    havingValue = "true"
)
@EnableConfigurationProperties(JwtFilterProperties::class)
open class JwtSecurityFilterChain(
    private val jwtService: JwtService,
    private val jwtFilterProperties: JwtFilterProperties
) {

    /**
     * Creates JWT authentication filter bean.
     * 
     * @return Configured JwtAuthenticationFilter
     */
    @Bean
    @ConditionalOnMissingBean
    open fun jwtAuthenticationFilter(): JwtAuthenticationFilter {
        return JwtAuthenticationFilter(
            jwtService = jwtService,
            additionalExcludedPaths = jwtFilterProperties.excludedPaths,
            autoDetectPermitAllPaths = jwtFilterProperties.autoDetectPermitAllPaths
        )
    }

    /**
     * Creates a SecurityFilterChain with JWT authentication filter.
     * 
     * This filter chain has high precedence to ensure JWT authentication
     * is applied before other security configurations.
     * 
     * @param http HttpSecurity configuration
     * @param jwtAuthenticationFilter JWT authentication filter
     * @return Configured SecurityFilterChain with JWT authentication
     */
    @Bean
    @ConditionalOnMissingBean(name = ["jwtSecurityFilterChain"])
    @Order(100) // High precedence, but allows override
    open fun jwtSecurityFilterChain(
        http: HttpSecurity,
        jwtAuthenticationFilter: JwtAuthenticationFilter
    ): SecurityFilterChain {
        return http
            .securityMatcher(createSecurityMatcher())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .authorizeHttpRequests { auth ->
                // Configure authorization based on existing permitAll patterns
                jwtFilterProperties.excludedPaths.forEach { pattern: String ->
                    auth.requestMatchers(pattern).permitAll()
                }
                auth.anyRequest().authenticated()
            }
            .csrf { csrf -> csrf.disable() }
            .sessionManagement { session -> 
                session.sessionCreationPolicy(
                    org.springframework.security.config.http.SessionCreationPolicy.STATELESS
                )
            }
            .build()
    }

    /**
     * Creates a request matcher that applies to all requests except excluded paths.
     * 
     * @return RequestMatcher for JWT filter chain
     */
    private fun createSecurityMatcher(): RequestMatcher {
        // If no excluded paths are specified, match all requests
        if (jwtFilterProperties.excludedPaths.isEmpty()) {
            return RequestMatcher { true }
        }
        
        // Create matchers for excluded paths
        val excludedMatchers = jwtFilterProperties.excludedPaths.map { pattern: String ->
            AntPathRequestMatcher(pattern)
        }
        
        // Return matcher that matches requests NOT in excluded paths
        return RequestMatcher { request ->
            excludedMatchers.none { matcher -> matcher.matches(request) }
        }
    }
}