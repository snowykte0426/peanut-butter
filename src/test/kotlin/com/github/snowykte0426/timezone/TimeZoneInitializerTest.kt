package com.github.snowykte0426.timezone

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.collections.shouldContain
import io.kotest.assertions.throwables.shouldThrow
import java.util.*

class TimeZoneInitializerTest : FunSpec({

    lateinit var initializer: TimeZoneInitializer
    lateinit var properties: TimeZoneProperties
    lateinit var originalTimeZone: TimeZone

    beforeEach {
        originalTimeZone = TimeZone.getDefault()
        properties = TimeZoneProperties(zone = "KST", enableLogging = false)
        initializer = TimeZoneInitializer(properties)
    }

    test("should change timezone using SupportedTimeZone enum") {
        initializer.changeTimeZone(SupportedTimeZone.UTC)

        TimeZone.getDefault().id shouldBe "UTC"
        System.getProperty("user.timezone") shouldBe "UTC"
    }

    test("should change timezone using string") {
        initializer.changeTimeZone("JST")

        TimeZone.getDefault().id shouldBe "Asia/Tokyo"
        System.getProperty("user.timezone") shouldBe "Asia/Tokyo"
    }

    test("should throw exception for unsupported timezone") {
        shouldThrow<IllegalArgumentException> {
            initializer.changeTimeZone("INVALID_TIMEZONE")
        }
    }

    test("should get current timezone") {
        initializer.changeTimeZone(SupportedTimeZone.KST)
        val currentTimeZone = initializer.getCurrentTimeZone()

        currentTimeZone.id shouldBe "Asia/Seoul"
    }

    test("should get supported timezones list") {
        val supportedTimeZones = initializer.getSupportedTimeZones()

        supportedTimeZones.size shouldBe 14
        supportedTimeZones shouldContain SupportedTimeZone.KST
        supportedTimeZones shouldContain SupportedTimeZone.UTC
    }
})
