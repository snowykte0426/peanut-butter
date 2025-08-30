# Peanut-Butter 🥜🧈

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java Version](https://img.shields.io/badge/Java-17+-blue.svg)](https://openjdk.java.net/)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.25-purple.svg)](https://kotlinlang.org/)

A comprehensive utility library providing essential tools and helper functions for Java and Kotlin development.

## ✨ Features

### 🔍 Field Validation Annotations
- **`@FieldEquals`** - Validates that specified fields have equal values
- **`@FieldNotEquals`** - Validates that specified fields have different values

### 📝 Logging Extensions (Kotlin)
- **Basic Logging** - `logger()`, `logInfo()`, `logDebug()`, `logError()` etc.
- **Performance Logging** - `logExecutionTime()`, `logMethodExecution()`
- **Exception Handling** - `logOnException()`, `logWarningOnException()`

### ⚡ Coroutine Logging Extensions (Kotlin)
- **Async Logging** - `logInfoAsync()`, `logDebugAsync()`, `logErrorAsync()` etc.
- **Async Performance** - `logExecutionTimeAsync()`, `logMethodExecutionAsync()`
- **Advanced Features** - `retryWithLogging()`, `withLoggingContext()`, `executeParallelWithLogging()`

## 🚀 Quick Start

### Installation

#### Gradle
```kotlin
dependencies {
    implementation("com.github.snowykte0426:peanut-butter:1.0.2")
}
```

#### Maven
```xml
<dependency>
    <groupId>com.github.snowykte0426</groupId>
    <artifactId>peanut-butter</artifactId>
    <version>1.0.2</version>
</dependency>
```

### Basic Usage

#### Field Validation
```java
@FieldEquals(fields = {"password", "passwordConfirm"})
@FieldNotEquals(fields = {"username", "password"})
public class UserForm {
    private String username;
    private String password;
    private String passwordConfirm;
}
```

#### Logging
```kotlin
class UserService {
    fun createUser(userData: UserData) {
        logInfo("Creating user: {}", userData.username)
        
        val result = logExecutionTime("User creation") {
            userRepository.save(userData)
        }
    }
}
```

#### Coroutine Logging
```kotlin
class AsyncUserService {
    suspend fun createUserAsync(userData: UserData) {
        logInfoAsync("Creating user asynchronously: {}", userData.username)
        
        val result = logExecutionTimeAsync("Async user creation") {
            userRepository.saveAsync(userData)
        }
    }
}
```

## 📚 Documentation

- **[Usage Guide](docs/USAGE.md)** - Comprehensive usage examples and best practices
- **[API Documentation](https://javadoc.jitpack.io/com/github/snowykte0426/peanut-butter/latest/javadoc/)** - Full API reference

## 🔧 Requirements

- **Java 17+**
- **SLF4J 2.0+** (for logging features)
- **Jakarta Bean Validation 3.0+** (for validation features)
- **Kotlin Coroutines 1.7.3+** (for coroutine logging features)

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**Kim Tae Eun** (snowykte0426)
- 📧 Email: snowykte0426@naver.com
- 🐙 GitHub: [@snowykte0426](https://github.com/snowykte0426)

---

**Made with ❤️ for the Java & Kotlin community**