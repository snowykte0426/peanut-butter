# Peanut-Butter Usage Guide 🥜🧈

This guide provides comprehensive examples and best practices for using the Peanut-Butter library.

## Table of Contents

- [Field Validation](#field-validation)
- [Logging Extensions](#logging-extensions)
- [Coroutine Logging](#coroutine-logging)
- [Best Practices](#best-practices)
- [Advanced Examples](#advanced-examples)

## Field Validation

### @FieldEquals Annotation

Validates that specified fields have equal values.

```java
import com.github.snowykte0426.validation.FieldEquals;

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
    message = "비밀번호와 비밀번호 확인이 일치하지 않습니다."
)
public class UserForm {
    private String password;
    private String passwordConfirm;
}
```

### @FieldNotEquals Annotation

Validates that specified fields have different values.

```java
import com.github.snowykte0426.validation.FieldNotEquals;

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
    message = "새 비밀번호는 현재 비밀번호와 달라야 합니다."
)
public class PasswordChangeForm {
    private String currentPassword;
    private String newPassword;
    private String newPasswordConfirm;
}
```

## Logging Extensions

### Basic Logger Creation

```kotlin
import com.github.snowykte0426.logging.*

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
import com.github.snowykte0426.logging.*

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

## Advanced Examples

### Complex Validation Scenario

```java
@FieldEquals(fields = {"email", "emailConfirm"})
@FieldEquals(fields = {"password", "passwordConfirm"})
@FieldNotEquals(fields = {"username", "password"})
@FieldNotEquals(fields = {"username", "email"})
public class ComplexRegistrationForm {
    @NotBlank
    private String username;
    
    @Email
    private String email;
    private String emailConfirm;
    
    @Size(min = 8)
    private String password;
    private String passwordConfirm;
}
```

### Microservice Architecture Example

```kotlin
@Service
class UserMicroservice {
    private val logger by lazyLogger()
    
    suspend fun handleUserRequest(request: UserRequest): UserResponse {
        return withLoggingContext("req-${request.id}") {
            logInfoAsync("Handling user request: {}", request.type)
            
            val result = logExecutionTimeAsync("Request processing") {
                when (request.type) {
                    "CREATE" -> createUserWithRetry(request.userData)
                    "UPDATE" -> updateUserWithValidation(request.userData)
                    "DELETE" -> deleteUserSafely(request.userId)
                    else -> throw IllegalArgumentException("Unknown request type")
                }
            }
            
            logInfoAsync("Request completed successfully")
            result
        }
    }
    
    private suspend fun createUserWithRetry(userData: UserData): User {
        return retryWithLogging(
            operation = "User creation",
            maxAttempts = 3
        ) {
            userRepository.saveAsync(userData)
        }
    }
}
```

### Performance Monitoring Example

```kotlin
class PerformanceMonitoringService {
    suspend fun monitorSystemPerformance() {
        executeParallelWithLogging {
            listOf(
                suspend { monitorDatabasePerformance() },
                suspend { monitorAPIPerformance() },
                suspend { monitorMemoryUsage() },
                suspend { monitorCPUUsage() }
            )
        }
    }
    
    private suspend fun monitorDatabasePerformance() {
        logExecutionTimeAsync("Database health check") {
            databaseHealthChecker.checkHealth()
        }
    }
    
    private suspend fun monitorAPIPerformance() {
        logMethodExecutionAsync("API performance check") {
            apiPerformanceChecker.checkPerformance()
        }
    }
}
```

---

For more information, see the [main README](../README.md) or visit the [GitHub repository](https://github.com/snowykte0426/peanut-butter).
