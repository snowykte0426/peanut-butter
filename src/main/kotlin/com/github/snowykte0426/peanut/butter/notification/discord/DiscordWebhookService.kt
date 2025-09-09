package com.github.snowykte0426.peanut.butter.notification.discord

import com.github.snowykte0426.peanut.butter.logging.logError
import com.github.snowykte0426.peanut.butter.logging.logInfo
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.net.InetAddress
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Service
@ConditionalOnProperty(
    name = ["peanut-butter.notification.discord.webhook.enabled"],
    havingValue = "true"
)
class DiscordWebhookService(
    private val discordProperties: DiscordProperties,
    private val restTemplate: RestTemplate = RestTemplate(),
    @Value("\${spring.application.name:Unknown Application}") private val applicationName: String,
    @Value("\${spring.profiles.active:default}") private val activeProfiles: String
) {

    private val hostname: String by lazy {
        try {
            InetAddress.getLocalHost().hostName
        } catch (e: Exception) {
            "Unknown Host"
        }
    }

    private val messages: DiscordMessages by lazy {
        val locale = when (discordProperties.locale.lowercase()) {
            "ko", "korean" -> DiscordLocale.KOREAN
            else -> DiscordLocale.ENGLISH
        }
        DiscordMessageLocalizer.getMessages(locale)
    }

    fun sendStartupNotification() {
        if (!discordProperties.webhook.enabled) return

        val embed = createEmbed(
            title = messages.serverStartTitle,
            description = messages.serverStartDescription,
            color = 0x00ff00
        )

        sendWebhookMessage(listOf(embed))
    }

    fun sendShutdownNotification() {
        if (!discordProperties.webhook.enabled) return

        val embed = createEmbed(
            title = messages.serverStopTitle,
            description = messages.serverStopDescription,
            color = 0xff9900
        )

        sendWebhookMessage(listOf(embed))
    }

    fun sendExceptionNotification(exception: Throwable, context: String = "") {
        if (!discordProperties.webhook.enabled) return

        val stackTrace = exception.stackTraceToString()
        val truncatedStackTrace = if (stackTrace.length > 1500) {
            stackTrace.take(1500) + "...\n${messages.stackTraceTruncated}"
        } else {
            stackTrace
        }

        val embed = createEmbed(
            title = messages.exceptionTitle,
            description = buildString {
                if (context.isNotEmpty()) {
                    append("**${messages.contextLabel}:** $context\n\n")
                }
                append("**${messages.exceptionTypeLabel}:** ${exception.javaClass.simpleName}\n")
                append("**${messages.messageLabel}:** ${exception.message ?: "N/A"}\n\n")
                append("**${messages.stackTraceLabel}:**\n")
                append("```\n$truncatedStackTrace\n```")
            },
            color = 0xff0000
        )

        sendWebhookMessage(listOf(embed))
    }

    private fun createEmbed(
        title: String,
        description: String,
        color: Int = discordProperties.embed.color
    ): Map<String, Any> {
        val embed = mutableMapOf<String, Any>(
            "title" to title,
            "description" to description,
            "color" to color
        )

        val fields = mutableListOf<Map<String, Any>>()

        fields.add(mapOf(
            "name" to messages.applicationLabel,
            "value" to applicationName,
            "inline" to true
        ))

        fields.add(mapOf(
            "name" to messages.profileLabel,
            "value" to activeProfiles,
            "inline" to true
        ))

        if (discordProperties.embed.includeHostname) {
            fields.add(mapOf(
                "name" to messages.hostnameLabel,
                "value" to hostname,
                "inline" to true
            ))
        }

        embed["fields"] = fields

        if (discordProperties.embed.includeTimestamp) {
            embed["timestamp"] = Instant.now().toString()
        }

        if (discordProperties.embed.footer.isNotEmpty()) {
            embed["footer"] = mapOf("text" to discordProperties.embed.footer)
        }

        return embed
    }

    private fun sendWebhookMessage(embeds: List<Map<String, Any>>) {
        try {
            val payload = mapOf("embeds" to embeds)

            val headers = HttpHeaders().apply {
                contentType = MediaType.APPLICATION_JSON
            }

            val entity = HttpEntity(payload, headers)

            restTemplate.postForEntity(
                discordProperties.webhook.url,
                entity,
                String::class.java
            )

            logInfo("Discord webhook message sent successfully")
        } catch (e: Exception) {
            logError("Failed to send Discord webhook message", e)
        }
    }
}