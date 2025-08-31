package com.github.snowykte0426.peanut.butter.security.cors

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.web.cors.CorsConfiguration as SpringCorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

/**
 * Auto-configuration for CORS settings.
 * Automatically configures CORS based on CorsProperties when enabled.
 */
@Configuration
@EnableConfigurationProperties(CorsProperties::class)
@ConditionalOnProperty(
    prefix = "peanut-butter.security.cors",
    name = ["enabled"],
    havingValue = "true",
    matchIfMissing = true
)
open class CorsConfiguration(
    private val corsProperties: CorsProperties
) {

    /**
     * Creates and configures a CorsConfigurationSource bean.
     * This bean can be used by Spring Security's SecurityFilterChain.
     *
     * @return Configured CorsConfigurationSource
     */
    @Bean
    open fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = SpringCorsConfiguration().apply {
            allowedOrigins = corsProperties.allowedOrigins
            allowedHeaders = corsProperties.allowedHeaders
            allowedMethods = corsProperties.allowedMethods
                .filterValues { it }
                .keys
                .toList()
            allowCredentials = corsProperties.allowCredentials
            maxAge = corsProperties.maxAge
            if (corsProperties.exposedHeaders.isNotEmpty()) {
                exposedHeaders = corsProperties.exposedHeaders
            }
        }

        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", configuration)
        }
    }

    /**
     * Creates a CorsConfiguration for programmatic use.
     * Useful for manual SecurityFilterChain configuration.
     *
     * @return Configured SpringCorsConfiguration
     */
    @Bean
    open fun springCorsConfiguration(): SpringCorsConfiguration {
        return SpringCorsConfiguration().apply {
            allowedOrigins = corsProperties.allowedOrigins
            allowedHeaders = corsProperties.allowedHeaders
            allowedMethods = corsProperties.allowedMethods
                .filterValues { it }
                .keys
                .toList()
            allowCredentials = corsProperties.allowCredentials
            maxAge = corsProperties.maxAge
            if (corsProperties.exposedHeaders.isNotEmpty()) {
                exposedHeaders = corsProperties.exposedHeaders
            }
        }
    }
}