package com.github.snowykte0426.peanut.butter.timezone

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.assertions.throwables.shouldThrow
import java.util.*

class TimeZoneAutoConfigurationTest : FunSpec({

    lateinit var originalTimeZone: TimeZone

    beforeEach {
        originalTimeZone = TimeZone.getDefault()
    }

    afterEach {
        TimeZone.setDefault(originalTimeZone)
        System.setProperty("user.timezone", originalTimeZone.id)
    }

    test("should initialize timezone correctly with valid configuration") {
        val properties = TimeZoneProperties(zone = "UTC", enableLogging = false)
        val configuration = TimeZoneAutoConfiguration(properties)

        configuration.init()

        TimeZone.getDefault().id shouldBe "UTC"
        System.getProperty("user.timezone") shouldBe "UTC"
    }

    test("should throw exception for unsupported timezone") {
        val properties = TimeZoneProperties(zone = "INVALID", enableLogging = false)
        val configuration = TimeZoneAutoConfiguration(properties)

        shouldThrow<IllegalArgumentException> {
            configuration.init()
        }
    }

    test("should create TimeZoneInitializer bean") {
        val properties = TimeZoneProperties(zone = "KST", enableLogging = false)
        val configuration = TimeZoneAutoConfiguration(properties)

        val initializer = configuration.timeZoneInitializer()

        initializer shouldNotBe null
        initializer.getSupportedTimeZones().size shouldBe 14
    }

    test("should initialize different timezones correctly") {
        listOf("KST", "JST", "UTC", "GMT").forEach { zone ->
            val properties = TimeZoneProperties(zone = zone, enableLogging = false)
            val configuration = TimeZoneAutoConfiguration(properties)

            configuration.init()

            val expectedTimeZone = SupportedTimeZone.fromString(zone)!!
            TimeZone.getDefault().id shouldBe expectedTimeZone.zoneId
        }
    }

    test("should handle logging enabled configuration") {
        val properties = TimeZoneProperties(zone = "UTC", enableLogging = true)
        val configuration = TimeZoneAutoConfiguration(properties)

        configuration.init()

        TimeZone.getDefault().id shouldBe "UTC"
    }
})
