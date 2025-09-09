package com.github.snowykte0426.peanut.butter.notification.discord

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.mockito.kotlin.*
import org.springframework.http.HttpEntity
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate

class DiscordWebhookServiceTest : DescribeSpec({

    describe("DiscordWebhookService") {
        
        context("when webhook is disabled") {
            val properties = DiscordProperties(
                webhook = DiscordProperties.WebhookProperties(enabled = false)
            )
            val restTemplate = mock<RestTemplate>()
            val service = DiscordWebhookService(properties, restTemplate, "test-app", "test")

            it("should not send startup notification") {
                service.sendStartupNotification()
                verifyNoInteractions(restTemplate)
            }

            it("should not send shutdown notification") {
                service.sendShutdownNotification()
                verifyNoInteractions(restTemplate)
            }

            it("should not send exception notification") {
                service.sendExceptionNotification(RuntimeException("test"))
                verifyNoInteractions(restTemplate)
            }
        }

        context("when webhook is enabled") {
            val properties = DiscordProperties(
                webhook = DiscordProperties.WebhookProperties(
                    enabled = true,
                    url = "https://discord.com/api/webhooks/test"
                ),
                locale = "en"
            )
            val restTemplate = mock<RestTemplate>()
            val service = DiscordWebhookService(properties, restTemplate, "test-app", "test")

            beforeEach {
                reset(restTemplate)
                whenever(restTemplate.postForEntity(any<String>(), any<HttpEntity<*>>(), eq(String::class.java)))
                    .thenReturn(ResponseEntity.ok("success"))
            }

            it("should send startup notification") {
                service.sendStartupNotification()
                
                verify(restTemplate).postForEntity(
                    eq("https://discord.com/api/webhooks/test"),
                    any<HttpEntity<*>>(),
                    eq(String::class.java)
                )
            }

            it("should send shutdown notification") {
                service.sendShutdownNotification()
                
                verify(restTemplate).postForEntity(
                    eq("https://discord.com/api/webhooks/test"),
                    any<HttpEntity<*>>(),
                    eq(String::class.java)
                )
            }

            it("should send exception notification") {
                val exception = RuntimeException("Test exception")
                service.sendExceptionNotification(exception, "Test context")
                
                verify(restTemplate).postForEntity(
                    eq("https://discord.com/api/webhooks/test"),
                    any<HttpEntity<*>>(),
                    eq(String::class.java)
                )
            }
        }

        context("when using Korean locale") {
            val properties = DiscordProperties(
                webhook = DiscordProperties.WebhookProperties(
                    enabled = true,
                    url = "https://discord.com/api/webhooks/test"
                ),
                locale = "ko"
            )
            val restTemplate = mock<RestTemplate>()
            val service = DiscordWebhookService(properties, restTemplate, "test-app", "test")

            beforeEach {
                reset(restTemplate)
                whenever(restTemplate.postForEntity(any<String>(), any<HttpEntity<*>>(), eq(String::class.java)))
                    .thenReturn(ResponseEntity.ok("success"))
            }

            it("should use Korean messages") {
                service.sendStartupNotification()
                
                verify(restTemplate).postForEntity(
                    eq("https://discord.com/api/webhooks/test"),
                    argThat<HttpEntity<*>> { entity ->
                        val body = entity.body as Map<*, *>
                        val embeds = body["embeds"] as List<*>
                        val embed = embeds[0] as Map<*, *>
                        embed["title"] == "🚀 서버 시작"
                    },
                    eq(String::class.java)
                )
            }
        }
    }
})