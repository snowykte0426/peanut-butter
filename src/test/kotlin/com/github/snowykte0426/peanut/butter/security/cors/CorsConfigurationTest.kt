package com.github.snowykte0426.peanut.butter.security.cors

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import org.springframework.web.cors.CorsConfiguration as SpringCorsConfiguration
import org.springframework.mock.web.MockHttpServletRequest

class CorsConfigurationTest : FunSpec({

    test("should create CorsConfigurationSource with default properties") {
        val properties = CorsProperties()
        val configuration = CorsConfiguration(properties)

        val corsConfigurationSource = configuration.corsConfigurationSource()

        corsConfigurationSource shouldNotBe null
        val mockRequest = MockHttpServletRequest()
        val corsConfig = corsConfigurationSource.getCorsConfiguration(mockRequest)
        corsConfig shouldNotBe null
        corsConfig!!.allowedOrigins shouldBe listOf("*")
        corsConfig.allowedHeaders shouldBe listOf("*")
        corsConfig.allowCredentials shouldBe true
        corsConfig.maxAge shouldBe 3600L
    }

    test("should filter allowed methods based on properties") {
        val properties = CorsProperties(
            allowedMethods = mapOf(
                "GET" to true,
                "POST" to true,
                "PUT" to false,
                "DELETE" to false,
                "PATCH" to true
            )
        )
        val configuration = CorsConfiguration(properties)

        val corsConfigurationSource = configuration.corsConfigurationSource()
        val mockRequest = MockHttpServletRequest()
        val corsConfig = corsConfigurationSource.getCorsConfiguration(mockRequest)!!

        corsConfig.allowedMethods shouldContain "GET"
        corsConfig.allowedMethods shouldContain "POST"
        corsConfig.allowedMethods shouldContain "PATCH"
        corsConfig.allowedMethods shouldNotContain "PUT"
        corsConfig.allowedMethods shouldNotContain "DELETE"
    }

    test("should handle custom origins and headers") {
        val customOrigins = listOf("https://example.com", "http://localhost:3000")
        val customHeaders = listOf("Content-Type", "Authorization", "X-Requested-With")
        val properties = CorsProperties(
            allowedOrigins = customOrigins,
            allowedHeaders = customHeaders
        )
        val configuration = CorsConfiguration(properties)

        val corsConfigurationSource = configuration.corsConfigurationSource()
        val mockRequest = MockHttpServletRequest()
        val corsConfig = corsConfigurationSource.getCorsConfiguration(mockRequest)!!

        corsConfig.allowedOrigins shouldBe customOrigins
        corsConfig.allowedHeaders shouldBe customHeaders
    }

    test("should handle exposed headers when provided") {
        val exposedHeaders = listOf("X-Total-Count", "X-Page-Count")
        val properties = CorsProperties(exposedHeaders = exposedHeaders)
        val configuration = CorsConfiguration(properties)

        val corsConfigurationSource = configuration.corsConfigurationSource()
        val mockRequest = MockHttpServletRequest()
        val corsConfig = corsConfigurationSource.getCorsConfiguration(mockRequest)!!

        corsConfig.exposedHeaders shouldBe exposedHeaders
    }

    test("should not set exposed headers when empty") {
        val properties = CorsProperties(exposedHeaders = emptyList())
        val configuration = CorsConfiguration(properties)

        val corsConfigurationSource = configuration.corsConfigurationSource()
        val mockRequest = MockHttpServletRequest()
        val corsConfig = corsConfigurationSource.getCorsConfiguration(mockRequest)!!

        corsConfig.exposedHeaders shouldBe null
    }

    test("should create SpringCorsConfiguration bean with correct settings") {
        val properties = CorsProperties(
            allowedOrigins = listOf("https://api.example.com"),
            allowCredentials = false,
            maxAge = 7200L
        )
        val configuration = CorsConfiguration(properties)

        val springCorsConfig = configuration.springCorsConfiguration()

        springCorsConfig.allowedOrigins shouldBe listOf("https://api.example.com")
        springCorsConfig.allowCredentials shouldBe false
        springCorsConfig.maxAge shouldBe 7200L
    }

    test("should handle all methods disabled scenario") {
        val properties = CorsProperties(
            allowedMethods = mapOf(
                "GET" to false,
                "POST" to false,
                "PUT" to false,
                "DELETE" to false
            )
        )
        val configuration = CorsConfiguration(properties)

        val corsConfigurationSource = configuration.corsConfigurationSource()
        val mockRequest = MockHttpServletRequest()
        val corsConfig = corsConfigurationSource.getCorsConfiguration(mockRequest)!!

        corsConfig.allowedMethods shouldBe emptyList<String>()
    }

    test("should handle single method enabled scenario") {
        val properties = CorsProperties(
            allowedMethods = mapOf(
                "GET" to true,
                "POST" to false,
                "PUT" to false,
                "DELETE" to false
            )
        )
        val configuration = CorsConfiguration(properties)

        val corsConfigurationSource = configuration.corsConfigurationSource()
        val mockRequest = MockHttpServletRequest()
        val corsConfig = corsConfigurationSource.getCorsConfiguration(mockRequest)!!

        corsConfig.allowedMethods shouldBe listOf("GET")
    }
})