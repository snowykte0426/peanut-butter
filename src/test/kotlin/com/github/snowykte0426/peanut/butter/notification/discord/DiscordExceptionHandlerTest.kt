package com.github.snowykte0426.peanut.butter.notification.discord

import io.kotest.core.spec.style.DescribeSpec
import org.mockito.kotlin.*

class DiscordExceptionHandlerTest : DescribeSpec({

    describe("DiscordExceptionHandler") {
        
        context("when handling exceptions") {
            val discordWebhookService = mock<DiscordWebhookService>()
            val handler = DiscordExceptionHandler(discordWebhookService)

            it("should send exception notification") {
                val exception = RuntimeException("Test exception")
                val context = "Test context"
                
                handler.handleException(exception, context, false)
                
                verify(discordWebhookService).sendExceptionNotification(exception, context)
            }

            it("should handle exception without context") {
                val exception = IllegalArgumentException("Invalid argument")
                
                handler.handleException(exception, "", false)
                
                verify(discordWebhookService).sendExceptionNotification(exception, "")
            }
        }
    }

    describe("DiscordGlobalExceptionListener") {
        
        context("when handling uncaught exceptions") {
            val discordExceptionHandler = mock<DiscordExceptionHandler>()
            val listener = DiscordGlobalExceptionListener(discordExceptionHandler)

            it("should handle UncaughtExceptionEvent") {
                val exception = RuntimeException("Uncaught exception")
                val event = UncaughtExceptionEvent(exception, "TestClass")
                
                listener.handleUncaughtException(event)
                
                verify(discordExceptionHandler).handleException(
                    exception = exception,
                    context = "Uncaught Exception in TestClass",
                    includeRequestInfo = false
                )
            }
        }
    }
})