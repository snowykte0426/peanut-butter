package com.github.snowykte0426.peanut.butter.notification.discord

import com.github.snowykte0426.peanut.butter.logging.logError
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@Component
@ConditionalOnProperty(
    name = ["peanut-butter.notification.discord.webhook.enabled"],
    havingValue = "true"
)
class DiscordExceptionHandler(
    private val discordWebhookService: DiscordWebhookService
) {

    fun handleException(
        exception: Throwable,
        context: String = "",
        includeRequestInfo: Boolean = true
    ) {
        try {
            var contextInfo = context
            
            if (includeRequestInfo) {
                val requestInfo = getRequestInfo()
                if (requestInfo.isNotEmpty()) {
                    contextInfo = if (context.isEmpty()) requestInfo else "$context\n$requestInfo"
                }
            }

            discordWebhookService.sendExceptionNotification(exception, contextInfo)
        } catch (e: Exception) {
            logError("Discord 예외 알림 전송 중 오류 발생", e)
        }
    }

    private fun getRequestInfo(): String {
        return try {
            val requestAttributes = RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes
            val request = requestAttributes?.request

            if (request != null) {
                buildString {
                    append("**Request Info:**\n")
                    append("- URI: ${request.requestURI}\n")
                    append("- Method: ${request.method}\n")
                    append("- Remote Address: ${request.remoteAddr}\n")
                    if (request.queryString != null) {
                        append("- Query String: ${request.queryString}\n")
                    }
                }
            } else {
                ""
            }
        } catch (e: Exception) {
            ""
        }
    }
}

@Component
@ConditionalOnProperty(
    name = ["peanut-butter.notification.discord.webhook.enabled"],
    havingValue = "true"
)
class DiscordGlobalExceptionListener(
    private val discordExceptionHandler: DiscordExceptionHandler
) {

    @EventListener
    fun handleUncaughtException(event: UncaughtExceptionEvent) {
        discordExceptionHandler.handleException(
            exception = event.exception,
            context = "Uncaught Exception in ${event.source}",
            includeRequestInfo = false
        )
    }
}

data class UncaughtExceptionEvent(
    val exception: Throwable,
    val source: String = "Unknown"
)