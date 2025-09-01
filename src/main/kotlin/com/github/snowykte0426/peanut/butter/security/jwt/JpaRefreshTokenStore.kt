package com.github.snowykte0426.peanut.butter.security.jwt

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import jakarta.persistence.*

@Entity
@Table(name = "refresh_tokens")
data class RefreshTokenEntity(
    @Id
    val tokenId: String = "",
    
    @Column(nullable = false)
    val subject: String = "",
    
    @Column(nullable = false)
    val expiration: Instant = Instant.now(),
    
    @Column(nullable = false)
    val isBlacklisted: Boolean = false
)

@Repository
@ConditionalOnClass(JpaRepository::class)
interface RefreshTokenRepository : JpaRepository<RefreshTokenEntity, String> {
    
    fun findByTokenId(tokenId: String): RefreshTokenEntity?
    
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM RefreshTokenEntity r " +
           "WHERE r.tokenId = :tokenId AND r.isBlacklisted = false AND r.expiration > :now")
    fun isTokenValid(@Param("tokenId") tokenId: String, @Param("now") now: Instant): Boolean
    
    @Modifying
    @Query("UPDATE RefreshTokenEntity r SET r.isBlacklisted = true WHERE r.tokenId = :tokenId")
    fun blacklistToken(@Param("tokenId") tokenId: String)
    
    @Modifying
    @Query("DELETE FROM RefreshTokenEntity r WHERE r.expiration < :now")
    fun deleteExpiredTokens(@Param("now") now: Instant)
}

@Component
@ConditionalOnClass(JpaRepository::class)
@ConditionalOnProperty(name = ["peanut-butter.jwt.refresh-token-store-type"], havingValue = "RDB")
open class JpaRefreshTokenStore(
    private val refreshTokenRepository: RefreshTokenRepository
) : RefreshTokenStore {

    override fun storeRefreshToken(tokenId: String, subject: String, expiration: Instant) {
        val entity = RefreshTokenEntity(tokenId, subject, expiration, false)
        refreshTokenRepository.save(entity)
    }

    override fun isRefreshTokenValid(tokenId: String): Boolean {
        return refreshTokenRepository.isTokenValid(tokenId, Instant.now())
    }

    override fun removeRefreshToken(tokenId: String) {
        refreshTokenRepository.deleteById(tokenId)
    }

    @Transactional
    override fun blacklistRefreshToken(tokenId: String) {
        refreshTokenRepository.blacklistToken(tokenId)
    }

    @Transactional
    override fun cleanupExpiredTokens() {
        refreshTokenRepository.deleteExpiredTokens(Instant.now())
    }
}