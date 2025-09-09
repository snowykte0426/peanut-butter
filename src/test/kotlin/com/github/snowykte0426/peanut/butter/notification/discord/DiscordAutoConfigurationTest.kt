package com.github.snowykte0426.peanut.butter.notification.discord

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class DiscordAutoConfigurationTest : DescribeSpec({

    describe("DiscordAutoConfiguration") {
        
        context("when creating DiscordAutoConfiguration") {
            val configuration = DiscordAutoConfiguration()

            it("should create discordRestTemplate bean") {
                val restTemplate = configuration.discordRestTemplate()
                restTemplate.shouldBeInstanceOf<org.springframework.web.client.RestTemplate>()
            }
        }
    }
})