package com.github.snowykte0426.peanut.butter.security.cors

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain

class CorsPropertiesTest : FunSpec({

    test("should have correct default values") {
        val properties = CorsProperties()

        properties.allowedOrigins shouldBe listOf("*")
        properties.allowedHeaders shouldBe listOf("*")
        properties.allowCredentials shouldBe true
        properties.maxAge shouldBe 3600L
        properties.exposedHeaders shouldBe emptyList<String>()
        properties.enabled shouldBe true
    }

    test("should have all standard HTTP methods allowed by default") {
        val properties = CorsProperties()

        properties.allowedMethods["GET"] shouldBe true
        properties.allowedMethods["POST"] shouldBe true
        properties.allowedMethods["PUT"] shouldBe true
        properties.allowedMethods["DELETE"] shouldBe true
        properties.allowedMethods["PATCH"] shouldBe true
        properties.allowedMethods["HEAD"] shouldBe true
        properties.allowedMethods["OPTIONS"] shouldBe true
        properties.allowedMethods["TRACE"] shouldBe false
    }

    test("should support custom configuration") {
        val customOrigins = listOf("https://example.com", "http://localhost:3000")
        val customHeaders = listOf("Content-Type", "Authorization")
        val customMethods = mapOf("GET" to true, "POST" to false)
        val customExposedHeaders = listOf("X-Total-Count")

        val properties = CorsProperties(
            allowedOrigins = customOrigins,
            allowedHeaders = customHeaders,
            allowedMethods = customMethods,
            allowCredentials = false,
            maxAge = 7200L,
            exposedHeaders = customExposedHeaders,
            enabled = false
        )

        properties.allowedOrigins shouldBe customOrigins
        properties.allowedHeaders shouldBe customHeaders
        properties.allowedMethods shouldBe customMethods
        properties.allowCredentials shouldBe false
        properties.maxAge shouldBe 7200L
        properties.exposedHeaders shouldBe customExposedHeaders
        properties.enabled shouldBe false
    }

    test("should handle mixed method configuration") {
        val methods = mapOf(
            "GET" to true,
            "POST" to true,
            "PUT" to false,
            "DELETE" to false
        )

        val properties = CorsProperties(allowedMethods = methods)

        properties.allowedMethods["GET"] shouldBe true
        properties.allowedMethods["POST"] shouldBe true
        properties.allowedMethods["PUT"] shouldBe false
        properties.allowedMethods["DELETE"] shouldBe false
    }

    test("should handle empty exposed headers by default") {
        val properties = CorsProperties()

        properties.exposedHeaders shouldBe emptyList<String>()
    }

    test("should handle multiple exposed headers") {
        val exposedHeaders = listOf("X-Total-Count", "X-Page-Count", "Link")
        val properties = CorsProperties(exposedHeaders = exposedHeaders)

        properties.exposedHeaders shouldBe exposedHeaders
        properties.exposedHeaders shouldContain "X-Total-Count"
        properties.exposedHeaders shouldContain "X-Page-Count"
        properties.exposedHeaders shouldContain "Link"
    }
})