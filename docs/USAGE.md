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
- [CORS Configuration](#cors-configuration)
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

#### For CORS Configuration
```kotlin
dependencies {
    // Add CORS support (requires Spring Boot + Spring Security)
    implementation("org.springframework.boot:spring-boot-starter:3.1.5")
    implementation("org.springframework.boot:spring-boot-starter-security:3.1.5")
}
```

### Feature Availability Matrix

| Feature | Core Only | + Validation | + Multipart | + Coroutines | + Spring Boot | + CORS |
|---------|-----------|--------------|-------------|--------------|---------------|---------|
| Basic logging | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| Performance timing | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| Timezone utilities | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `@FieldEquals` / `@FieldNotEquals` | ❌ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `@NotEmptyFile` | ❌ | ❌ | ✅ | ✅ | ✅ | ✅ |
| Async logging (`logInfoAsync`) | ❌ | ❌ | ❌ | ✅ | ✅ | ✅ |
| Auto timezone configuration | ❌ | ❌ | ❌ | ❌ | ✅ | ✅ |
| CORS auto-configuration | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ |
| Hexagonal annotations | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |

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

**Dependencies required**: Spring Boot + Spring Security

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

---

For more information, see the [main README](../README.md) or visit the [GitHub repository](https://github.com/snowykte0426/peanut-butter).
