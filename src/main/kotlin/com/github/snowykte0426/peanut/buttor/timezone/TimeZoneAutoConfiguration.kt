package com.github.snowykte0426.peanut.buttor.timezone

import com.github.snowykte0426.peanut.buttor.logging.logInfo
import com.github.snowykte0426.peanut.buttor.logging.logWarn
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDateTime
import java.util.*
import jakarta.annotation.PostConstruct

/**
 * Auto-configuration class for timezone settings.
 */
@Configuration
@EnableConfigurationProperties(TimeZoneProperties::class)
@ConditionalOnProperty(
    prefix = "peanut-butter.timezone",
    name = ["enabled"],
    havingValue = "true",
    matchIfMissing = true
)
open class TimeZoneAutoConfiguration(
    private val timeZoneProperties: TimeZoneProperties
) {

    @PostConstruct
    fun init() {
        try {
            val supportedTimeZone = SupportedTimeZone.fromString(timeZoneProperties.zone)
                ?: throw IllegalArgumentException("Unsupported timezone: ${timeZoneProperties.zone}")

            val timeZone = supportedTimeZone.toTimeZone()

            System.setProperty("user.timezone", supportedTimeZone.zoneId)
            TimeZone.setDefault(timeZone)

            if (timeZoneProperties.enableLogging) {
                logInfo(
                    "Default timezone set to {} ({}): Current time: {}",
                    supportedTimeZone.displayName,
                    supportedTimeZone.zoneId,
                    LocalDateTime.now()
                )
            }

        } catch (e: Exception) {
            if (timeZoneProperties.enableLogging) {
                logWarn("Failed to set default timezone to '{}': {}", timeZoneProperties.zone, e.message, e)
            }
            throw e
        }
    }

    @Bean
    open fun timeZoneInitializer(): TimeZoneInitializer {
        return TimeZoneInitializer(timeZoneProperties)
    }
}
