package com.github.snowykte0426.timezone

import java.util.*

/**
 * Supported timezone enumeration with predefined timezone mappings.
 */
enum class SupportedTimeZone(
    val zoneId: String,
    val displayName: String
) {
    UTC("UTC", "Coordinated Universal Time"),
    KST("Asia/Seoul", "Korea Standard Time"),
    JST("Asia/Tokyo", "Japan Standard Time"),
    GMT("GMT", "Greenwich Mean Time"),
    WET("WET", "Western European Time"),
    BST("BST", "British Summer Time"),
    CET("CET", "Central European Time"),
    WEST("WET", "Western European Summer Time"),
    CEST("CEST", "Central European Summer Time"),
    EET("EET", "Eastern European Time"),
    EEST("EEST", "Eastern European Summer Time"),
    MST("MST", "Mountain Standard Time"),
    PT("America/Los_Angeles", "Pacific Time"),
    ET("America/New_York", "Eastern Time");

    fun toTimeZone(): TimeZone = TimeZone.getTimeZone(zoneId)

    companion object {
        /**
         * Finds SupportedTimeZone by string value (case-insensitive).
         * Matches both enum name and zoneId.
         */
        fun fromString(value: String): SupportedTimeZone? {
            return values().find {
                it.name.equals(value, ignoreCase = true) ||
                it.zoneId.equals(value, ignoreCase = true)
            }
        }
    }
}
