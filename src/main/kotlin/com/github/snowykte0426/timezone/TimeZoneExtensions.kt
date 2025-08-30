package com.github.snowykte0426.timezone

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

/**
 * Timezone extension functions that work with any object.
 * These functions provide convenient timezone operations.
 */

/**
 * Get current time in the specified timezone.
 */
fun Any.getCurrentTimeIn(timeZone: SupportedTimeZone): ZonedDateTime {
    return ZonedDateTime.now(ZoneId.of(timeZone.zoneId))
}

/**
 * Get current time in the specified timezone by string.
 */
fun Any.getCurrentTimeIn(timeZoneString: String): ZonedDateTime {
    val supportedTimeZone = SupportedTimeZone.fromString(timeZoneString)
        ?: throw IllegalArgumentException("Unsupported timezone: $timeZoneString")
    return getCurrentTimeIn(supportedTimeZone)
}

/**
 * Convert LocalDateTime to ZonedDateTime in the specified timezone.
 */
fun LocalDateTime.inTimeZone(timeZone: SupportedTimeZone): ZonedDateTime {
    return this.atZone(ZoneId.of(timeZone.zoneId))
}

/**
 * Convert LocalDateTime to ZonedDateTime in the specified timezone by string.
 */
fun LocalDateTime.inTimeZone(timeZoneString: String): ZonedDateTime {
    val supportedTimeZone = SupportedTimeZone.fromString(timeZoneString)
        ?: throw IllegalArgumentException("Unsupported timezone: $timeZoneString")
    return inTimeZone(supportedTimeZone)
}

/**
 * Convert ZonedDateTime to another timezone.
 */
fun ZonedDateTime.convertToTimeZone(timeZone: SupportedTimeZone): ZonedDateTime {
    return this.withZoneSameInstant(ZoneId.of(timeZone.zoneId))
}

/**
 * Convert ZonedDateTime to another timezone by string.
 */
fun ZonedDateTime.convertToTimeZone(timeZoneString: String): ZonedDateTime {
    val supportedTimeZone = SupportedTimeZone.fromString(timeZoneString)
        ?: throw IllegalArgumentException("Unsupported timezone: $timeZoneString")
    return convertToTimeZone(supportedTimeZone)
}

/**
 * Check if the current system timezone matches the specified timezone.
 */
fun Any.isCurrentTimeZone(timeZone: SupportedTimeZone): Boolean {
    return TimeZone.getDefault().id == timeZone.zoneId
}

/**
 * Check if the current system timezone matches the specified timezone by string.
 */
fun Any.isCurrentTimeZone(timeZoneString: String): Boolean {
    val supportedTimeZone = SupportedTimeZone.fromString(timeZoneString)
        ?: return false
    return isCurrentTimeZone(supportedTimeZone)
}

/**
 * Get the display name of current system timezone.
 */
fun Any.getCurrentTimeZoneDisplayName(): String {
    val currentZoneId = TimeZone.getDefault().id
    return SupportedTimeZone.values()
        .find { it.zoneId == currentZoneId }
        ?.displayName
        ?: currentZoneId
}

/**
 * Execute a block of code with a temporarily changed timezone.
 */
inline fun <T> Any.withTimeZone(timeZone: SupportedTimeZone, block: () -> T): T {
    val originalTimeZone = TimeZone.getDefault()
    try {
        System.setProperty("user.timezone", timeZone.zoneId)
        TimeZone.setDefault(timeZone.toTimeZone())
        return block()
    } finally {
        System.setProperty("user.timezone", originalTimeZone.id)
        TimeZone.setDefault(originalTimeZone)
    }
}

/**
 * Execute a block of code with a temporarily changed timezone by string.
 */
inline fun <T> Any.withTimeZone(timeZoneString: String, block: () -> T): T {
    val supportedTimeZone = SupportedTimeZone.fromString(timeZoneString)
        ?: throw IllegalArgumentException("Unsupported timezone: $timeZoneString")
    return withTimeZone(supportedTimeZone, block)
}
