package com.github.snowykte0426.peanut.butter.timezone

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.collections.shouldContain

class SupportedTimeZoneTest : FunSpec({

    test("should have correct number of supported timezones") {
        val supportedZones = SupportedTimeZone.values()

        supportedZones.size shouldBe 14
        supportedZones shouldContain SupportedTimeZone.KST
        supportedZones shouldContain SupportedTimeZone.UTC
        supportedZones shouldContain SupportedTimeZone.JST
    }

    test("should find SupportedTimeZone from string successfully") {
        SupportedTimeZone.fromString("KST") shouldBe SupportedTimeZone.KST
        SupportedTimeZone.fromString("kst") shouldBe SupportedTimeZone.KST
        SupportedTimeZone.fromString("Asia/Seoul") shouldBe SupportedTimeZone.KST
        SupportedTimeZone.fromString("UTC") shouldBe SupportedTimeZone.UTC
        SupportedTimeZone.fromString("JST") shouldBe SupportedTimeZone.JST
    }

    test("should return null for invalid timezone string") {
        SupportedTimeZone.fromString("INVALID").shouldBeNull()
        SupportedTimeZone.fromString("").shouldBeNull()
    }

    test("should convert to TimeZone object correctly") {
        val kstTimeZone = SupportedTimeZone.KST.toTimeZone()
        val utcTimeZone = SupportedTimeZone.UTC.toTimeZone()

        kstTimeZone.id shouldBe "Asia/Seoul"
        utcTimeZone.id shouldBe "UTC"
    }

    test("should have correct timezone information") {
        SupportedTimeZone.KST.zoneId shouldBe "Asia/Seoul"
        SupportedTimeZone.KST.displayName shouldBe "Korea Standard Time"

        SupportedTimeZone.UTC.zoneId shouldBe "UTC"
        SupportedTimeZone.UTC.displayName shouldBe "Coordinated Universal Time"
    }
})
