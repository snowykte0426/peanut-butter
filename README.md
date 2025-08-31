# Peanut-Butter

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java Version](https://img.shields.io/badge/Java-17+-blue.svg)](https://openjdk.java.net/)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.25-purple.svg)](https://kotlinlang.org/)

Peanut-Butter is a **lightweight, modular** utility library for JVM (Java & Kotlin) projects. It provides production‑oriented building blocks for validation, structured logging (sync & coroutine), performance instrumentation, application‑wide time zone management (with Spring Boot auto‑configuration), and architectural clarity helpers (hexagonal annotations).

**🚀 Zero Forced Dependencies**: Only SLF4J API is required. All other dependencies are optional and loaded only when needed.

## Feature Overview

| Area | Capabilities | Required Dependencies |
|------|--------------|----------------------|
| Field Validation | Class‑level equality / inequality (`@FieldEquals`, `@FieldNotEquals`), multipart file non‑empty (`@NotEmptyFile`) | Jakarta Bean Validation API (optional) |
| Logging (Sync) | Logger helpers, structured & conditional logging, performance timers | SLF4J API only |
| Logging (Coroutines) | Async‑safe logging, execution timing for suspend functions, correlation (MDC) context | Kotlin Coroutines (optional) |
| Time Zone Management | Spring Boot auto configuration, runtime switching, safe temporary context | Spring Boot Starter (optional) |
| CORS Configuration | Spring Security CORS auto-configuration, flexible property-based setup | Spring Boot Starter + Spring Security (optional) |
| Performance Helpers | Execution/method timing, memory usage snapshot logging | SLF4J API only |
| Hexagonal Architecture | `@Port`, `@Adapter`, `PortDirection` semantic markers | (Optional) Spring (for `@Adapter`) |

## Installation

### Gradle (Kotlin DSL)
```kotlin
dependencies {
    implementation("com.github.snowykte0426:peanut-butter:1.2.1")
    
    // Optional: Add only what you need
    // For validation features (constraints + file upload)
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")
    implementation("org.hibernate.validator:hibernate-validator:8.0.1.Final")
    implementation("org.glassfish:jakarta.el:4.0.2") // EL (needed by Hibernate Validator)
    
    // For coroutine logging features
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    
    // For Spring Boot auto-configuration & @Adapter stereotype
    implementation("org.springframework.boot:spring-boot-starter:3.1.5")
    
    // Choose your logging implementation
    implementation("ch.qos.logback:logback-classic:1.5.13") // or log4j2, etc.
}
```

### Maven
```xml
<dependency>
  <groupId>com.github.snowykte0426</groupId>
  <artifactId>peanut-butter</artifactId>
  <version>1.2.1</version>
</dependency>

<!-- Optional: Validation -->
<dependency>
  <groupId>jakarta.validation</groupId>
  <artifactId>jakarta.validation-api</artifactId>
  <version>3.0.2</version>
</dependency>
<dependency>
  <groupId>org.hibernate.validator</groupId>
  <artifactId>hibernate-validator</artifactId>
  <version>8.0.1.Final</version>
</dependency>
<dependency>
  <groupId>org.glassfish</groupId>
  <artifactId>jakarta.el</artifactId>
  <version>4.0.2</version>
</dependency>

<!-- Optional: Coroutines -->
<dependency>
  <groupId>org.jetbrains.kotlinx</groupId>
  <artifactId>kotlinx-coroutines-core</artifactId>
  <version>1.7.3</version>
</dependency>

<!-- Optional: Spring Boot + Adapter stereotype -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter</artifactId>
  <version>3.1.5</version>
</dependency>

<!-- Logging Implementation -->
<dependency>
  <groupId>ch.qos.logback</groupId>
  <artifactId>logback-classic</artifactId>
  <version>1.5.13</version>
</dependency>
```

## Minimal Dependencies

This library is designed with **dependency minimization** in mind:

- **Core requirement**: Only SLF4J API (2.0.9)
- **All other dependencies are optional** and loaded only when the corresponding features are used
- **No forced logging implementation** - you choose your preferred SLF4J implementation
- **Modular design** - use only the features you need

### What's included by default:
- ✅ Basic logging extensions (SLF4J API only)
- ✅ Performance timing utilities
- ✅ Core timezone enums and extensions
- ✅ Hexagonal port/adapter annotations (no runtime dependency except Spring for `@Adapter` component registration)

### What requires additional dependencies:
- 📦 Validation annotations → Jakarta Validation API (+ implementation)
- 📦 File upload constraint (`@NotEmptyFile`) → Jakarta Validation + Spring Web `MultipartFile`
- 📦 Coroutine logging → Kotlin Coroutines
- 📦 Spring Boot auto-configuration → Spring Boot Starter
- 📦 CORS configuration → Spring Boot Starter + Spring Security
- 📦 Actual logging → Your choice of SLF4J implementation

## Documentation

- Usage Guide: [docs/USAGE.md](docs/USAGE.md)
- Release Notes: [docs/RELEASE_NOTES.md](docs/RELEASE_NOTES.md)
- API (Javadoc): https://javadoc.jitpack.io/com/github/snowykte0426/peanut-butter/latest/javadoc/
- Security Policy: [SECURITY.md](SECURITY.md)

## Requirements

- **Minimum**: Java 17+ and SLF4J 2.0+
- **For Kotlin features**: Kotlin 1.9+
- **For validation**: Jakarta Bean Validation 3.0+
- **For coroutine logging**: Kotlin Coroutines 1.7.3+
- **For Spring Boot integration**: Spring Boot 3.1.x

## Version Support Policy
All released versions are supported **except `1.1.1`**, which is explicitly excluded from maintenance and security updates.

| Version | Support |
|---------|---------|
| 1.1.1 | Not supported (excluded) |
| Any other released version | Supported |

If you are on 1.1.1, upgrade to the latest patch or minor release to continue receiving fixes and security updates.

## Security
For vulnerability reporting and full disclosure process, see the dedicated [Security Policy](SECURITY.md).