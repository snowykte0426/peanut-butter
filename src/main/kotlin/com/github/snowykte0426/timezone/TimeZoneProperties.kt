package com.github.snowykte0426.timezone

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

/**
 * Configuration properties for timezone settings.
 * Can be configured under `peanut-butter.timezone` in application.yml.
 */
@ConfigurationProperties(prefix = "peanut-butter.timezone")
data class TimeZoneProperties @ConstructorBinding constructor(
    /**
     * Timezone to set (default: KST).
     * Supported: UTC, KST, JST, GMT, WET, BST, CET, WEST, CEST, EET, EEST, MST, PT, ET
     */
    val zone: String = "UTC",

    /**
     * Enable logging when timezone is set (default: true)
     */
    val enableLogging: Boolean = true,

    /**
     * Enable timezone configuration (default: true)
     */
    val enabled: Boolean = true
)
