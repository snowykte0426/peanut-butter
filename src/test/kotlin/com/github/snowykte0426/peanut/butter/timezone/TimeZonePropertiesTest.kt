package com.github.snowykte0426.peanut.butter.timezone

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.context.properties.ConfigurationProperties

class TimeZonePropertiesTest : FunSpec({

    test("TimeZoneProperties should have default values") {
        val properties = TimeZoneProperties()

        properties.zone shouldBe "UTC"
        properties.enableLogging shouldBe true
        properties.enabled shouldBe true
    }

    test("TimeZoneProperties should allow custom zone") {
        val properties = TimeZoneProperties(zone = "KST")

        properties.zone shouldBe "KST"
        properties.enableLogging shouldBe true
        properties.enabled shouldBe true
    }

    test("TimeZoneProperties should allow disabling logging") {
        val properties = TimeZoneProperties(enableLogging = false)

        properties.zone shouldBe "UTC"
        properties.enableLogging shouldBe false
        properties.enabled shouldBe true
    }

    test("TimeZoneProperties should allow disabling timezone configuration") {
        val properties = TimeZoneProperties(enabled = false)

        properties.zone shouldBe "UTC"
        properties.enableLogging shouldBe true
        properties.enabled shouldBe false
    }

    test("TimeZoneProperties should allow setting all properties") {
        val properties = TimeZoneProperties(
            zone = "JST",
            enableLogging = false,
            enabled = false
        )

        properties.zone shouldBe "JST"
        properties.enableLogging shouldBe false
        properties.enabled shouldBe false
    }

    test("TimeZoneProperties should be annotated with ConfigurationProperties") {
        val annotations = TimeZoneProperties::class.annotations
        val configPropsAnnotation = annotations.find { it is ConfigurationProperties }

        configPropsAnnotation shouldNotBe null
        val annotation = configPropsAnnotation as ConfigurationProperties
        annotation.prefix shouldBe "peanut-butter.timezone"
    }

    test("TimeZoneProperties should support various timezone values") {
        val supportedZones = listOf("UTC", "KST", "JST", "GMT", "WET", "BST", "CET", "WEST", "CEST", "EET", "EEST", "MST", "PT", "ET")

        supportedZones.forEach { zone ->
            val properties = TimeZoneProperties(zone = zone)
            properties.zone shouldBe zone
        }
    }

    test("TimeZoneProperties should be data class") {
        val properties1 = TimeZoneProperties("KST", false, true)
        val properties2 = TimeZoneProperties("KST", false, true)
        val properties3 = TimeZoneProperties("UTC", false, true)

        properties1 shouldBe properties2
        properties1 shouldNotBe properties3
    }
})
