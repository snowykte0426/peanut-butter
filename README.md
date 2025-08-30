# Peanut-Butter

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java Version](https://img.shields.io/badge/Java-17+-blue.svg)](https://openjdk.java.net/)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.25-purple.svg)](https://kotlinlang.org/)

Peanut-Butter is a modular utility library for JVM (Java & Kotlin) projects. It provides production‑oriented building blocks for validation, structured logging (sync & coroutine), performance instrumentation, and application‑wide time zone management (with Spring Boot auto‑configuration).

## Feature Overview

| Area | Capabilities |
|------|--------------|
| Field Validation | Class‑level equality / inequality annotations (`@FieldEquals`, `@FieldNotEquals`) with Jakarta Bean Validation integration |
| Logging (Sync) | Logger helpers, structured & conditional logging, performance timers, exception handling wrappers |
| Logging (Coroutines) | Async‑safe logging, execution timing for suspend functions, correlation (MDC) context, parallel operation instrumentation, retry with backoff |
| Time Zone Management | Spring Boot auto configuration, runtime switching, safe temporary context, enum‑backed zone resolution, conversion extensions |
| Performance Helpers | Execution/method timing, memory usage snapshot logging |

## Installation

### Gradle (Kotlin DSL)
```kotlin
dependencies {
    implementation("com.github.snowykte0426:peanut-butter:1.1.0")
}
```

### Maven
```xml
<dependency>
  <groupId>com.github.snowykte0426</groupId>
  <artifactId>peanut-butter</artifactId>
  <version>1.1.0</version>
</dependency>
```

## Documentation

- Usage Guide: [docs/USAGE.md](docs/USAGE.md)
- Release Notes: [docs/RELEASE_NOTES.md](docs/RELEASE_NOTES.md)
- API (Javadoc): https://javadoc.jitpack.io/com/github/snowykte0426/peanut-butter/latest/javadoc/

## Requirements

- Java 17+
- Kotlin 1.9+ (for Kotlin / coroutine features)
- SLF4J 2.0+ (logging API)
- Jakarta Bean Validation 3.0+ (validation annotations)
- Kotlin Coroutines 1.7.3+ (async logging features)
- Spring Boot 3.1.x (optional; for auto time zone configuration)

## Versioning & Compatibility

The project follows semantic versioning (MAJOR.MINOR.PATCH). Backward incompatible changes are announced under **Breaking Changes** in the release notes.

## Contributing

Contributions, issue reports, and improvement suggestions are welcome. Please open an issue or submit a pull request with a clear description and relevant tests when applicable.

## License

Distributed under the MIT License. See [LICENSE](LICENSE) for full text.

## Author

Kim Tae Eun (snowykte0426)  
Email: snowykte0426@naver.com  
GitHub: https://github.com/snowykte0426

---
For detailed examples and best practices, refer to the Usage Guide.
