package com.github.snowykte0426.peanut.butter.notification.discord

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class DiscordPropertiesTest : DescribeSpec({

    describe("DiscordProperties") {
        
        context("when using default values") {
            val properties = DiscordProperties()

            it("should have default webhook settings") {
                properties.webhook.url shouldBe ""
                properties.webhook.enabled shouldBe false
                properties.webhook.timeout shouldBe 5000L
            }

            it("should have default embed settings") {
                properties.embed.color shouldBe 0x00ff00
                properties.embed.footer shouldBe "Powered by peanut-butter"
                properties.embed.includeTimestamp shouldBe true
                properties.embed.includeHostname shouldBe true
                properties.embed.includeVersion shouldBe true
            }

            it("should have default locale") {
                properties.locale shouldBe "en"
            }
        }

        context("when customizing values") {
            val properties = DiscordProperties(
                webhook = DiscordProperties.WebhookProperties(
                    url = "https://discord.com/api/webhooks/test",
                    enabled = true,
                    timeout = 10000L
                ),
                embed = DiscordProperties.EmbedProperties(
                    color = 0xff0000,
                    footer = "Custom Footer",
                    includeTimestamp = false,
                    includeHostname = false,
                    includeVersion = false
                ),
                locale = "ko"
            )

            it("should use custom webhook settings") {
                properties.webhook.url shouldBe "https://discord.com/api/webhooks/test"
                properties.webhook.enabled shouldBe true
                properties.webhook.timeout shouldBe 10000L
            }

            it("should use custom embed settings") {
                properties.embed.color shouldBe 0xff0000
                properties.embed.footer shouldBe "Custom Footer"
                properties.embed.includeTimestamp shouldBe false
                properties.embed.includeHostname shouldBe false
                properties.embed.includeVersion shouldBe false
            }

            it("should use custom locale") {
                properties.locale shouldBe "ko"
            }
        }
    }
})