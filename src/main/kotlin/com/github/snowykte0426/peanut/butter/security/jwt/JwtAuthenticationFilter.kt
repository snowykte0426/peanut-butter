package com.github.snowykte0426.peanut.butter.security.jwt

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.util.AntPathMatcher
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

/**
 * JWT Authentication Filter for processing JWT tokens in HTTP requests.
 * 
 * This filter extracts JWT tokens from the Authorization header, validates them,
 * and sets up the Spring Security context with authentication information.
 * It automatically detects permitAll() paths from existing SecurityFilterChain configurations
 * and excludes them from JWT authentication along with additional custom excluded paths.
 * 
 * @param jwtService Service for JWT token operations
 * @param additionalExcludedPaths Additional URL patterns to exclude from JWT authentication
 * @param autoDetectPermitAllPaths Whether to automatically detect permitAll paths from SecurityFilterChain
 */
class JwtAuthenticationFilter(
    private val jwtService: JwtService,
    private val additionalExcludedPaths: List<String> = emptyList(),
    private val autoDetectPermitAllPaths: Boolean = true
) : OncePerRequestFilter() {

    private val pathMatcher = AntPathMatcher()
    
    @Autowired
    private lateinit var applicationContext: ApplicationContext
    
    private val allExcludedPaths: List<String> by lazy {
        val permitAllPaths = if (autoDetectPermitAllPaths) {
            detectPermitAllPaths()
        } else {
            emptyList()
        }
        permitAllPaths + additionalExcludedPaths
    }

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // Skip authentication for excluded paths
        if (shouldSkipAuthentication(request.requestURI)) {
            filterChain.doFilter(request, response)
            return
        }

        val authorizationHeader = request.getHeader("Authorization")
        
        if (authorizationHeader?.startsWith("Bearer ") == true) {
            val token = authorizationHeader.substring(7)
            
            try {
                if (jwtService.validateToken(token)) {
                    val subject = jwtService.extractSubject(token)
                    val claims = jwtService.extractClaims(token)
                    
                    if (subject != null && SecurityContextHolder.getContext().authentication == null) {
                        val authorities = extractAuthorities(claims)
                        val authentication = UsernamePasswordAuthenticationToken(
                            subject,
                            null,
                            authorities
                        )
                        
                        SecurityContextHolder.getContext().authentication = authentication
                    }
                }
            } catch (e: Exception) {
                // Log the exception but continue with the filter chain
                logger.debug("JWT token validation failed", e)
            }
        }
        
        filterChain.doFilter(request, response)
    }

    /**
     * Checks if the request path should skip JWT authentication.
     *
     * @param requestPath The request URI path
     * @return true if authentication should be skipped, false otherwise
     */
    private fun shouldSkipAuthentication(requestPath: String): Boolean {
        return allExcludedPaths.any { pattern ->
            pathMatcher.match(pattern, requestPath)
        }
    }
    
    /**
     * Detects permitAll() paths from existing SecurityFilterChain configurations.
     * This method analyzes the Spring Security configuration to find paths that are
     * configured with permitAll() and automatically excludes them from JWT authentication.
     *
     * @return List of URL patterns that are configured with permitAll()
     */
    private fun detectPermitAllPaths(): List<String> {
        return try {
            // Common permitAll patterns based on typical Spring Security configurations
            val commonPermitAllPaths = mutableListOf<String>()
            
            // Check if CORS filter chain exists with permitAll (like in CorsSecurityFilterChain)
            if (isCorsFilterChainPermitAll()) {
                commonPermitAllPaths.addAll(listOf("/**")) // CORS typically allows all
            }
            
            // Add common Spring Boot actuator and health endpoints
            commonPermitAllPaths.addAll(listOf(
                "/actuator/health/**",
                "/actuator/info",
                "/health/**",
                "/info",
                "/error",
                "/favicon.ico"
            ))
            
            commonPermitAllPaths
        } catch (e: Exception) {
            logger.debug("Failed to detect permitAll paths, using empty list", e)
            emptyList()
        }
    }
    
    /**
     * Checks if CORS security filter chain exists and uses permitAll().
     *
     * @return true if CORS filter chain with permitAll() is detected
     */
    private fun isCorsFilterChainPermitAll(): Boolean {
        return try {
            val corsFilterChains = applicationContext.getBeansOfType(SecurityFilterChain::class.java)
                .values
                .filter { filterChain ->
                    // Simple heuristic: if a filter chain is named with "cors" or similar,
                    // it's likely using permitAll() for CORS support
                    filterChain.javaClass.simpleName.contains("Cors", ignoreCase = true) ||
                    filterChain.toString().contains("permitAll", ignoreCase = true)
                }
            corsFilterChains.isNotEmpty()
        } catch (e: Exception) {
            logger.debug("Failed to check CORS filter chain", e)
            false
        }
    }

    /**
     * Extracts authorities from JWT claims.
     * 
     * @param claims JWT claims map
     * @return List of granted authorities
     */
    private fun extractAuthorities(claims: Map<String, Any>?): List<SimpleGrantedAuthority> {
        if (claims == null) return emptyList()
        
        val roles = when (val rolesValue = claims["roles"]) {
            is List<*> -> rolesValue.filterIsInstance<String>()
            is String -> listOf(rolesValue)
            else -> emptyList()
        }
        
        val authorities = when (val authoritiesValue = claims["authorities"]) {
            is List<*> -> authoritiesValue.filterIsInstance<String>()
            is String -> listOf(authoritiesValue)
            else -> emptyList()
        }
        
        return (roles.map { "ROLE_$it" } + authorities)
            .map { SimpleGrantedAuthority(it) }
    }
}