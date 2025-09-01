package com.github.snowykte0426.peanut.butter.security.jwt

import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.security.Key
import java.time.Instant
import java.util.*
import javax.crypto.SecretKey

@Service
class DefaultJwtService(
    private val jwtProperties: JwtProperties,
    private val refreshTokenStore: RefreshTokenStore? = null
) : JwtService {

    private val logger = LoggerFactory.getLogger(DefaultJwtService::class.java)
    private val secretKey: SecretKey = Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray(StandardCharsets.UTF_8))

    override fun generateAccessToken(subject: String, claims: Map<String, Any>): String {
        val now = Instant.now()
        val expiration = now.plus(jwtProperties.accessTokenExpiry)
        
        return Jwts.builder()
            .claims(claims)
            .subject(subject)
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiration))
            .signWith(secretKey)
            .compact()
    }

    override fun generateRefreshToken(subject: String): String {
        if (!jwtProperties.refreshTokenEnabled) {
            throw IllegalStateException("Refresh token is disabled")
        }

        val now = Instant.now()
        val expiration = now.plus(jwtProperties.refreshTokenExpiry)
        val tokenId = UUID.randomUUID().toString()
        
        val refreshToken = Jwts.builder()
            .subject(subject)
            .id(tokenId)
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiration))
            .signWith(secretKey)
            .compact()

        if (jwtProperties.refreshTokenMode == RefreshTokenMode.STORE_AND_VALIDATE) {
            refreshTokenStore?.storeRefreshToken(tokenId, subject, expiration)
        }

        return refreshToken
    }

    override fun validateToken(token: String): Boolean {
        return try {
            val claims = parseToken(token)
            val tokenId = claims.id
            
            if (jwtProperties.refreshTokenEnabled && 
                jwtProperties.refreshTokenMode == RefreshTokenMode.STORE_AND_VALIDATE && 
                tokenId != null) {
                refreshTokenStore?.isRefreshTokenValid(tokenId) == true
            } else {
                true
            }
        } catch (e: Exception) {
            logger.debug("Token validation failed", e)
            false
        }
    }

    override fun extractSubject(token: String): String? {
        return try {
            parseToken(token).subject
        } catch (e: Exception) {
            logger.debug("Failed to extract subject from token", e)
            null
        }
    }

    override fun extractClaims(token: String): Map<String, Any>? {
        return try {
            parseToken(token).filterNot { (key, _) -> 
                key in setOf("sub", "iat", "exp", "jti") 
            }
        } catch (e: Exception) {
            logger.debug("Failed to extract claims from token", e)
            null
        }
    }

    override fun extractExpiration(token: String): Instant? {
        return try {
            parseToken(token).expiration?.toInstant()
        } catch (e: Exception) {
            logger.debug("Failed to extract expiration from token", e)
            null
        }
    }

    override fun isTokenExpired(token: String): Boolean {
        val expiration = extractExpiration(token) ?: return true
        return expiration.isBefore(Instant.now())
    }

    override fun refreshTokens(refreshToken: String): TokenPair? {
        if (!jwtProperties.refreshTokenEnabled) {
            throw IllegalStateException("Refresh token is disabled")
        }

        return try {
            val claims = parseToken(refreshToken)
            val subject = claims.subject ?: return null
            val tokenId = claims.id

            if (jwtProperties.refreshTokenMode == RefreshTokenMode.STORE_AND_VALIDATE) {
                if (tokenId == null || refreshTokenStore?.isRefreshTokenValid(tokenId) != true) {
                    return null
                }

                if (jwtProperties.refreshTokenRotationEnabled) {
                    handleUsedRefreshToken(tokenId)
                }
            }

            val newAccessToken = generateAccessToken(subject)
            val newRefreshToken = if (jwtProperties.refreshTokenRotationEnabled) {
                generateRefreshToken(subject)
            } else {
                refreshToken
            }

            TokenPair(newAccessToken, newRefreshToken)
        } catch (e: Exception) {
            logger.debug("Failed to refresh tokens", e)
            null
        }
    }

    private fun parseToken(token: String): Claims {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload
    }

    private fun handleUsedRefreshToken(tokenId: String) {
        when (jwtProperties.usedRefreshTokenHandling) {
            UsedRefreshTokenHandling.REMOVE -> {
                refreshTokenStore?.removeRefreshToken(tokenId)
            }
            UsedRefreshTokenHandling.BLACKLIST -> {
                refreshTokenStore?.blacklistRefreshToken(tokenId)
            }
        }
    }
}