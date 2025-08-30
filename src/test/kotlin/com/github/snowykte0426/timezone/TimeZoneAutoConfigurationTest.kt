package com.github.snowykte0426.timezone

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Configuration
import java.util.*

@SpringBootTest
@TestPropertySource(properties = [
    "peanut-butter.timezone.zone=UTC",
    "peanut-butter.timezone.enabled=true",
    "peanut-butter.timezone.enable-logging=false"
])
class TimeZoneAutoConfigurationTest : FunSpec({

    @Autowired
    lateinit var timeZoneInitializer: TimeZoneInitializer

    test("should create TimeZoneInitializer bean successfully") {
        timeZoneInitializer shouldNotBe null
    }

    test("should apply configured timezone correctly") {
        val currentTimeZone = timeZoneInitializer.getCurrentTimeZone()
        currentTimeZone.id shouldBe "UTC"
    }

    test("should change timezone programmatically") {
        timeZoneInitializer.changeTimeZone(SupportedTimeZone.KST)
        TimeZone.getDefault().id shouldBe "Asia/Seoul"

        timeZoneInitializer.changeTimeZone("JST")
        TimeZone.getDefault().id shouldBe "Asia/Tokyo"
    }

}) {
    @SpringBootApplication
    @EnableAutomaticTimeZone
    @Configuration
    class TestApplication
}
