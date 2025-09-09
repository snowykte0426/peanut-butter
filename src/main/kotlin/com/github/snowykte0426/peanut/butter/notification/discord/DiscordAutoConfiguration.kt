package com.github.snowykte0426.peanut.butter.notification.discord

import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.web.client.RestTemplate

@AutoConfiguration
@EnableConfigurationProperties(DiscordProperties::class)
@ComponentScan(basePackages = ["com.github.snowykte0426.peanut.butter.notification.discord"])
@ConditionalOnProperty(
    name = ["peanut-butter.notification.discord.webhook.enabled"],
    havingValue = "true"
)
class DiscordAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    fun discordRestTemplate(): RestTemplate {
        return RestTemplate()
    }
}