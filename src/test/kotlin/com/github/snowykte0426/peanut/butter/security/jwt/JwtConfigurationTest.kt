package com.github.snowykte0426.peanut.butter.security.jwt

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

class JwtConfigurationTest : FunSpec({

    test("JwtConfiguration should be annotated with Configuration") {
        val annotations = JwtConfiguration::class.annotations
        val configurationAnnotation = annotations.find { it is Configuration }

        configurationAnnotation shouldNotBe null
    }

    test("JwtConfiguration should enable JwtProperties") {
        val annotations = JwtConfiguration::class.annotations
        val enableConfigPropsAnnotation = annotations.find { it is EnableConfigurationProperties }

        enableConfigPropsAnnotation shouldNotBe null
        val annotation = enableConfigPropsAnnotation as EnableConfigurationProperties
        annotation.value shouldBe arrayOf(JwtProperties::class)
    }

    test("JwtConfiguration should be instantiable") {
        val configuration = JwtConfiguration()

        configuration shouldNotBe null
    }
})
