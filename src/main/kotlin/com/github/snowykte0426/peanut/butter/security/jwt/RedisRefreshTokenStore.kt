package com.github.snowykte0426.peanut.butter.security.jwt

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.Instant
import java.util.concurrent.TimeUnit

@Component
@ConditionalOnClass(RedisTemplate::class)
@ConditionalOnProperty(name = ["peanut-butter.jwt.refresh-token-store-type"], havingValue = "REDIS")
class RedisRefreshTokenStore(
    private val redisTemplate: RedisTemplate<String, String>,
    private val objectMapper: ObjectMapper = ObjectMapper().apply {
        findAndRegisterModules()
    }
) : RefreshTokenStore {

    private companion object {
        private const val TOKEN_PREFIX = "jwt:refresh:"
        private const val BLACKLIST_PREFIX = "jwt:blacklist:"
    }

    override fun storeRefreshToken(tokenId: String, subject: String, expiration: Instant) {
        val record = RefreshTokenRecord(tokenId, subject, expiration)
        val key = TOKEN_PREFIX + tokenId
        val value = objectMapper.writeValueAsString(record)
        val ttl = Duration.between(Instant.now(), expiration).seconds
        
        redisTemplate.opsForValue().set(key, value, ttl, TimeUnit.SECONDS)
    }

    override fun isRefreshTokenValid(tokenId: String): Boolean {
        val tokenKey = TOKEN_PREFIX + tokenId
        val blacklistKey = BLACKLIST_PREFIX + tokenId
        
        val tokenExists = redisTemplate.hasKey(tokenKey) == true
        val isBlacklisted = redisTemplate.hasKey(blacklistKey) == true
        
        return tokenExists && !isBlacklisted
    }

    override fun removeRefreshToken(tokenId: String) {
        redisTemplate.delete(TOKEN_PREFIX + tokenId)
    }

    override fun blacklistRefreshToken(tokenId: String) {
        val tokenKey = TOKEN_PREFIX + tokenId
        val blacklistKey = BLACKLIST_PREFIX + tokenId
        
        val tokenValue = redisTemplate.opsForValue().get(tokenKey)
        if (tokenValue != null) {
            val record = objectMapper.readValue(tokenValue, RefreshTokenRecord::class.java)
            val ttl = Duration.between(Instant.now(), record.expiration).seconds
            if (ttl > 0) {
                redisTemplate.opsForValue().set(blacklistKey, "blacklisted", ttl, TimeUnit.SECONDS)
            }
        }
    }

    override fun cleanupExpiredTokens() {
        // Redis handles expiration automatically
    }
}