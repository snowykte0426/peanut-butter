package com.github.snowykte0426.peanut.butter.notification.discord

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class DiscordMessageLocalizerTest : DescribeSpec({

    describe("DiscordMessageLocalizer") {
        
        context("when getting English messages") {
            val messages = DiscordMessageLocalizer.getMessages(DiscordLocale.ENGLISH)

            it("should return English messages") {
                messages.serverStartTitle shouldBe "🚀 Server Started"
                messages.serverStartDescription shouldBe "Application has started successfully."
                messages.serverStopTitle shouldBe "🛑 Server Stopped"
                messages.serverStopDescription shouldBe "Application has been stopped."
                messages.exceptionTitle shouldBe "❌ Exception Occurred"
                messages.applicationLabel shouldBe "Application"
                messages.profileLabel shouldBe "Profile"
                messages.hostnameLabel shouldBe "Hostname"
            }
        }

        context("when getting Korean messages") {
            val messages = DiscordMessageLocalizer.getMessages(DiscordLocale.KOREAN)

            it("should return Korean messages") {
                messages.serverStartTitle shouldBe "🚀 서버 시작"
                messages.serverStartDescription shouldBe "애플리케이션이 성공적으로 시작되었습니다."
                messages.serverStopTitle shouldBe "🛑 서버 종료"
                messages.serverStopDescription shouldBe "애플리케이션이 종료되었습니다."
                messages.exceptionTitle shouldBe "❌ 예외 발생"
                messages.applicationLabel shouldBe "애플리케이션"
                messages.profileLabel shouldBe "프로파일"
                messages.hostnameLabel shouldBe "호스트명"
            }
        }

        context("when messages are different between locales") {
            val englishMessages = DiscordMessageLocalizer.getMessages(DiscordLocale.ENGLISH)
            val koreanMessages = DiscordMessageLocalizer.getMessages(DiscordLocale.KOREAN)

            it("should have different content for each locale") {
                englishMessages.serverStartTitle shouldNotBe koreanMessages.serverStartTitle
                englishMessages.applicationLabel shouldNotBe koreanMessages.applicationLabel
                englishMessages.exceptionTitle shouldNotBe koreanMessages.exceptionTitle
            }
        }
    }
})