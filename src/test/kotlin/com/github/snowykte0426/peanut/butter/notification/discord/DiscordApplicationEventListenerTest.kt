package com.github.snowykte0426.peanut.butter.notification.discord

import io.kotest.core.spec.style.DescribeSpec
import org.mockito.kotlin.*
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.ContextClosedEvent

class DiscordApplicationEventListenerTest : DescribeSpec({

    describe("DiscordApplicationEventListener") {
        
        context("when handling application events") {
            val discordWebhookService = mock<DiscordWebhookService>()
            val listener = DiscordApplicationEventListener(discordWebhookService)

            it("should send startup notification on ApplicationReadyEvent") {
                val event = mock<ApplicationReadyEvent>()
                
                listener.handleApplicationReady(event)
                
                verify(discordWebhookService).sendStartupNotification()
            }

            it("should send shutdown notification on ContextClosedEvent") {
                val event = mock<ContextClosedEvent>()
                
                listener.handleContextClosed(event)
                
                verify(discordWebhookService).sendShutdownNotification()
            }
        }
    }
})