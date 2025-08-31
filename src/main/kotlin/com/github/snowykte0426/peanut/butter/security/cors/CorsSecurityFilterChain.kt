package com.github.snowykte0426.peanut.butter.security.cors

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfigurationSource

/**
 * Auto-configuration for Spring Security integration with CORS.
 * Provides a default SecurityFilterChain with CORS configuration when Spring Security is present.
 */
@Configuration
@ConditionalOnClass(name = ["org.springframework.security.web.SecurityFilterChain"])
@ConditionalOnProperty(
    prefix = "peanut-butter.security.cors",
    name = ["enabled"],
    havingValue = "true",
    matchIfMissing = true
)
@EnableWebSecurity
open class CorsSecurityFilterChain(
    private val corsConfigurationSource: CorsConfigurationSource
) {

    /**
     * Creates a SecurityFilterChain with CORS configuration.
     * This provides a minimal security configuration that enables CORS.
     * 
     * Note: This is a basic configuration. For production applications,
     * you should create your own SecurityFilterChain with proper authentication
     * and authorization rules, and use the corsConfigurationSource bean.
     *
     * @param http HttpSecurity configuration
     * @return Configured SecurityFilterChain
     */
    @Bean
    open fun corsSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .cors { cors ->
                cors.configurationSource(corsConfigurationSource)
            }
            .csrf { csrf ->
                csrf.disable() // Disable CSRF for APIs, enable if needed
            }
            .authorizeHttpRequests { auth ->
                auth.anyRequest().permitAll() // Allow all requests by default
            }
            .build()
    }
}