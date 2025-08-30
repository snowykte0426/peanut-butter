package com.github.snowykte0426.timezone

import org.springframework.context.annotation.Import
import kotlin.annotation.AnnotationRetention
import kotlin.annotation.AnnotationTarget

/**
 * Enables automatic TimeZone configuration for the application.
 *
 * Usage:
 * ```kotlin
 * @SpringBootApplication
 * @EnableAutomaticTimeZone
 * class Application
 * ```
 *
 * Configuration (application.yml):
 * ```yaml
 * peanut-butter:
 *   timezone:
 *     zone: KST            # Target timezone
 *     enabled: true        # Enable auto configuration
 *     enable-logging: true # Log on initialization / change
 * ```
 *
 * Programmatic override example:
 * ```kotlin
 * @Configuration
 * class TimeZoneConfig {
 *     @Bean
 *     fun timeZoneProperties(): TimeZoneProperties =
 *         TimeZoneProperties(zone = "UTC", enableLogging = false)
 * }
 * ```
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Import(TimeZoneAutoConfiguration::class)
annotation class EnableAutomaticTimeZone
