package com.github.snowykte0426.peanut.butter.security.jwt

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.nulls.shouldBeNull

class CurrentUserProviderTest : FunSpec({

    data class TestUser(val id: String, val name: String)

    class TestCurrentUserProvider : CurrentUserProvider<TestUser> {
        private var currentUser: TestUser? = null
        private var currentUserId: String? = null
        private var currentUserClaims: Map<String, Any>? = null

        fun setCurrentUser(user: TestUser?, userId: String?, claims: Map<String, Any>?) {
            this.currentUser = user
            this.currentUserId = userId
            this.currentUserClaims = claims
        }

        override fun getCurrentUser(): TestUser? = currentUser
        override fun getCurrentUserId(): String? = currentUserId
        override fun getCurrentUserClaims(): Map<String, Any>? = currentUserClaims
    }

    class TestJwtUserResolver : JwtUserResolver<TestUser> {
        override fun resolveUser(subject: String, claims: Map<String, Any>): TestUser? {
            return if (subject == "valid-user") {
                TestUser(subject, claims["name"] as? String ?: "Unknown")
            } else null
        }
    }

    test("getCurrentUser should return current user") {
        val provider = TestCurrentUserProvider()
        val testUser = TestUser("123", "Test User")

        provider.setCurrentUser(testUser, "123", emptyMap())

        provider.getCurrentUser() shouldBe testUser
    }

    test("getCurrentUser should return null when no user is set") {
        val provider = TestCurrentUserProvider()

        provider.getCurrentUser().shouldBeNull()
    }

    test("getCurrentUserId should return current user id") {
        val provider = TestCurrentUserProvider()

        provider.setCurrentUser(null, "user123", null)

        provider.getCurrentUserId() shouldBe "user123"
    }

    test("getCurrentUserId should return null when no user id is set") {
        val provider = TestCurrentUserProvider()

        provider.getCurrentUserId().shouldBeNull()
    }

    test("getCurrentUserClaims should return current user claims") {
        val provider = TestCurrentUserProvider()
        val claims = mapOf("role" to "admin", "department" to "IT")

        provider.setCurrentUser(null, null, claims)

        provider.getCurrentUserClaims() shouldBe claims
    }

    test("getCurrentUserClaims should return null when no claims are set") {
        val provider = TestCurrentUserProvider()

        provider.getCurrentUserClaims().shouldBeNull()
    }

    test("JwtUserResolver should resolve user from subject and claims") {
        val resolver = TestJwtUserResolver()
        val claims = mapOf("name" to "John Doe", "role" to "user")

        val user = resolver.resolveUser("valid-user", claims)

        user shouldBe TestUser("valid-user", "John Doe")
    }

    test("JwtUserResolver should return null for invalid subject") {
        val resolver = TestJwtUserResolver()
        val claims = mapOf("name" to "John Doe")

        val user = resolver.resolveUser("invalid-user", claims)

        user.shouldBeNull()
    }

    test("JwtUserResolver should handle missing name claim") {
        val resolver = TestJwtUserResolver()
        val claims = mapOf("role" to "user")

        val user = resolver.resolveUser("valid-user", claims)

        user shouldBe TestUser("valid-user", "Unknown")
    }
})
