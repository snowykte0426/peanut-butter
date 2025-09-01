package com.github.snowykte0426.peanut.butter.security.jwt

interface CurrentUserProvider<T> {
    fun getCurrentUser(): T?
    fun getCurrentUserId(): String?
    fun getCurrentUserClaims(): Map<String, Any>?
}

interface JwtUserResolver<T> {
    fun resolveUser(subject: String, claims: Map<String, Any>): T?
}