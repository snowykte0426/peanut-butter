package com.github.snowykte0426.peanut.buttor.timezone

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.assertions.throwables.shouldThrow
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

class TimeZoneExtensionsTest : FunSpec({

    lateinit var originalTimeZone: TimeZone

    beforeEach {
        originalTimeZone = TimeZone.getDefault()
    }

    afterEach {
        TimeZone.setDefault(originalTimeZone)
        System.setProperty("user.timezone", originalTimeZone.id)
    }

    test("should get current time in specified timezone") {
        val testObject = TestClass()
        val kstTime = testObject.getCurrentTimeIn(SupportedTimeZone.KST)
        val utcTime = testObject.getCurrentTimeIn(SupportedTimeZone.UTC)

        kstTime.zone shouldBe ZoneId.of("Asia/Seoul")
        utcTime.zone shouldBe ZoneId.of("UTC")
    }

    test("should get current time in timezone by string") {
        val testObject = TestClass()
        val jstTime = testObject.getCurrentTimeIn("JST")

        jstTime.zone shouldBe ZoneId.of("Asia/Tokyo")
    }

    test("should throw exception for invalid timezone string in getCurrentTimeIn") {
        val testObject = TestClass()

        shouldThrow<IllegalArgumentException> {
            testObject.getCurrentTimeIn("INVALID")
        }
    }

    test("should convert LocalDateTime to ZonedDateTime in timezone") {
        val localDateTime = LocalDateTime.of(2025, 8, 30, 12, 0, 0)
        val zonedDateTime = localDateTime.inTimeZone(SupportedTimeZone.KST)

        zonedDateTime.zone shouldBe ZoneId.of("Asia/Seoul")
        zonedDateTime.toLocalDateTime() shouldBe localDateTime
    }

    test("should convert LocalDateTime to ZonedDateTime by string") {
        val localDateTime = LocalDateTime.of(2025, 8, 30, 12, 0, 0)
        val zonedDateTime = localDateTime.inTimeZone("UTC")

        zonedDateTime.zone shouldBe ZoneId.of("UTC")
    }

    test("should convert ZonedDateTime between timezones") {
        val utcTime = ZonedDateTime.of(2025, 8, 30, 12, 0, 0, 0, ZoneId.of("UTC"))
        val kstTime = utcTime.convertToTimeZone(SupportedTimeZone.KST)

        kstTime.zone shouldBe ZoneId.of("Asia/Seoul")
        kstTime.hour shouldBe 21 // UTC 12:00 = KST 21:00
    }

    test("should convert ZonedDateTime to timezone by string") {
        val utcTime = ZonedDateTime.of(2025, 8, 30, 12, 0, 0, 0, ZoneId.of("UTC"))
        val jstTime = utcTime.convertToTimeZone("JST")

        jstTime.zone shouldBe ZoneId.of("Asia/Tokyo")
        jstTime.hour shouldBe 21 // UTC 12:00 = JST 21:00
    }

    test("should check if current timezone matches specified timezone") {
        val testObject = TestClass()

        // Set to KST first
        TimeZone.setDefault(SupportedTimeZone.KST.toTimeZone())
        System.setProperty("user.timezone", "Asia/Seoul")

        testObject.isCurrentTimeZone(SupportedTimeZone.KST) shouldBe true
        testObject.isCurrentTimeZone(SupportedTimeZone.UTC) shouldBe false
    }

    test("should check current timezone by string") {
        val testObject = TestClass()

        TimeZone.setDefault(SupportedTimeZone.UTC.toTimeZone())
        System.setProperty("user.timezone", "UTC")

        testObject.isCurrentTimeZone("UTC") shouldBe true
        testObject.isCurrentTimeZone("KST") shouldBe false
        testObject.isCurrentTimeZone("INVALID") shouldBe false
    }

    test("should get current timezone display name") {
        val testObject = TestClass()

        TimeZone.setDefault(SupportedTimeZone.KST.toTimeZone())
        System.setProperty("user.timezone", "Asia/Seoul")

        val displayName = testObject.getCurrentTimeZoneDisplayName()
        displayName shouldBe "Korea Standard Time"
    }

    test("should get timezone id for unsupported current timezone") {
        val testObject = TestClass()

        // Set to an unsupported timezone
        val customTimeZone = TimeZone.getTimeZone("America/New_York")
        TimeZone.setDefault(customTimeZone)
        System.setProperty("user.timezone", "America/New_York")

        val displayName = testObject.getCurrentTimeZoneDisplayName()
        displayName shouldBe "Eastern Time" // This should match ET in our enum
    }

    test("should execute block with temporary timezone change") {
        val testObject = TestClass()

        // Start with UTC
        TimeZone.setDefault(SupportedTimeZone.UTC.toTimeZone())

        val result = testObject.withTimeZone(SupportedTimeZone.KST) {
            TimeZone.getDefault().id shouldBe "Asia/Seoul"
            "executed in KST"
        }

        result shouldBe "executed in KST"
        TimeZone.getDefault().id shouldBe "UTC" // Should be restored
    }

    test("should execute block with temporary timezone change by string") {
        val testObject = TestClass()

        TimeZone.setDefault(SupportedTimeZone.UTC.toTimeZone())

        val result = testObject.withTimeZone("JST") {
            TimeZone.getDefault().id shouldBe "Asia/Tokyo"
            42
        }

        result shouldBe 42
        TimeZone.getDefault().id shouldBe "UTC" // Should be restored
    }

    test("should restore timezone even when exception occurs in withTimeZone") {
        val testObject = TestClass()

        TimeZone.setDefault(SupportedTimeZone.UTC.toTimeZone())

        shouldThrow<RuntimeException> {
            testObject.withTimeZone(SupportedTimeZone.KST) {
                throw RuntimeException("Test exception")
            }
        }

        TimeZone.getDefault().id shouldBe "UTC" // Should be restored even after exception
    }

}) {
    private class TestClass
}
