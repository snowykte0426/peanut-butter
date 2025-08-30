package com.github.snowykte0426.timezone

import com.github.snowykte0426.logging.logInfo
import com.github.snowykte0426.logging.logWarn
import java.time.LocalDateTime
import java.util.*

/**
 * Timezone initializer for programmatic timezone changes.
 */
class TimeZoneInitializer(
    private val properties: TimeZoneProperties
) {

    /**
     * Changes the timezone programmatically.
     */
    fun changeTimeZone(timeZone: SupportedTimeZone) {
        try {
            val tz = timeZone.toTimeZone()

            System.setProperty("user.timezone", timeZone.zoneId)
            TimeZone.setDefault(tz)

            if (properties.enableLogging) {
                logInfo(
                    "Timezone changed to {} ({}): Current time: {}",
                    timeZone.displayName,
                    timeZone.zoneId,
                    LocalDateTime.now()
                )
            }

        } catch (e: Exception) {
            if (properties.enableLogging) {
                logWarn("Failed to change timezone to '{}': {}", timeZone.name, e.message, e)
            }
            throw e
        }
    }

    /**
     * Changes the timezone based on a string value.
     */
    fun changeTimeZone(timeZoneString: String) {
        val supportedTimeZone = SupportedTimeZone.fromString(timeZoneString)
            ?: throw IllegalArgumentException("Unsupported timezone: $timeZoneString")

        changeTimeZone(supportedTimeZone)
    }

    /**
     * Returns the currently set timezone.
     */
    fun getCurrentTimeZone(): TimeZone = TimeZone.getDefault()

    /**
     * Returns a list of all supported timezones.
     */
    fun getSupportedTimeZones(): List<SupportedTimeZone> = SupportedTimeZone.values().toList()
}
