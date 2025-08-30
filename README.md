# Peanut-Butter 🥜🧈

A comprehensive utility library for Java and Kotlin development.

## Features

### Field Validation Annotations
- `@FieldEquals` - Validates that specified fields have equal values
- `@FieldNotEquals` - Validates that specified fields have different values

### Logging Extensions (Kotlin)
- `logger()` - Simple logger creation for any class
- `logInfo()`, `logDebug()`, `logError()` etc. - Convenient logging methods
- `logExecutionTime()` - Performance logging with timing
- `logMethodExecution()` - Method entry/exit tracking
- `logOnException()` - Exception handling with logging

## Installation

### Gradle
```kotlin
dependencies {
    implementation("com.github.snowykte0426:peanut-butter:1.0.0")
}
```

### Maven
```xml
<dependency>
    <groupId>com.github.snowykte0426</groupId>
    <artifactId>peanut-butter</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Quick Examples

### Validation
```java
@FieldEquals(fields = {"password", "passwordConfirm"})
@FieldNotEquals(fields = {"username", "password"})
public class UserForm {
    private String username;
    private String password;
    private String passwordConfirm;
}
```

### Logging (Kotlin)
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

## Requirements

- Java 17+
- SLF4J 2.0+ (for logging features)
- Jakarta Bean Validation 3.0+ (for validation features)

## License

MIT License

## Author

**Kim Tae Eun** (snowykte0426)
- Email: snowykte0426@naver.com
- GitHub: [@snowykte0426](https://github.com/snowykte0426)
