package com.github.snowykte0426.peanut.butter.notification.discord

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf

class DiscordAutoConfigurationTest : DescribeSpec({

    describe("DiscordAutoConfiguration") {
        
        context("when creating DiscordAutoConfiguration") {
            val configuration = DiscordAutoConfiguration()

            it("should create configuration instance") {
                configuration shouldNotBe null
                configuration.shouldBeInstanceOf<DiscordAutoConfiguration>()
            }
        }
    }
})