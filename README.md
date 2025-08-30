# Peanut-Butter

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java Version](https://img.shields.io/badge/Java-17+-blue.svg)](https://openjdk.java.net/)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.25-purple.svg)](https://kotlinlang.org/)

Peanut-Butter is a **lightweight, modular** utility library for JVM (Java & Kotlin) projects. It provides production‑oriented building blocks for validation, structured logging (sync & coroutine), performance instrumentation, and application‑wide time zone management (with Spring Boot auto‑configuration).

**🚀 Zero Forced Dependencies**: Only SLF4J API is required. All other dependencies are optional and loaded only when needed.

## Feature Overview

| Area | Capabilities | Required Dependencies |
|------|--------------|----------------------|
| Field Validation | Class‑level equality / inequality annotations (`@FieldEquals`, `@FieldNotEquals`) | Jakarta Bean Validation API (optional) |
| Logging (Sync) | Logger helpers, structured & conditional logging, performance timers | SLF4J API only |
| Logging (Coroutines) | Async‑safe logging, execution timing for suspend functions, correlation (MDC) context | Kotlin Coroutines (optional) |
| Time Zone Management | Spring Boot auto configuration, runtime switching, safe temporary context | Spring Boot Starter (optional) |
| Performance Helpers | Execution/method timing, memory usage snapshot logging | SLF4J API only |

## Installation

### Gradle (Kotlin DSL)
```kotlin
dependencies {
    implementation("com.github.snowykte0426:peanut-butter:1.1.1")
    
    // Optional: Add only what you need
    // For validation features
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")
    implementation("org.hibernate.validator:hibernate-validator:8.0.1.Final")
    
    // For coroutine logging features
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    
    // For Spring Boot auto-configuration
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
  <version>1.1.1</version>
</dependency>

<!-- Optional: Add only what you need -->
<!-- For validation features -->
<dependency>
  <groupId>jakarta.validation</groupId>
  <artifactId>jakarta.validation-api</artifactId>
  <version>3.0.2</version>
</dependency>

<!-- For coroutine logging features -->
<dependency>
  <groupId>org.jetbrains.kotlinx</groupId>
  <artifactId>kotlinx-coroutines-core</artifactId>
  <version>1.7.3</version>
</dependency>

<!-- Choose your logging implementation -->
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

### What requires additional dependencies:
- 📦 Validation annotations → Jakarta Validation API
- 📦 Coroutine logging → Kotlin Coroutines
- 📦 Spring Boot auto-configuration → Spring Boot Starter
- 📦 Actual logging → Your choice of SLF4J implementation

## Documentation

- Usage Guide: [docs/USAGE.md](docs/USAGE.md)
- Release Notes: [docs/RELEASE_NOTES.md](docs/RELEASE_NOTES.md)
- API (Javadoc): https://javadoc.jitpack.io/com/github/snowykte0426/peanut-butter/latest/javadoc/

## Requirements

- **Minimum**: Java 17+ and SLF4J 2.0+
- **For Kotlin features**: Kotlin 1.9+
- **For validation**: Jakarta Bean Validation 3.0+
- **For coroutine logging**: Kotlin Coroutines 1.7.3+
- **For Spring Boot integration**: Spring Boot 3.1.x
