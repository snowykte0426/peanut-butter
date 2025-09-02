package com.github.snowykte0426.peanut.butter.security.jwt

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class JwtFilterPropertiesTest : FunSpec({

    test("should create properties with default values") {
        val properties = JwtFilterProperties()
        
        properties.enabled shouldBe false
        properties.excludedPaths shouldBe emptyList()
        properties.autoDetectPermitAllPaths shouldBe true
    }

    test("should create properties with custom values") {
        val customPaths = listOf("/api/public/**", "/health/*", "/actuator/**")
        val properties = JwtFilterProperties(
            enabled = true,
            excludedPaths = customPaths,
            autoDetectPermitAllPaths = false
        )
        
        properties.enabled shouldBe true
        properties.excludedPaths shouldBe customPaths
        properties.autoDetectPermitAllPaths shouldBe false
    }

    test("should handle empty excluded paths") {
        val properties = JwtFilterProperties(
            enabled = true,
            excludedPaths = emptyList(),
            autoDetectPermitAllPaths = true
        )
        
        properties.enabled shouldBe true
        properties.excludedPaths shouldBe emptyList()
        properties.autoDetectPermitAllPaths shouldBe true
    }

    test("should use default values when not specified") {
        val properties = JwtFilterProperties(enabled = true)
        
        properties.enabled shouldBe true
        properties.excludedPaths shouldBe emptyList()
        properties.autoDetectPermitAllPaths shouldBe true
    }
})