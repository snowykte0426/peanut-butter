package com.github.snowykte0426.peanut.butter.notification.discord

import com.github.snowykte0426.peanut.butter.logging.logInfo
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.ContextClosedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(
    name = ["peanut-butter.notification.discord.webhook.enabled"],
    havingValue = "true"
)
class DiscordApplicationEventListener(
    private val discordWebhookService: DiscordWebhookService
) {

    @EventListener
    fun handleApplicationReady(event: ApplicationReadyEvent) {
        logInfo("Application ready - sending Discord notification")
        discordWebhookService.sendStartupNotification()
    }

    @EventListener
    fun handleContextClosed(event: ContextClosedEvent) {
        logInfo("Application context closed - sending Discord notification")
        discordWebhookService.sendShutdownNotification()
    }
}