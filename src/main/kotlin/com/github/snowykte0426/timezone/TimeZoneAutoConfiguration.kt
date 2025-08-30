package com.github.snowykte0426.timezone

import org.slf4j.LoggerFactory
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

    private val log = LoggerFactory.getLogger(TimeZoneAutoConfiguration::class.java)

    @PostConstruct
    fun init() {
        try {
            val supportedTimeZone = SupportedTimeZone.fromString(timeZoneProperties.zone)
                ?: throw IllegalArgumentException("Unsupported timezone: ${timeZoneProperties.zone}")

            val timeZone = supportedTimeZone.toTimeZone()

            System.setProperty("user.timezone", supportedTimeZone.zoneId)
            TimeZone.setDefault(timeZone)

            if (timeZoneProperties.enableLogging) {
                log.info(
                    "Default timezone set to {} ({}): Current time: {}",
                    supportedTimeZone.displayName,
                    supportedTimeZone.zoneId,
                    LocalDateTime.now()
                )
            }

        } catch (e: Exception) {
            if (timeZoneProperties.enableLogging) {
                log.warn("Failed to set default timezone to '{}': {}", timeZoneProperties.zone, e.message, e)
            }
            throw e
        }
    }

    @Bean
    open fun timeZoneInitializer(): TimeZoneInitializer {
        return TimeZoneInitializer(timeZoneProperties)
    }
}
