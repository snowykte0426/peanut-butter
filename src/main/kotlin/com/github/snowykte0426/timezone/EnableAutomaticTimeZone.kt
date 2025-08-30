package com.github.snowykte0426.timezone

import org.springframework.context.annotation.Import
import kotlin.annotation.AnnotationRetention
import kotlin.annotation.AnnotationTarget

/**
 * TimeZone 설정 기능을 활성화하는 어노테이션
 *
 * 사용법:
 * ```kotlin
 * @SpringBootApplication
 * @EnableTimeZoneSet
 * class Application
 * ```
 *
 * 설정 방법:
 * 1. application.yml을 통한 설정:
 * ```yaml
 * peanut-butter:
 *   timezone:
 *     zone: KST
 *     enabled: true
 *     enableLogging: true
 * ```
 *
 * 2. Configuration 클래스를 통한 설정:
 * ```kotlin
 * @Configuration
 * class TimeZoneConfig {
 *     @Bean
 *     fun timeZoneProperties(): TimeZoneProperties {
 *         return TimeZoneProperties(zone = "UTC", enableLogging = false)
 *     }
 * }
 * ```
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Import(TimeZoneAutoConfiguration::class)
annotation class EnableAutomaticTimeZone
