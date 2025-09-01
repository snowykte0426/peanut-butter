package com.github.snowykte0426.peanut.butter.security.jwt

import jakarta.servlet.http.HttpServletRequest
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@Component
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
class JwtCurrentUserProvider<T>(
    private val jwtService: JwtService,
    private val jwtUserResolver: JwtUserResolver<T>? = null
) : CurrentUserProvider<T> {

    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val BEARER_PREFIX = "Bearer "
    }

    override fun getCurrentUser(): T? {
        val subject = getCurrentUserId() ?: return null
        val claims = getCurrentUserClaims() ?: emptyMap()
        return jwtUserResolver?.resolveUser(subject, claims)
    }

    override fun getCurrentUserId(): String? {
        val token = extractTokenFromRequest() ?: return null
        return jwtService.extractSubject(token)
    }

    override fun getCurrentUserClaims(): Map<String, Any>? {
        val token = extractTokenFromRequest() ?: return null
        return jwtService.extractClaims(token)
    }

    private fun extractTokenFromRequest(): String? {
        return try {
            val requestAttributes = RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes
            val request = requestAttributes?.request ?: return null
            
            val authHeader = request.getHeader(AUTHORIZATION_HEADER)
            if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
                val token = authHeader.substring(BEARER_PREFIX.length)
                if (jwtService.validateToken(token)) token else null
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}