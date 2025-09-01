package com.github.snowykte0426.peanut.butter.security.jwt

import java.time.Instant

interface JwtService {
    fun generateAccessToken(subject: String, claims: Map<String, Any> = emptyMap()): String
    fun generateRefreshToken(subject: String): String
    fun validateToken(token: String): Boolean
    fun extractSubject(token: String): String?
    fun extractClaims(token: String): Map<String, Any>?
    fun extractExpiration(token: String): Instant?
    fun isTokenExpired(token: String): Boolean
    fun refreshTokens(refreshToken: String): TokenPair?
}

data class TokenPair(
    val accessToken: String,
    val refreshToken: String?
)