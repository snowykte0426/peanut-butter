# Peanut-Butter Usage Guide 🥜🧈

**Language**: [한국어](ko/USAGE.md)

This guide provides comprehensive examples and best practices for using the Peanut-Butter library.

## Table of Contents

- [Dependency Management](#dependency-management)
- [Field Validation](#field-validation)
- [File Upload Validation](#file-upload-validation)
- [Hexagonal Architecture Annotations](#hexagonal-architecture-annotations)
- [Logging Extensions](#logging-extensions)
- [Coroutine Logging](#coroutine-logging)
- [TimeZone Utilities](#timezone-utilities)
- [CORS Configuration](#cors-configuration)
- [JWT Authentication](#jwt-authentication)
- [Best Practices](#best-practices)
- [Advanced Examples](#advanced-examples)

## Dependency Management

Peanut-Butter is designed to be **lightweight and modular**. You only need to include dependencies for the features you actually use.

### Core Installation (Minimal)

```kotlin
dependencies {
    implementation("com.github.snowykte0426:peanut-butter:1.3.1")
    
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

#### For CORS Configuration
```kotlin
dependencies {
    // Option 1: Use Spring Security modules directly (matches library compileOnly versions)
    implementation("org.springframework.security:spring-security-web:6.3.5")
    implementation("org.springframework.security:spring-security-config:6.3.5")
    implementation("org.springframework.boot:spring-boot-starter:3.1.5") // if Spring Boot auto-config also needed
    
    // Option 2: Simpler (brings in matching security modules transitively)
    // implementation("org.springframework.boot:spring-boot-starter-security:3.1.5")
}
```

#### For JWT Authentication
```kotlin
dependencies {
    // JWT support (required)
    implementation("io.jsonwebtoken:jjwt-api:0.12.3")
    implementation("io.jsonwebtoken:jjwt-impl:0.12.3")
    implementation("io.jsonwebtoken:jjwt-jackson:0.12.3")
    
    // For servlet context support (required for current user provider)
    implementation("jakarta.servlet:jakarta.servlet-api:6.0.0")
    
    // For Redis refresh token storage (optional)
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    
    // For database refresh token storage (optional)  
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    
    // For Spring Boot integration
    implementation("org.springframework.boot:spring-boot-starter:3.1.5")
}
```

### Feature Availability Matrix

| Feature | Core Only | + Validation | + Multipart | + Coroutines | + Spring Boot | + CORS | + JWT |
|---------|-----------|--------------|-------------|--------------|---------------|--------|-------|
| Basic logging | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| Performance timing | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| Timezone utilities | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `@FieldEquals` / `@FieldNotEquals` | ❌ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `@NotEmptyFile` | ❌ | ❌ | ✅ | ✅ | ✅ | ✅ | ✅ |
| Async logging (`logInfoAsync`) | ❌ | ❌ | ❌ | ✅ | ✅ | ✅ | ✅ |
| Auto timezone configuration | ❌ | ❌ | ❌ | ❌ | ✅ | ✅ | ✅ |
| CORS auto-configuration | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ | ✅ |
| JWT token management | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ |
| Refresh token storage | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ |
| Current user provider | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ |
| JWT authentication filter | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ |
| Hexagonal annotations | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |

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

## CORS Configuration

**Dependencies required**: Spring Boot + Spring Security (web + config modules or Boot starter security)

Peanut-Butter provides comprehensive CORS (Cross-Origin Resource Sharing) configuration with Spring Boot auto-configuration support. The CORS module automatically configures CORS policies based on your application properties and integrates seamlessly with Spring Security.

### 1. Basic Setup and Configuration

CORS is automatically enabled when Spring Security is on the classpath. The module provides sensible defaults while allowing full customization.

#### Auto Configuration (Spring Boot)

Add CORS properties to your `application.yml`:

```yaml
peanut-butter:
  security:
    cors:
      enabled: true                    # Enable/disable CORS (default: true)
      allowed-origins: ["*"]           # Allowed origins (default: ["*"])
      allowed-headers: ["*"]           # Allowed headers (default: ["*"])
      allow-credentials: true          # Allow credentials (default: true)
      max-age: 3600                    # Preflight cache duration in seconds (default: 3600)
      exposed-headers: []              # Headers exposed to client (default: empty)
      allowed-methods:                 # HTTP methods configuration (default: all except TRACE)
        GET: true
        POST: true
        PUT: true
        DELETE: true
        PATCH: true
        HEAD: true
        OPTIONS: true
        TRACE: false
```

### 2. Property Configuration Examples

#### Development Configuration
```yaml
peanut-butter:
  security:
    cors:
      enabled: true
      allowed-origins: 
        - "http://localhost:3000"
        - "http://localhost:8080"
        - "http://127.0.0.1:3000"
      allowed-headers:
        - "Content-Type"
        - "Authorization"
        - "X-Requested-With"
        - "Accept"
      allow-credentials: true
      max-age: 1800
```

#### Production Configuration
```yaml
peanut-butter:
  security:
    cors:
      enabled: true
      allowed-origins:
        - "https://myapp.com"
        - "https://www.myapp.com"
        - "https://api.myapp.com"
      allowed-headers:
        - "Content-Type"
        - "Authorization"
        - "X-API-Key"
      allowed-methods:
        GET: true
        POST: true
        PUT: true
        DELETE: false          # Disable DELETE in production
        PATCH: true
        HEAD: true
        OPTIONS: true
        TRACE: false
      allow-credentials: true
      max-age: 86400            # 24 hours
      exposed-headers:
        - "X-Total-Count"
        - "X-Page-Count"
        - "Link"
```

#### API-Only Configuration
```yaml
peanut-butter:
  security:
    cors:
      enabled: true
      allowed-origins: ["https://client.example.com"]
      allowed-headers: 
        - "Content-Type"
        - "Authorization"
        - "X-API-Version"
      allowed-methods:
        GET: true
        POST: true
        PUT: true
        DELETE: true
        PATCH: false
        HEAD: false
        OPTIONS: true
        TRACE: false
      allow-credentials: false
      max-age: 7200
      exposed-headers:
        - "X-RateLimit-Remaining"
        - "X-RateLimit-Reset"
```

### 3. Advanced Configuration Examples

#### Programmatic Configuration

If you need more control, you can access the `CorsConfigurationSource` bean:

```kotlin
@Service
class CustomCorsService(
    private val corsConfigurationSource: CorsConfigurationSource
) {
    fun getCorsConfiguration(): CorsConfiguration? {
        return corsConfigurationSource.getCorsConfiguration(null)
    }
    
    fun isOriginAllowed(origin: String): Boolean {
        val config = getCorsConfiguration()
        return config?.allowedOrigins?.contains(origin) ?: false
    }
}
```

#### Custom CORS Configuration Bean

You can override the default configuration by providing your own bean:

```kotlin
@Configuration
class CustomCorsConfiguration {
    
    @Bean
    @Primary
    fun customCorsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration().apply {
            allowedOriginPatterns = listOf("https://*.mycompany.com")
            allowedHeaders = listOf("*")
            allowedMethods = listOf("GET", "POST", "PUT", "DELETE")
            allowCredentials = true
            maxAge = 3600L
        }
        
        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/api/**", configuration)
            registerCorsConfiguration("/public/**", createPublicCorsConfiguration())
        }
    }
    
    private fun createPublicCorsConfiguration(): CorsConfiguration {
        return CorsConfiguration().apply {
            allowedOrigins = listOf("*")
            allowedHeaders = listOf("Content-Type")
            allowedMethods = listOf("GET", "HEAD", "OPTIONS")
            allowCredentials = false
            maxAge = 86400L
        }
    }
}
```

### 4. SecurityFilterChain Integration Examples

#### Using the Default Security Filter Chain

The library provides a basic SecurityFilterChain with CORS enabled:

```kotlin
// No additional configuration needed - auto-configured SecurityFilterChain
// includes CORS configuration from properties
```

#### Custom SecurityFilterChain with CORS

For production applications, create your own SecurityFilterChain:

```kotlin
@Configuration
@EnableWebSecurity
class SecurityConfiguration(
    private val corsConfigurationSource: CorsConfigurationSource
) {
    
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .cors { cors ->
                cors.configurationSource(corsConfigurationSource)
            }
            .csrf { csrf ->
                csrf.disable() // Disable for APIs, configure as needed
            }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/api/public/**").permitAll()
                    .requestMatchers("/api/admin/**").hasRole("ADMIN")
                    .anyRequest().authenticated()
            }
            .oauth2ResourceServer { oauth2 ->
                oauth2.jwt { }
            }
            .build()
    }
}
```

#### Multiple CORS Configurations

Configure different CORS policies for different endpoints:

```kotlin
@Configuration
class MultiCorsConfiguration {
    
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val source = UrlBasedCorsConfigurationSource()
        
        // API endpoints - strict CORS
        val apiConfig = CorsConfiguration().apply {
            allowedOrigins = listOf("https://app.mycompany.com")
            allowedHeaders = listOf("Content-Type", "Authorization")
            allowedMethods = listOf("GET", "POST", "PUT", "DELETE")
            allowCredentials = true
            maxAge = 3600L
        }
        source.registerCorsConfiguration("/api/**", apiConfig)
        
        // Public endpoints - relaxed CORS
        val publicConfig = CorsConfiguration().apply {
            allowedOrigins = listOf("*")
            allowedHeaders = listOf("Content-Type")
            allowedMethods = listOf("GET", "HEAD", "OPTIONS")
            allowCredentials = false
            maxAge = 86400L
        }
        source.registerCorsConfiguration("/public/**", publicConfig)
        
        return source
    }
}
```

#### WebMvc CORS Configuration

For non-security CORS configuration (WebMvc only):

```kotlin
@Configuration
class WebMvcCorsConfiguration : WebMvcConfigurer {
    
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/api/**")
            .allowedOrigins("https://app.mycompany.com")
            .allowedMethods("GET", "POST", "PUT", "DELETE")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600)
    }
}
```

### 5. Best Practices

#### Security Best Practices

```yaml
# ✅ Good: Specific origins in production
peanut-butter:
  security:
    cors:
      allowed-origins: 
        - "https://myapp.com"
        - "https://www.myapp.com"

# ❌ Avoid: Wildcard origins in production with credentials
peanut-butter:
  security:
    cors:
      allowed-origins: ["*"]
      allow-credentials: true  # Security risk!
```

#### Method Configuration

```yaml
# ✅ Good: Explicitly configure needed methods
peanut-butter:
  security:
    cors:
      allowed-methods:
        GET: true
        POST: true
        PUT: true
        DELETE: false        # Disable if not needed
        PATCH: true
        HEAD: true
        OPTIONS: true
        TRACE: false         # Always disable TRACE

# ❌ Avoid: Allowing all methods without consideration
```

#### Header Configuration

```yaml
# ✅ Good: Specific headers for security
peanut-butter:
  security:
    cors:
      allowed-headers:
        - "Content-Type"
        - "Authorization"
        - "X-Requested-With"
        - "Accept"
      exposed-headers:
        - "X-Total-Count"
        - "X-Page-Size"

# ❌ Avoid: Wildcard headers in sensitive applications
peanut-butter:
  security:
    cors:
      allowed-headers: ["*"]   # Too permissive
```

#### Environment-Specific Configuration

```kotlin
// Development profile
@Configuration
@Profile("dev")
class DevCorsConfiguration {
    
    @Bean
    @Primary
    fun devCorsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration().apply {
            allowedOriginPatterns = listOf("*")  // Allow all in dev
            allowedHeaders = listOf("*")
            allowedMethods = listOf("*")
            allowCredentials = true
            maxAge = 3600L
        }
        
        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", configuration)
        }
    }
}

// Production profile  
@Configuration
@Profile("prod")
class ProdCorsConfiguration {
    
    @Bean
    @Primary
    fun prodCorsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration().apply {
            allowedOrigins = listOf("https://myapp.com")
            allowedHeaders = listOf("Content-Type", "Authorization")
            allowedMethods = listOf("GET", "POST", "PUT", "PATCH")
            allowCredentials = true
            maxAge = 86400L
        }
        
        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", configuration)
        }
    }
}
```

#### Testing CORS Configuration

```kotlin
@SpringBootTest
@TestPropertySource(properties = [
    "peanut-butter.security.cors.enabled=true",
    "peanut-butter.security.cors.allowed-origins=https://test.example.com"
])
class CorsIntegrationTest {
    
    @Autowired
    private lateinit var corsConfigurationSource: CorsConfigurationSource
    
    @Test
    fun `should configure CORS correctly`() {
        val config = corsConfigurationSource.getCorsConfiguration(null)
        
        assertThat(config?.allowedOrigins).contains("https://test.example.com")
        assertThat(config?.allowCredentials).isTrue()
    }
}
```

#### Common Configuration Patterns

| Use Case | Configuration | Notes |
|----------|---------------|-------|
| **Development** | `allowed-origins: ["*"]`, `allow-credentials: true` | Permissive for local development |
| **Single Page App** | Specific origins, `allow-credentials: true` | For authenticated SPAs |
| **Public API** | Specific origins, `allow-credentials: false` | For public APIs without auth |
| **Microservices** | Pattern-based origins, specific methods | For service-to-service communication |
| **CDN Integration** | Multiple origins, specific headers | For assets served via CDN |

#### Debugging CORS Issues

Enable debug logging to troubleshoot CORS problems:

```yaml
logging:
  level:
    org.springframework.web.cors: DEBUG
    com.github.snowykte0426.peanut.butter.security.cors: DEBUG
```

Common CORS error scenarios:
- **"Access to fetch at '...' has been blocked"**: Check `allowed-origins`
- **"Method not allowed"**: Verify HTTP method in `allowed-methods`
- **"Request header field X is not allowed"**: Add header to `allowed-headers`
- **"Credentials flag is 'true', but origin is '*'"**: Use specific origins with credentials

## JWT Authentication

**Dependencies required**: JJWT + Spring Boot (optional servlet context for current user provider)

Peanut-Butter provides comprehensive JWT (JSON Web Token) authentication with flexible refresh token management, multiple storage backends, and production-ready security features.

### 1. Basic Setup and Configuration

JWT functionality is automatically enabled when JJWT dependencies are on the classpath. The module provides sensible defaults for development with production-ready customization options.

#### Auto Configuration (Spring Boot)

Add JWT properties to your `application.yml`:

```yaml
peanut-butter:
  jwt:
    secret: "your-production-secret-key-minimum-256-bits-long"
    access-token-expiry: "PT15M"      # 15 minutes
    refresh-token-expiry: "PT24H"     # 24 hours
    refresh-token-enabled: true       # Enable refresh tokens
    refresh-token-rotation-enabled: false  # Disable rotation by default
    refresh-token-mode: "SIMPLE_VALIDATION"  # Simple validation mode
    refresh-token-store-type: "IN_MEMORY"   # In-memory storage
    used-refresh-token-handling: "REMOVE"   # Remove used tokens
```

### 2. JWT Service Usage

#### Basic Token Operations

```kotlin
@Service
class AuthenticationService(
    private val jwtService: JwtService
) {
    fun login(user: User): LoginResponse {
        // Generate access token with custom claims
        val claims = mapOf(
            "role" to user.role,
            "department" to user.department,
            "permissions" to user.permissions
        )
        val accessToken = jwtService.generateAccessToken(user.id, claims)
        
        // Generate refresh token if enabled
        val refreshToken = if (jwtService.refreshTokenEnabled) {
            jwtService.generateRefreshToken(user.id)
        } else null
        
        return LoginResponse(accessToken, refreshToken)
    }
    
    fun validateToken(token: String): Boolean {
        return jwtService.validateToken(token)
    }
    
    fun extractUserInfo(token: String): UserInfo? {
        val subject = jwtService.extractSubject(token) ?: return null
        val claims = jwtService.extractClaims(token) ?: return null
        
        return UserInfo(
            userId = subject,
            role = claims["role"] as? String,
            department = claims["department"] as? String,
            permissions = claims["permissions"] as? List<String> ?: emptyList()
        )
    }
    
    fun refreshTokens(refreshToken: String): TokenPair? {
        return jwtService.refreshTokens(refreshToken)
    }
}
```

#### Token Information Extraction

```kotlin
@Service  
class TokenService(private val jwtService: JwtService) {
    
    fun getTokenInfo(token: String): TokenInfo? {
        if (!jwtService.validateToken(token)) return null
        
        return TokenInfo(
            subject = jwtService.extractSubject(token),
            claims = jwtService.extractClaims(token),
            expiration = jwtService.extractExpiration(token),
            isExpired = jwtService.isTokenExpired(token)
        )
    }
    
    fun isTokenValid(token: String): Boolean {
        return jwtService.validateToken(token) && !jwtService.isTokenExpired(token)
    }
}
```

### 3. Current User Context

#### Using Current User Provider

```kotlin
@RestController
class UserController(
    private val currentUserProvider: CurrentUserProvider<User>
) {
    @GetMapping("/me")
    fun getCurrentUser(): User? {
        return currentUserProvider.getCurrentUser()
    }
    
    @GetMapping("/profile")  
    fun getUserProfile(): UserProfile {
        val userId = currentUserProvider.getCurrentUserId()
            ?: throw SecurityException("No authenticated user")
            
        val claims = currentUserProvider.getCurrentUserClaims()
        val role = claims?.get("role") as? String
        
        return userService.getProfile(userId, role)
    }
    
    @PostMapping("/update-profile")
    fun updateProfile(@RequestBody request: UpdateProfileRequest): ResponseEntity<*> {
        val userId = currentUserProvider.getCurrentUserId()
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
            
        userService.updateProfile(userId, request)
        return ResponseEntity.ok().build()
    }
}
```

#### Custom User Resolver

```kotlin
@Component
class CustomUserResolver(
    private val userService: UserService,
    private val roleService: RoleService
) : JwtUserResolver<User> {
    
    override fun resolveUser(subject: String, claims: Map<String, Any>): User? {
        return try {
            val user = userService.findById(subject) ?: return null
            
            // Enrich user with additional information from claims
            val role = claims["role"] as? String
            val department = claims["department"] as? String
            
            user.copy(
                currentRole = role?.let { roleService.findByName(it) },
                currentDepartment = department
            )
        } catch (e: Exception) {
            logError("Failed to resolve user", e)
            null
        }
    }
}
```

### 4. Refresh Token Storage Configuration

#### In-Memory Storage (Default)

```yaml
peanut-butter:
  jwt:
    refresh-token-store-type: "IN_MEMORY"
    refresh-token-mode: "STORE_AND_VALIDATE"
```

#### Redis Storage

```yaml
peanut-butter:
  jwt:
    refresh-token-store-type: "REDIS"
    refresh-token-mode: "STORE_AND_VALIDATE"
    
spring:
  redis:
    host: localhost
    port: 6379
    database: 0
```

#### Database Storage

```yaml
peanut-butter:
  jwt:
    refresh-token-store-type: "RDB"
    refresh-token-mode: "STORE_AND_VALIDATE"
    
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/myapp
    username: myuser
    password: mypassword
  jpa:
    hibernate:
      ddl-auto: update
```

### 5. Advanced Security Configuration

#### Token Rotation Configuration

```yaml
peanut-butter:
  jwt:
    refresh-token-rotation-enabled: true
    used-refresh-token-handling: "BLACKLIST"  # or "REMOVE"
```

#### Production Security Configuration

```yaml
peanut-butter:
  jwt:
    secret: "${JWT_SECRET}"  # Load from environment
    access-token-expiry: "PT5M"        # 5 minutes for high security
    refresh-token-expiry: "PT7D"       # 7 days
    refresh-token-rotation-enabled: true
    refresh-token-mode: "STORE_AND_VALIDATE"
    refresh-token-store-type: "REDIS"
    used-refresh-token-handling: "BLACKLIST"
```

### 6. Configuration Examples by Environment

#### Development Configuration

```yaml
peanut-butter:
  jwt:
    secret: "dev-secret-key-at-least-256-bits-long-for-development"
    access-token-expiry: "PT1H"
    refresh-token-expiry: "PT24H"  
    refresh-token-enabled: true
    refresh-token-rotation-enabled: false
    refresh-token-store-type: "IN_MEMORY"
```

#### Production Configuration

```yaml
peanut-butter:
  jwt:
    secret: "${JWT_SECRET_KEY}"  # Required environment variable
    access-token-expiry: "PT15M"
    refresh-token-expiry: "PT7D"
    refresh-token-enabled: true
    refresh-token-rotation-enabled: true
    refresh-token-mode: "STORE_AND_VALIDATE"
    refresh-token-store-type: "REDIS"
    used-refresh-token-handling: "BLACKLIST"
```

#### High-Security Configuration

```yaml
peanut-butter:
  jwt:
    secret: "${JWT_SECRET_KEY}"
    access-token-expiry: "PT5M"   # Very short lived
    refresh-token-expiry: "PT1H"  # Short lived refresh tokens
    refresh-token-enabled: true
    refresh-token-rotation-enabled: true
    refresh-token-mode: "STORE_AND_VALIDATE"
    refresh-token-store-type: "RDB"  # Persistent storage
    used-refresh-token-handling: "BLACKLIST"
```

### 7. Custom Refresh Token Store

#### Custom Store Implementation

```kotlin
@Component
class CustomRefreshTokenStore : RefreshTokenStore {
    
    private val tokenCache = ConcurrentHashMap<String, RefreshTokenRecord>()
    private val blacklist = ConcurrentHashMap<String, Instant>()
    
    override fun storeRefreshToken(tokenId: String, subject: String, expiration: Instant) {
        tokenCache[tokenId] = RefreshTokenRecord(tokenId, subject, expiration)
        logInfo("Stored refresh token for user: {}", subject)
    }
    
    override fun isRefreshTokenValid(tokenId: String): Boolean {
        // Check if token is blacklisted
        if (blacklist.containsKey(tokenId)) {
            return false
        }
        
        val record = tokenCache[tokenId] ?: return false
        return record.expiration.isAfter(Instant.now())
    }
    
    override fun removeRefreshToken(tokenId: String) {
        tokenCache.remove(tokenId)
        logInfo("Removed refresh token: {}", tokenId)
    }
    
    override fun blacklistRefreshToken(tokenId: String) {
        val record = tokenCache[tokenId]
        if (record != null) {
            blacklist[tokenId] = record.expiration
            logInfo("Blacklisted refresh token: {}", tokenId)
        }
    }
    
    override fun cleanupExpiredTokens() {
        val now = Instant.now()
        
        // Clean up expired tokens
        tokenCache.entries.removeIf { it.value.expiration.isBefore(now) }
        
        // Clean up expired blacklist entries  
        blacklist.entries.removeIf { it.value.isBefore(now) }
        
        logInfo("Cleaned up expired tokens")
    }
}
```

### 8. Integration Examples

#### Spring Security Integration

```kotlin
@Configuration
@EnableWebSecurity
class SecurityConfiguration(
    private val jwtService: JwtService
) {
    
    @Bean
    fun jwtAuthenticationFilter(): JwtAuthenticationFilter {
        return JwtAuthenticationFilter(jwtService)
    }
    
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/api/auth/**").permitAll()
                    .requestMatchers("/api/public/**").permitAll()
                    .anyRequest().authenticated()
            }
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter::class.java)
            .build()
    }
}

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService
) : OncePerRequestFilter() {
    
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = extractTokenFromRequest(request)
        
        if (token != null && jwtService.validateToken(token)) {
            val subject = jwtService.extractSubject(token)
            val claims = jwtService.extractClaims(token)
            
            val authorities = extractAuthorities(claims)
            val authentication = JwtAuthenticationToken(subject, authorities, claims)
            
            SecurityContextHolder.getContext().authentication = authentication
        }
        
        filterChain.doFilter(request, response)
    }
    
    private fun extractTokenFromRequest(request: HttpServletRequest): String? {
        val authHeader = request.getHeader("Authorization")
        return if (authHeader?.startsWith("Bearer ") == true) {
            authHeader.substring(7)
        } else null
    }
    
    private fun extractAuthorities(claims: Map<String, Any>?): Collection<GrantedAuthority> {
        val roles = claims?.get("roles") as? List<String> ?: emptyList()
        return roles.map { SimpleGrantedAuthority("ROLE_$it") }
    }
}
```

### 9. Testing JWT Configuration

#### Unit Tests

```kotlin
@ExtendWith(MockitoExtension::class)
class JwtServiceTest {
    
    private val properties = JwtProperties(
        secret = "test-secret-key-that-is-at-least-256-bits-long-for-testing",
        accessTokenExpiry = Duration.ofMinutes(15),
        refreshTokenExpiry = Duration.ofHours(24)
    )
    
    private val jwtService = DefaultJwtService(properties)
    
    @Test
    fun `should generate and validate access token`() {
        val subject = "test-user"
        val claims = mapOf("role" to "USER")
        
        val token = jwtService.generateAccessToken(subject, claims)
        
        assertThat(jwtService.validateToken(token)).isTrue()
        assertThat(jwtService.extractSubject(token)).isEqualTo(subject)
        assertThat(jwtService.extractClaims(token)?.get("role")).isEqualTo("USER")
    }
    
    @Test
    fun `should handle token expiration`() {
        val shortLivedProperties = properties.copy(
            accessTokenExpiry = Duration.ofMillis(1)
        )
        val shortLivedService = DefaultJwtService(shortLivedProperties)
        
        val token = shortLivedService.generateAccessToken("test-user")
        Thread.sleep(10)
        
        assertThat(shortLivedService.isTokenExpired(token)).isTrue()
        assertThat(shortLivedService.validateToken(token)).isFalse()
    }
}
```

#### Integration Tests

```kotlin
@SpringBootTest
@TestPropertySource(properties = [
    "peanut-butter.jwt.secret=test-secret-key-at-least-256-bits-long",
    "peanut-butter.jwt.refresh-token-enabled=true"
])
class JwtIntegrationTest {
    
    @Autowired
    private lateinit var jwtService: JwtService
    
    @Autowired 
    private lateinit var currentUserProvider: CurrentUserProvider<User>
    
    @Test
    fun `should configure JWT service correctly`() {
        assertThat(jwtService).isNotNull()
        
        val token = jwtService.generateAccessToken("test-user")
        assertThat(jwtService.validateToken(token)).isTrue()
    }
}
```

### 10. Best Practices

#### Security Best Practices

```kotlin
// ✅ Good: Use strong secrets (minimum 256 bits)
peanut-butter:
  jwt:
    secret: "${JWT_SECRET}" # Load from secure environment variable

// ✅ Good: Short-lived access tokens
peanut-butter:
  jwt:
    access-token-expiry: "PT15M"  # 15 minutes

// ✅ Good: Enable token rotation for high security
peanut-butter:
  jwt:
    refresh-token-rotation-enabled: true
    used-refresh-token-handling: "BLACKLIST"

// ❌ Avoid: Hardcoded secrets in configuration
peanut-butter:
  jwt:
    secret: "hardcoded-secret"  # Never do this!

// ❌ Avoid: Long-lived access tokens
peanut-butter:
  jwt:
    access-token-expiry: "PT24H"  # Too long for access tokens
```

#### Error Handling Best Practices

```kotlin
@RestControllerAdvice
class JwtExceptionHandler {
    
    @ExceptionHandler(SecurityException::class)
    fun handleSecurityException(ex: SecurityException): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(ErrorResponse("Authentication required"))
    }
    
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleInvalidToken(ex: IllegalArgumentException): ResponseEntity<ErrorResponse> {
        if (ex.message?.contains("JWT") == true) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED) 
                .body(ErrorResponse("Invalid token"))
        }
        return ResponseEntity.badRequest().body(ErrorResponse(ex.message))
    }
}
```

#### Performance Best Practices

```kotlin
// ✅ Good: Cache JWT service instance
@Component
class TokenValidator(private val jwtService: JwtService) {
    
    fun validateRequest(request: HttpServletRequest): ValidationResult {
        val token = extractToken(request)
        return if (token != null && jwtService.validateToken(token)) {
            ValidationResult.success(jwtService.extractSubject(token))
        } else {
            ValidationResult.failure("Invalid token")
        }
    }
}

// ✅ Good: Use conditional logging for debug information
fun processToken(token: String) {
    logDebugIf { "Processing token: ${token.take(10)}..." }
    
    val claims = jwtService.extractClaims(token)
    logInfoIf { "User authenticated: ${claims?.get("sub")}" }
}
```

### 11. Common Configuration Patterns

| Use Case | Configuration | Notes |
|----------|---------------|-------|
| **Development** | In-memory store, no rotation | Fast, simple setup |
| **Production API** | Redis store, rotation enabled | Scalable, secure |
| **High Security** | DB store, short tokens, rotation | Maximum security |
| **Mobile App** | Longer refresh tokens, rotation | Better UX for mobile |
| **Microservices** | Stateless validation | No storage needed |

### 12. Troubleshooting

#### Common Issues

```kotlin
// Issue: "Invalid JWT signature"
// Solution: Check secret key configuration
peanut-butter:
  jwt:
    secret: "${JWT_SECRET}"  # Ensure environment variable is set

// Issue: "Token expired" 
// Solution: Check token expiry configuration
peanut-butter:
  jwt:
    access-token-expiry: "PT30M"  # Increase if needed

// Issue: "Refresh token not found"
// Solution: Check refresh token store configuration
peanut-butter:
  jwt:
    refresh-token-mode: "STORE_AND_VALIDATE"
    refresh-token-store-type: "REDIS"  # Ensure Redis is configured
```

### 8. JWT Authentication Filter

**Dependencies required**: Spring Security + JJWT (for JWT filtering capabilities)

The JWT Authentication Filter provides automatic request-level JWT authentication with intelligent path exclusion and seamless Spring Security integration.

#### Enable JWT Filter

Add JWT filter configuration to your `application.yml`:

```yaml
peanut-butter:
  security:
    jwt:
      filter:
        enabled: true                          # Enable JWT filtering
        auto-detect-permit-all-paths: true     # Automatically exclude permitAll() paths
        excluded-paths:                        # Additional manual exclusions
          - "/api/public/**"
          - "/health/**" 
          - "/actuator/**"
          - "/swagger-ui/**"
          - "/v3/api-docs/**"
```

#### Automatic permitAll() Detection

The filter automatically analyzes your existing Spring Security configuration and excludes any paths configured with `permitAll()`:

```kotlin
@Configuration
@EnableWebSecurity
class SecurityConfig {
    
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .authorizeHttpRequests { auth ->
                // These paths will be automatically detected and excluded from JWT filtering
                auth.requestMatchers("/api/public/**").permitAll()
                auth.requestMatchers("/health", "/metrics").permitAll() 
                auth.requestMatchers("/login", "/register").permitAll()
                auth.anyRequest().authenticated()
            }
            .build()
    }
}
```

#### Manual Path Exclusion

Configure additional paths that should bypass JWT authentication:

```yaml
peanut-butter:
  security:
    jwt:
      filter:
        enabled: true
        excluded-paths:
          # Ant-style patterns supported
          - "/api/public/**"           # Exclude all public API endpoints
          - "/health/*"                # Exclude health check endpoints
          - "/admin/*/status"          # Exclude admin status endpoints
          - "/static/**"               # Exclude static resources
          - "/docs"                    # Exclude exact path
```

#### Combined Configuration Example

```yaml
peanut-butter:
  jwt:
    # JWT Service Configuration
    secret: "your-production-secret-key"
    access-token-expiry: "PT15M"
    refresh-token-enabled: true
    refresh-token-expiry: "PT24H"
    refresh-token-store-type: "REDIS"
    
  security:
    jwt:
      filter:
        # JWT Filter Configuration  
        enabled: true
        auto-detect-permit-all-paths: true  # Use both auto-detection
        excluded-paths:                     # AND manual exclusions
          - "/api/docs/**"
          - "/metrics/**"
```

#### Filter Behavior

The JWT filter operates as follows:

1. **Path Exclusion Check**: Requests matching excluded patterns bypass JWT authentication
2. **Token Extraction**: Extracts Bearer token from Authorization header (`Authorization: Bearer <token>`)
3. **Token Validation**: Validates token using the configured JWT service
4. **Authentication Setup**: Creates Spring Security authentication context with:
   - **Principal**: Token subject (usually user ID)
   - **Authorities**: Roles and permissions from token claims
5. **Error Handling**: Gracefully handles invalid tokens by continuing filter chain without authentication

#### Security Context Population

When a valid JWT token is found, the filter automatically populates the Spring Security context:

```kotlin
@RestController
class SecuredController {
    
    @GetMapping("/profile")
    fun getUserProfile(authentication: Authentication): UserProfile {
        val userId = authentication.principal as String
        val authorities = authentication.authorities.map { it.authority }
        
        // Token claims are available through JWT service
        return userService.getProfile(userId)
    }
    
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    fun getAdminData(): AdminData {
        // Role-based security works automatically
        return adminService.getData()
    }
}
```

#### Production Configuration

```yaml
peanut-butter:
  jwt:
    secret: "${JWT_SECRET_KEY}"
    access-token-expiry: "PT15M"
    refresh-token-enabled: true
    refresh-token-store-type: "REDIS"
    
  security:
    jwt:
      filter:
        enabled: true
        auto-detect-permit-all-paths: true
        excluded-paths:
          - "/actuator/health"
          - "/api/public/**"
          - "/metrics/**"

logging:
  level:
    # Enable debug logging for JWT filter
    com.github.snowykte0426.peanut.butter.security.jwt: DEBUG
```

#### Debug Logging

```yaml
logging:
  level:
    com.github.snowykte0426.peanut.butter.security.jwt: DEBUG
    io.jsonwebtoken: DEBUG
```

---

For more information, see the [main README](../README.md) or visit the [GitHub repository](https://github.com/snowykte0426/peanut-butter).
