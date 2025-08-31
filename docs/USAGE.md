# Peanut-Butter Usage Guide 🥜🧈

This guide provides comprehensive examples and best practices for using the Peanut-Butter library.

## Table of Contents

- [Dependency Management](#dependency-management)
- [Field Validation](#field-validation)
- [File Upload Validation](#file-upload-validation)
- [Hexagonal Architecture Annotations](#hexagonal-architecture-annotations)
- [Logging Extensions](#logging-extensions)
- [Coroutine Logging](#coroutine-logging)
- [TimeZone Utilities](#timezone-utilities)
- [Best Practices](#best-practices)
- [Advanced Examples](#advanced-examples)

## Dependency Management

Peanut-Butter is designed to be **lightweight and modular**. You only need to include dependencies for the features you actually use.

### Core Installation (Minimal)

```kotlin
dependencies {
    implementation("com.github.snowykte0426:peanut-butter:1.1.4")
    
    // Choose your logging implementation (required for any logging functionality)
    implementation("ch.qos.logback:logback-classic:1.5.13")
    // OR implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.21.1")
}
```

This gives you:
- ✅ Basic logging extensions (`logger()`, `logInfo()`, etc.)
- ✅ Performance timing utilities
- ✅ Core timezone enums and utilities
- ✅ Hexagonal annotations

### Add Features as Needed

#### For Validation Annotations
```kotlin
dependencies {
    // Add validation support
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")
    implementation("org.hibernate.validator:hibernate-validator:8.0.1.Final")
    implementation("org.glassfish:jakarta.el:4.0.2") // For expression evaluation
}
```

#### For File Upload Constraint (`@NotEmptyFile`)
```kotlin
dependencies {
    // Add file upload validation support
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")
    implementation("org.hibernate.validator:hibernate-validator:8.0.1.Final")
    implementation("org.springframework:spring-web:6.2.8") // MultipartFile
}
```

#### For Coroutine Logging
```kotlin
dependencies {
    // Add coroutine support
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
}
```

#### For Spring Boot Auto-Configuration
```kotlin
dependencies {
    // Add Spring Boot integration
    implementation("org.springframework.boot:spring-boot-starter:3.1.5")
}
```

### Feature Availability Matrix

| Feature | Core Only | + Validation | + Multipart | + Coroutines | + Spring Boot |
|---------|-----------|--------------|-------------|--------------|---------------|
| Basic logging | ✅ | ✅ | ✅ | ✅ | ✅ |
| Performance timing | ✅ | ✅ | ✅ | ✅ | ✅ |
| Timezone utilities | ✅ | ✅ | ✅ | ✅ | ✅ |
| `@FieldEquals` / `@FieldNotEquals` | ❌ | ✅ | ✅ | ✅ | ✅ |
| `@NotEmptyFile` | ❌ | ❌ | ✅ | ✅ | ✅ |
| Async logging (`logInfoAsync`) | ❌ | ❌ | ❌ | ✅ | ✅ |
| Auto timezone configuration | ❌ | ❌ | ❌ | ❌ | ✅ |
| Hexagonal annotations | ✅ | ✅ | ✅ | ✅ | ✅ |

## Field Validation

**Dependencies required**: Jakarta Validation API + implementation

### @FieldEquals Annotation

Validates that specified fields have equal values.

```java
@FieldEquals(fields = {"password", "passwordConfirm"})
public class UserRegistrationForm {
    private String username;
    private String password;
    private String passwordConfirm;
    
    // constructors, getters, setters...
}
```

#### Multiple Field Equality Checks

```java
@FieldEquals(fields = {"email", "emailConfirm"})
@FieldEquals(fields = {"password", "passwordConfirm"})
public class RegistrationForm {
    private String email;
    private String emailConfirm;
    private String password;
    private String passwordConfirm;
}
```

#### Custom Error Messages

```java
@FieldEquals(
    fields = {"password", "passwordConfirm"}, 
    message = "Password and confirmation do not match."
)
public class UserForm {
    private String password;
    private String passwordConfirm;
}
```

### @FieldNotEquals Annotation

Validates that specified fields have different values.

```java
@FieldNotEquals(fields = {"username", "password"})
public class UserForm {
    private String username;
    private String password;

    // constructors, getters, setters...
}
```

#### Security Example

```java
@FieldNotEquals(
    fields = {"currentPassword", "newPassword"}, 
    message = "New password must be different from current password."
)
public class PasswordChangeForm {
    private String currentPassword;
    private String newPassword;
    private String newPasswordConfirm;
}
```

## File Upload Validation

`@NotEmptyFile` ensures a `MultipartFile` field is present and not empty.

```java
public class UploadRequest {
    @NotEmptyFile(message = "Image file is required")
    private MultipartFile image;
}
```

Behavior:
- Valid when the field is non-null and `!file.isEmpty()`
- Invalid when null or `file.isEmpty()`
- Works with Jakarta Bean Validation. Requires Spring Web's `MultipartFile` type.

## Hexagonal Architecture Annotations

Lightweight semantic markers to make boundaries explicit.

```java
@Port(direction = PortDirection.INBOUND)
public interface UserQueryPort { UserView find(String id); }

@Adapter(direction = PortDirection.INBOUND)
public class UserQueryRestAdapter implements UserQueryPort {
    public UserView find(String id) { /* ... */ return null; }
}
```

Guidelines:
- Use `INBOUND` for interfaces/adapter entry points that receive external requests (REST, Messaging) calling into the domain.
- Use `OUTBOUND` for interfaces that the domain uses to reach external systems (DB, other services).
- `@Port` is SOURCE retention (no runtime overhead). `@Adapter` is runtime & meta-annotated with `@Component` so Spring can discover it.

Benefits:
- Structural clarity without introducing a framework dependency for ports.
- Enables future static analysis (annotation processors or custom build checks).

## Logging Extensions

### Basic Logger Creation

```kotlin
class UserService {
    // Simple logger creation
    private val logger = logger()
    
    // Lazy logger (performance optimized)
    private val lazyLogger by lazyLogger()
    
    // Custom logger name
    private val customLogger = logger("UserServiceCustom")
}
```

### Companion Object Logging

```kotlin
class UserService {
    companion object {
        private val logger = companionLogger()
        
        fun staticMethod() {
            logger.info("Static method called")
        }
    }
}
```

### Convenient Logging Methods

```kotlin
class UserService {
    fun createUser(userData: UserData) {
        // Basic logging
        logInfo("Creating user: {}", userData.username)
        logDebug("User data: {}", userData)
        logWarn("Warning: User creation attempted")
        logError("Error occurred", RuntimeException("test"))
        
        // Conditional logging (performance optimized)
        logDebugIf { "Expensive debug info: ${generateExpensiveDebugInfo()}" }
        logInfoIf { "Conditional info: ${someExpensiveOperation()}" }
    }
}
```

### Performance Logging

```kotlin
class UserService {
    fun createUser(userData: UserData) {
        // Simple execution time logging
        val result = logExecutionTime("User creation") {
            userRepository.save(userData)
        }
        
        // Method execution tracking
        val validatedUser = logMethodExecution(
            "validateUser",
            mapOf("userId" to userData.id, "type" to userData.type)
        ) {
            userValidator.validate(userData)
        }
        
        // Performance logging with memory usage
        val processedData = logPerformance("Data processing") {
            dataProcessor.process(userData)
        }
    }
}
```

### Exception Handling with Logging

```kotlin
class UserService {
    fun createUser(userData: UserData): User? {
        // Log exceptions and return null on failure
        return logOnException("User creation") {
            userRepository.save(userData)
        }
    }
    
    fun createUserWithDefault(userData: UserData): User {
        // Log warnings and return default value on failure
        return logWarningOnException("User creation", User.defaultUser()) {
            userRepository.save(userData)
        }
    }
}
```

## Coroutine Logging

### Basic Async Logging

```kotlin
class AsyncUserService {
    suspend fun createUser(userData: UserData) {
        // Async-safe logging
        logInfoAsync("Starting user creation: {}", userData.username)
        logDebugAsync("User data: {}", userData)
        logErrorAsync("Error occurred", RuntimeException("async error"))
        
        // All logging operations are thread-safe in coroutine context
        repeat(10) {
            launch {
                logInfoAsync("Concurrent logging: {}", it)
            }
        }
    }
}
```

### Async Performance Logging

```kotlin
class AsyncUserService {
    suspend fun createUser(userData: UserData) {
        // Measure execution time for suspend functions
        val result = logExecutionTimeAsync("Async user creation") {
            delay(100) // Simulate async operation
            userRepository.saveAsync(userData)
        }
        
        // Method execution tracking in coroutines
        val validatedUser = logMethodExecutionAsync(
            "validateUserAsync",
            mapOf("userId" to userData.id)
        ) {
            validationService.validateAsync(userData)
        }
    }
}
```

### Exception Handling in Coroutines

```kotlin
class AsyncUserService {
    suspend fun createUser(userData: UserData): User? {
        // Handle exceptions in coroutine context
        return logOnExceptionAsync("Async user creation") {
            userRepository.saveAsync(userData)
        }
    }
}
```

### Retry with Exponential Backoff

```kotlin
class AsyncUserService {
    suspend fun callExternalAPI(request: APIRequest): APIResponse {
        return retryWithLogging(
            operation = "External API call",
            maxAttempts = 5,
            initialDelay = 1000L,
            maxDelay = 30000L,
            backoffFactor = 2.0
        ) {
            externalAPIClient.call(request)
        }
    }
}
```

### Correlation ID Tracking

```kotlin
class AsyncUserService {
    suspend fun processUserOperation(userId: String) {
        // All logs within this block will include the correlation ID
        withLoggingContext("user-op-${UUID.randomUUID()}") {
            logInfoAsync("Starting user operation")
            
            val user = fetchUserAsync(userId)
            val result = processUserAsync(user)
            
            logInfoAsync("User operation completed")
        }
    }
    
    suspend fun processWithCustomCorrelationId(correlationId: String) {
        withLoggingContext(correlationId) {
            logInfoAsync("Processing with custom correlation ID")
            // Your business logic here
        }
    }
}
```

### Parallel Execution with Logging

```kotlin
class AsyncUserService {
    suspend fun processMultipleUsers(users: List<UserData>) {
        val results = executeParallelWithLogging {
            users.map { user ->
                suspend {
                    logInfoAsync("Processing user: {}", user.id)
                    processUserAsync(user)
                }
            }
        }
        
        logInfoAsync("Processed {} users", results.size)
    }
}
```

### Coroutine Context Logging

```kotlin
class AsyncUserService {
    suspend fun debugCoroutineContext() {
        // Log current coroutine context information
        logCoroutineContext()
        
        withContext(Dispatchers.IO) {
            logCoroutineContext() // Will show different dispatcher
        }
    }
}
```

## Best Practices

### 1. Logger Creation

```kotlin
// ✅ Good: Use lazy logger for better performance
class UserService {
    private val logger by lazyLogger()
}

// ✅ Good: Use companion object for static access
class UserService {
    companion object {
        private val logger = companionLogger()
    }
}

// ❌ Avoid: Creating logger in every method
class UserService {
    fun someMethod() {
        val logger = logger() // Creates new logger each time
    }
}
```

### 2. Conditional Logging

```kotlin
// ✅ Good: Use conditional logging for expensive operations
logDebugIf { "Expensive debug info: ${generateExpensiveDebugInfo()}" }

// ❌ Avoid: Always generating expensive debug info
logDebug("Expensive debug info: ${generateExpensiveDebugInfo()}")
```

### 3. Coroutine Logging

```kotlin
// ✅ Good: Use async logging in suspend functions
suspend fun createUser() {
    logInfoAsync("Creating user")
}

// ❌ Avoid: Using sync logging in suspend functions
suspend fun createUser() {
    logInfo("Creating user") // Blocks the current thread
}
```

### 4. Exception Handling

```kotlin
// ✅ Good: Use logging exception handlers
val result = logOnException("Risky operation") {
    riskyOperation()
}

// ✅ Good: Provide fallback values
val result = logWarningOnException("Operation", defaultValue) {
    riskyOperation()
}
```

## TimeZone Utilities

Powerful utilities for configuring and working with application-wide time zones. Includes Spring Boot auto-configuration, runtime switching, and convenient conversion/inspection helpers.

### 1. Auto Configuration (Spring Boot)

Add properties to your `application.yml` (auto configuration active by default unless disabled):

```yaml
peanut-butter:
  timezone:
    enabled: true          # Enable/disable auto configuration (default true)
    zone: UTC              # Target timezone (default UTC)
    enable-logging: true   # Log on initialization / failures (default true)
```

On startup the library sets `user.timezone` and `TimeZone.setDefault(...)`.

### 2. Supported Time Zones

`SupportedTimeZone` enum:
```
UTC, KST, JST, GMT, WET, BST, CET, WEST, CEST, EET, EEST, MST, PT, ET
```
Match is case‑insensitive and also accepts the underlying Zone ID (e.g. `Asia/Seoul`).

### 3. Enabling via Annotation

```kotlin
@SpringBootApplication
@EnableAutomaticTimeZone
class Application
```

(Annotation is optional if classpath scanning and properties are sufficient.)

### 4. Programmatic Control

Use `TimeZoneInitializer` bean (injected by Spring) to switch at runtime:

```kotlin
@Service
class TimeZoneService(private val initializer: TimeZoneInitializer) {
    fun switchToKst() = initializer.changeTimeZone("KST")
    fun switchToUtc() = initializer.changeTimeZone(SupportedTimeZone.UTC)
    fun current(): TimeZone = initializer.getCurrentTimeZone()
    fun supported(): List<SupportedTimeZone> = initializer.getSupportedTimeZones()
}
```

### 5. Extension Functions Overview

| Purpose | Function | Example |
|---------|----------|---------|
| Current time in zone | `Any.getCurrentTimeIn(zone)` | `getCurrentTimeIn(SupportedTimeZone.UTC)` |
| Current time by string | `Any.getCurrentTimeIn("KST")` | `getCurrentTimeIn("KST")` |
| Convert `LocalDateTime` -> `ZonedDateTime` | `LocalDateTime.inTimeZone(zone)` | `now.inTimeZone("ET")` |
| Convert between zones | `ZonedDateTime.convertToTimeZone(zone)` | `zdt.convertToTimeZone("PT")` |
| Check current default | `Any.isCurrentTimeZone(zone)` | `isCurrentTimeZone("UTC")` |
| Get display name | `Any.getCurrentTimeZoneDisplayName()` | `getCurrentTimeZoneDisplayName()` |
| Temporary zone context | `Any.withTimeZone(zone) {}` | `withTimeZone("UTC") { runJob() }` |

### 6. Usage Examples

#### Get Current Time in Multiple Zones
```kotlin
val utcNow = getCurrentTimeIn(SupportedTimeZone.UTC)
val seoulNow = getCurrentTimeIn("KST")
```

#### Convert LocalDateTime
```kotlin
val createdAt: LocalDateTime = LocalDateTime.now()
val createdInEt = createdAt.inTimeZone("ET")
```

#### Convert ZonedDateTime
```kotlin
val utcZdt = ZonedDateTime.now(ZoneId.of("UTC"))
val inPacific = utcZdt.convertToTimeZone(SupportedTimeZone.PT)
```

#### Temporary TimeZone Context
```kotlin
withTimeZone("UTC") {
    // All date/time APIs inside see the temporary default
    generateDailyReport()
}
```

#### Safe Runtime Switch with Logging
```kotlin
try {
    initializer.changeTimeZone("KST")
} catch (ex: IllegalArgumentException) {
    logWarn("Unsupported timezone request: {}", ex.message)
}
```

### 7. Error Handling

Invalid zone strings throw `IllegalArgumentException`:
```kotlin
val t = runCatching { getCurrentTimeIn("NOT_A_ZONE") }
if (t.isFailure) logWarn("Invalid timezone input")
```

### 8. Best Practices

- Prefer enum constants over raw strings for compile‑time safety
- Keep runtime switches rare; frequent switches can confuse scheduled tasks
- Wrap critical business logic in `withTimeZone` instead of manually restoring defaults
- Log all administrative changes to default timezone
- Use `SupportedTimeZone.fromString(value)` to gracefully validate input

### 9. Migration to v1.1.0

If upgrading from <= 1.0.2:
1. Bump dependency version to `1.1.0`
2. (Optional) Add `peanut-butter.timezone` section to `application.yml`
3. Use `@EnableAutomaticTimeZone` if you want explicit opt‑in
4. Replace ad‑hoc `TimeZone.setDefault(...)` calls with `TimeZoneInitializer` or `withTimeZone`

---

For more information, see the [main README](../README.md) or visit the [GitHub repository](https://github.com/snowykte0426/peanut-butter).
