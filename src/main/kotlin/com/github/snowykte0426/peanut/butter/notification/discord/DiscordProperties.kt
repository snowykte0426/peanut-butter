package com.github.snowykte0426.peanut.butter.notification.discord

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "peanut-butter.notification.discord")
data class DiscordProperties(
    val webhook: WebhookProperties = WebhookProperties(),
    val embed: EmbedProperties = EmbedProperties(),
    val locale: String = "en"
) {
    data class WebhookProperties(
        val url: String = "",
        val enabled: Boolean = false,
        val timeout: Long = 5000L
    )

    data class EmbedProperties(
        val color: Int = 0x00ff00,
        val footer: String = "Powered by peanut-butter",
        val includeTimestamp: Boolean = true,
        val includeHostname: Boolean = true,
        val includeVersion: Boolean = true
    )
}