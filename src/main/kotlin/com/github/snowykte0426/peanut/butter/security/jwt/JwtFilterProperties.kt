package com.github.snowykte0426.peanut.butter.security.jwt

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.DefaultValue

@ConfigurationProperties(prefix = "peanut-butter.security.jwt.filter")
data class JwtFilterProperties(
    @DefaultValue("false")
    val enabled: Boolean = false,
    val excludedPaths: List<String> = emptyList(),
    @DefaultValue("true")
    val autoDetectPermitAllPaths: Boolean = true
)