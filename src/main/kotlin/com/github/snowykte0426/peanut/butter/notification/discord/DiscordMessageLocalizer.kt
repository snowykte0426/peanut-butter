package com.github.snowykte0426.peanut.butter.notification.discord

enum class DiscordLocale(val code: String) {
    ENGLISH("en"),
    KOREAN("ko")
}

data class DiscordMessages(
    val serverStartTitle: String,
    val serverStartDescription: String,
    val serverStopTitle: String,
    val serverStopDescription: String,
    val exceptionTitle: String,
    val contextLabel: String,
    val exceptionTypeLabel: String,
    val messageLabel: String,
    val stackTraceLabel: String,
    val requestInfoLabel: String,
    val applicationLabel: String,
    val profileLabel: String,
    val hostnameLabel: String,
    val uriLabel: String,
    val methodLabel: String,
    val remoteAddressLabel: String,
    val queryStringLabel: String,
    val stackTraceTruncated: String
)

object DiscordMessageLocalizer {
    private val messages = mapOf(
        DiscordLocale.ENGLISH to DiscordMessages(
            serverStartTitle = "🚀 Server Started",
            serverStartDescription = "Application has started successfully.",
            serverStopTitle = "🛑 Server Stopped",
            serverStopDescription = "Application has been stopped.",
            exceptionTitle = "❌ Exception Occurred",
            contextLabel = "Context",
            exceptionTypeLabel = "Exception Type",
            messageLabel = "Message",
            stackTraceLabel = "Stack Trace",
            requestInfoLabel = "Request Info",
            applicationLabel = "Application",
            profileLabel = "Profile",
            hostnameLabel = "Hostname",
            uriLabel = "URI",
            methodLabel = "Method",
            remoteAddressLabel = "Remote Address",
            queryStringLabel = "Query String",
            stackTraceTruncated = "[Stack trace truncated]"
        ),
        DiscordLocale.KOREAN to DiscordMessages(
            serverStartTitle = "🚀 서버 시작",
            serverStartDescription = "애플리케이션이 성공적으로 시작되었습니다.",
            serverStopTitle = "🛑 서버 종료",
            serverStopDescription = "애플리케이션이 종료되었습니다.",
            exceptionTitle = "❌ 예외 발생",
            contextLabel = "컨텍스트",
            exceptionTypeLabel = "예외 타입",
            messageLabel = "메시지",
            stackTraceLabel = "스택트레이스",
            requestInfoLabel = "요청 정보",
            applicationLabel = "애플리케이션",
            profileLabel = "프로파일",
            hostnameLabel = "호스트명",
            uriLabel = "URI",
            methodLabel = "Method",
            remoteAddressLabel = "Remote Address",
            queryStringLabel = "Query String",
            stackTraceTruncated = "[스택트레이스가 잘렸습니다]"
        )
    )

    fun getMessages(locale: DiscordLocale): DiscordMessages {
        return messages[locale] ?: messages[DiscordLocale.ENGLISH]!!
    }
}