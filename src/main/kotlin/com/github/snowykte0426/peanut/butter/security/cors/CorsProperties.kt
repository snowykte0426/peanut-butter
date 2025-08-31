package com.github.snowykte0426.peanut.butter.security.cors

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Configuration properties for CORS settings.
 * Can be configured under `peanut-butter.security.cors` in application.yml.
 */
@ConfigurationProperties(prefix = "peanut-butter.security.cors")
data class CorsProperties(
    /**
     * List of allowed origins for CORS requests (default: ["*"])
     * Examples: ["https://example.com", "http://localhost:3000"]
     */
    val allowedOrigins: List<String> = listOf("*"),

    /**
     * List of allowed headers for CORS requests (default: ["*"])
     * Examples: ["Content-Type", "Authorization", "X-Requested-With"]
     */
    val allowedHeaders: List<String> = listOf("*"),

    /**
     * Map of HTTP methods and their allowed status for CORS requests
     * Key: HTTP method name, Value: true to allow, false to deny
     * Default: All standard methods are allowed
     */
    val allowedMethods: Map<String, Boolean> = mapOf(
        "GET" to true,
        "POST" to true,
        "PUT" to true,
        "DELETE" to true,
        "PATCH" to true,
        "HEAD" to true,
        "OPTIONS" to true,
        "TRACE" to false
    ),

    /**
     * Whether to allow credentials in CORS requests (default: true)
     */
    val allowCredentials: Boolean = true,

    /**
     * Maximum age (in seconds) for preflight request caching (default: 3600)
     */
    val maxAge: Long = 3600L,

    /**
     * List of exposed headers for CORS responses (default: empty)
     * Headers that the browser is allowed to access
     */
    val exposedHeaders: List<String> = emptyList(),

    /**
     * Enable CORS configuration (default: true)
     */
    val enabled: Boolean = true
)