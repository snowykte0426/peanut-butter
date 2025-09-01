# Release Notes

## v1.3.0

### Summary
**JWT Authentication Feature Release** – Adds comprehensive JSON Web Token (JWT) support with flexible refresh token management, multiple storage options, and configurable security policies. This major feature addition provides production-ready JWT authentication capabilities for Spring Boot applications.

### New Features
- **JWT Token Management**: Complete JWT token generation and validation (`JwtService`, `DefaultJwtService`)
  - Access token generation with custom claims support
  - Optional refresh token functionality with configurable expiration
  - Token validation and information extraction (subject, claims, expiration)
  - Secure HMAC-SHA256 signing with configurable secret keys
- **Flexible Refresh Token Storage**: Multiple storage backends for scalable refresh token management
  - **In-Memory Store** (`InMemoryRefreshTokenStore`): Default, zero-configuration option
  - **Redis Store** (`RedisRefreshTokenStore`): Distributed storage with automatic TTL management
  - **Database Store** (`JpaRefreshTokenStore`): Persistent storage with JPA entity support
- **Advanced Security Features**: Production-ready security policies
  - **Token Rotation**: Optional refresh token rotation with configurable used token handling
  - **Blacklisting Support**: Used refresh tokens can be removed or moved to blacklist
  - **Automatic Cleanup**: Scheduled cleanup of expired tokens across all storage types
- **Current User Context**: Seamless user information extraction from JWT tokens
  - **Current User Provider** (`CurrentUserProvider`, `JwtCurrentUserProvider`): Extract user info from request context
  - **User Resolver Interface** (`JwtUserResolver`): Custom user object transformation support
  - **Request Context Integration**: Automatic token extraction from Authorization headers
- **Comprehensive Configuration**: Property-driven configuration with secure defaults
  - **Application Properties**: Full configuration via `application.yml` under `peanut-butter.jwt.*`
  - **Programmatic Configuration**: `JwtConfiguration` for code-based setup
  - **Auto-Configuration**: Spring Boot auto-configuration with conditional bean creation

### Improvements
- **Zero-Configuration Setup**: Works out-of-the-box with secure defaults for development
- **Production-Ready Security**: Configurable secret keys, expiration times, and security policies
- **Modular Architecture**: Optional dependencies allow selective feature usage
- **Comprehensive Testing**: Full test coverage with unit and integration tests
- **Spring Boot Integration**: Seamless integration with Spring Security and Spring Boot ecosystem
- **Performance Optimized**: Efficient token operations with minimal overhead
- **Thread-Safe Operations**: Concurrent access support across all components

### Bug Fixes
- None (feature-focused release)

### Breaking Changes
- None (fully backward compatible with v1.2.x)

### Deprecated
- None

### Key Highlights
- **Production-grade JWT implementation** with JJWT library integration
- **Multiple storage backends** (In-Memory, Redis, Database) for different deployment scenarios
- **Advanced refresh token management** with rotation and blacklisting capabilities
- **Flexible user context resolution** with customizable user object mapping
- **Zero-configuration development** with production-ready security defaults
- **Comprehensive property configuration** for all JWT-related settings

### Requirements
- Java 17+
- SLF4J 2.0+
- (Optional) Kotlin 1.9+ for Kotlin extensions
- (Optional) Jakarta Bean Validation 3.0+ for validation features
- (Optional) Kotlin Coroutines 1.7.3+ for async logging
- (Optional) Spring Boot 3.1.x + Spring Security 6.3.x for CORS auto-configuration
- **New: JJWT 0.12.3+ for JWT features**
- **New: Spring Data JPA 3.1.x for database refresh token storage**
- **New: Spring Data Redis 3.1.x for Redis refresh token storage**

### Installation
```kotlin
dependencies {
    implementation("com.github.snowykte0426:peanut-butter:1.3.0")
    
    // For JWT features (required)
    implementation("io.jsonwebtoken:jjwt-api:0.12.3")
    implementation("io.jsonwebtoken:jjwt-impl:0.12.3")
    implementation("io.jsonwebtoken:jjwt-jackson:0.12.3")
    
    // For Redis refresh token storage (optional)
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    
    // For database refresh token storage (optional)
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
}
```
### Migration Guide
**From v1.2.x to v1.3.0**: Fully backward compatible. Simply update the version number.

1. **Update Version**: Change dependency to `1.3.0`
2. **Add JWT Dependencies**: Include JJWT dependencies for JWT functionality
3. **Configure JWT** (optional): Add JWT properties to `application.yml`:
   ```yaml
   peanut-butter:
     jwt:
       secret: "your-production-secret-key"
       access-token-expiry: "PT15M"
       refresh-token-enabled: true
   ```
4. **Choose Storage Backend**: Select appropriate refresh token storage (in-memory, Redis, or database)
5. **Implement User Resolver**: Create `JwtUserResolver` implementation for custom user object resolution

---
*See README.md and docs/USAGE.md for detailed examples and usage patterns.*

---

## v1.2.2

### Summary
Minor documentation & clarity update: explicit Spring Security module version guidance and dependency clarification. No code changes, safe drop‑in upgrade from v1.2.1.

### New Features
- N/A (no new features)

### Improvements
- **Documentation Clarity**: Added explicit Spring Security module versions (6.3.5) for CORS integration.
- **Dependency Guidance**: Clarified alternative between direct security modules vs spring-boot-starter-security.
- **Consistency**: Aligned docs with current build script compileOnly declarations.

### Bug Fixes
- N/A (no functional defects addressed)

### Breaking Changes
- None (fully backward compatible with v1.2.1)

### Deprecated
- None

### Key Highlights
- Clearer CORS dependency instructions (security-web & security-config vs starter).
- Purely documentation—no runtime or API impact.

### Requirements
- Java 17+
- SLF4J 2.0+
- (Optional) Kotlin 1.9+ for Kotlin extensions
- (Optional) Jakarta Bean Validation 3.0+ for validation features
- (Optional) Kotlin Coroutines 1.7.3+ for async logging
- (Optional) Spring Boot 3.1.x + Spring Security 6.3.x for CORS auto-configuration

### Installation
```kotlin
dependencies {
    implementation("com.github.snowykte0426:peanut-butter:1.2.2")
}
```

### Migration Guide
1. Update dependency to `1.2.2`.
2. No code changes required.
3. (Optional) Adjust your CORS dependencies per clarified guidance if needed.

---
*See README.md and docs/USAGE.md for detailed examples and usage patterns.*

---

## v1.2.1

### Summary
Quality & stability improvement release focusing on enhanced library reliability with comprehensive bug fixes and improved validation behavior.

### New Features
- N/A (maintenance release)

### Improvements
- **Enhanced Validation**: Improved edge case handling in field validation annotations
- **Better Error Handling**: More robust exception handling across all modules  
- **Stability Improvements**: Fixed various edge cases discovered during testing

### Bug Fixes
- **Validation Logic**: Fixed edge cases in `@FieldEquals` and `@FieldNotEquals` validation
- **File Validation**: Resolved null handling issues in `@NotEmptyFile` validator
- **Hexagonal Annotations**: Fixed annotation reflection behavior for architectural markers

### Breaking Changes
- None (fully backward compatible with v1.2.0)

### Deprecated
- None

### Key Highlights
- Enhanced stability and reliability across all modules
- Improved validation logic with better edge case handling
- Zero breaking changes ensuring seamless upgrade experience

### Requirements
- Java 17+
- SLF4J 2.0+
- (Optional) Kotlin 1.9+ for Kotlin extensions
- (Optional) Jakarta Bean Validation 3.0+ for validation features
- (Optional) Kotlin Coroutines 1.7.3+ for async logging
- (Optional) Spring Boot 3.1.x + Spring Security 6.2+ for CORS auto-configuration

### Installation
```kotlin
dependencies {
    implementation("com.github.snowykte0426:peanut-butter:1.2.1")
}
```

### Migration Guide
1. Update dependency version to `1.2.1`
2. Review breaking changes (none in this release)
3. Update deprecated usages (none in this release)

---
*See README.md and docs/USAGE.md for detailed examples and usage patterns.*

---

## v1.2.0

### Summary
**CORS Configuration Feature Release** – Adds comprehensive Cross-Origin Resource Sharing (CORS) auto-configuration capabilities to Peanut-Butter, providing seamless Spring Security integration with flexible property-based configuration for modern web applications and APIs.

### New Features
- **CORS Auto-Configuration**: Complete CORS setup with Spring Security integration (`CorsConfiguration`, `CorsProperties`)
- **Flexible Property Configuration**: Map-based HTTP method configuration with enable/disable granularity
  - `peanut-butter.security.cors.allowed-methods.GET: true|false`
  - Support for all standard HTTP methods with individual control
- **Security Integration**: Auto-configured `SecurityFilterChain` with CORS support (`CorsSecurityFilterChain`)
- **Production-Ready Defaults**: Secure defaults with comprehensive customization options
- **Multiple Configuration Patterns**: Support for development, production, and API-specific CORS policies

### Improvements
- **Property-Driven Configuration**: All CORS settings configurable via `application.yml` under `peanut-butter.security.cors.*`
- **Conditional Auto-Configuration**: CORS features only activate when Spring Security is present on classpath
- **Multiple Beans Support**: Provides both `CorsConfigurationSource` and `SpringCorsConfiguration` beans for flexibility
- **Comprehensive Documentation**: Complete usage guide with development, production, and API examples
- **Enhanced Feature Matrix**: Updated documentation to include CORS capability across all feature combinations

### Bug Fixes
- None (feature-focused release)

### Breaking Changes
- None (fully backward compatible with v1.1.x)

### Deprecated
- None

### Key Highlights
- Zero-configuration CORS for Spring Security applications
- Granular HTTP method control via property maps
- Multiple pre-configured security patterns (development, production, API-only)
- Seamless integration with existing Spring Security configurations
- Comprehensive test coverage with Kotest framework

### Requirements
- Java 17+
- SLF4J 2.0+
- (Optional) Kotlin 1.9+ for Kotlin extensions
- (Optional) Jakarta Bean Validation 3.0+ for validation features
- (Optional) Kotlin Coroutines 1.7.3+ for async logging
- (Optional) Spring Boot 3.1.x + Spring Security 6.2+ for CORS auto-configuration

### Installation
```kotlin
dependencies {
    implementation("com.github.snowykte0426:peanut-butter:1.2.0")
    
    // For CORS features
    implementation("org.springframework.boot:spring-boot-starter-security:3.1.5")
}
```

### Migration Guide
**From v1.1.4 to v1.2.0**: Fully backward compatible. Simply update the version number.

1. **Update Version**: Change dependency to `1.2.0`
2. **Optional CORS Setup**: Add Spring Security dependency to enable CORS auto-configuration
3. **Configure CORS** (optional): Add CORS properties to `application.yml`:
   ```yaml
   peanut-butter:
     security:
       cors:
         enabled: true
         allowed-origins: ["https://yourapp.com"]
         allowed-methods:
           GET: true
           POST: true
           PUT: false
   ```

---

## v1.1.4

### Summary
Namespace & build quality update consolidating all public APIs under a single package root and adding minor build/reporting enhancements. One small breaking change (import path update) – no behavioral or API surface changes.

### New Features
- **Utility Gradle Task**: `printVersion` task to output the current project version.

### Improvements
- **Unified Namespace**: All modules relocated to `com.github.snowykte0426.peanut.butter.*` for consistent import structure.
- **Jar Manifest Metadata**: Adds `Implementation-Title`, `Implementation-Version`, vendor info for provenance & tooling.
- **Enhanced Test Logging**: JUnit test task now reports passed / skipped / failed with full stack traces.
- **Explicit JitPack Repository**: Added to build script for clarity when mirroring configuration.

### Bug Fixes
- None (no functional defects addressed in this release).

### Breaking Changes
- **Package Rename**: Previous roots (e.g. `com.github.snowykte0426.logging`, `.timezone`, `.validation`, `.hexagonal.annotation`) replaced by `com.github.snowykte0426.peanut.butter.*`. Update imports accordingly. (See Migration Guide.)

### Deprecated
- None.

### Key Highlights
- Single, predictable package hierarchy.
- Zero behavioral change – pure structural & build transparency improvements.
- Faster diagnosis via enriched test logging.

### Requirements
- Java 17+
- SLF4J 2.0+
- (Optional) Kotlin 1.9+ for Kotlin extensions
- (Optional) Jakarta Bean Validation 3.0+ for validation features
- (Optional) Kotlin Coroutines 1.7.3+ for async logging
- (Optional) Spring Boot 3.1.x for auto-configuration / `@Adapter`

### Installation
```kotlin
dependencies {
    implementation("com.github.snowykte0426:peanut-butter:1.1.4")
}
```

### Migration Guide
1. **Update Version**: Change dependency to `1.1.4`.
2. **Update Imports**: Perform find & replace:
   - `com.github.snowykte0426.logging` → `com.github.snowykte0426.peanut.butter.logging`
   - `com.github.snowykte0426.timezone` → `com.github.snowykte0426.peanut.butter.timezone`
   - `com.github.snowykte0426.validation` → `com.github.snowykte0426.peanut.butter.validation`
   - `com.github.snowykte0426.hexagonal.annotation` → `com.github.snowykte0426.peanut.butter.hexagonal.annotation`
3. **Rebuild & Run Tests**: No code changes expected beyond imports.
4. (Optional) Leverage `./gradlew printVersion` in CI for verification.

---

## v1.1.3

### Summary
Architecture & Validation Enhancement Release – adds lightweight hexagonal architecture meta‑annotations (`@Port`, `@Adapter`, `PortDirection`) and a new file upload validation constraint (`@NotEmptyFile`). No breaking API changes.

### New Features
- **Hexagonal Architecture Annotations**:
  - `@Port(direction = INBOUND|OUTBOUND)` – source‑retained marker for domain port interfaces (kept lightweight; no runtime cost)
  - `@Adapter(direction = INBOUND|OUTBOUND)` – runtime component stereotype (meta‑annotated with `@Component`) for Spring adapters, improves clarity & scanning
  - `PortDirection` enum (INBOUND, OUTBOUND)
- **File Upload Validation**:
  - `@NotEmptyFile` – Jakarta Bean Validation constraint for `MultipartFile` fields ensuring a non‑null, non‑empty upload (`NotEmptyFileValidator`)

### Improvements
- Documentation updated (README / USAGE) to include new annotations & examples
- Project version bumped to 1.1.3; dependency guidance kept minimal (no added required deps)

### Bug Fixes
- N/A (no reported issues since 1.1.2)

### Breaking Changes
- None (fully backward compatible with 1.1.x)

### Deprecated
- None

### Migration Guide (1.1.2 → 1.1.3)
1. Update dependency version to `1.1.3`
2. (Optional) Start annotating adapters & ports for clearer architectural boundaries
3. (Optional) Apply `@NotEmptyFile` to multipart upload DTO fields

No code changes required; all existing APIs remain stable.

---

## v1.1.2

### Summary
**Spring Boot 3.x Compatibility Fix** - Resolved `@ConstructorBinding` annotation issue that caused application startup failures in Spring Boot 3.x environments.

### Bug Fixes
- **Fixed Spring Boot 3.x compatibility**: Removed `@ConstructorBinding` annotation from `TimeZoneProperties` class to resolve startup error: `com.github.snowykte0426.timezone.TimeZoneProperties declares @ConstructorBinding on a no-args constructor`
- **Updated configuration binding**: Changed to Spring Boot 3.x compatible property binding approach for data classes with default values

### Improvements
- **Enhanced Spring Boot compatibility**: Now fully compatible with Spring Boot 3.1.x - 3.5.x
- **Simplified property configuration**: Removed unnecessary annotations for cleaner code

### Breaking Changes
- None (fully backward compatible)

### Deprecated
- None

### Key Features
- All features from v1.1.1 remain unchanged
- **Zero forced dependencies** beyond SLF4J API and Kotlin stdlib
- **Modular design** allowing selective feature usage
- **Framework agnostic** - works with or without Spring Boot

### Requirements
- **Minimum**: Java 17+ and SLF4J 2.0+
- **For Kotlin features**: Kotlin 1.9+
- **For validation**: Jakarta Bean Validation 3.0+
- **For coroutine logging**: Kotlin Coroutines 1.7.3+
- **For Spring Boot integration**: Spring Boot 3.1.x - 3.5.x

### Installation
```kotlin
dependencies {
    implementation("com.github.snowykte0426:peanut-butter:1.1.2")
}
```

### Migration Guide
**From v1.1.1 to v1.1.2**: Simply update the version number. No code changes required.

```kotlin
// Before
implementation("com.github.snowykte0426:peanut-butter:1.1.1")

// After  
implementation("com.github.snowykte0426:peanut-butter:1.1.2")
```

---

## v1.1.1

### Summary
**Dependency Minimization Release** - Transformed Peanut-Butter into a truly lightweight, modular library with **zero forced dependencies**. Users now only receive essential dependencies and can selectively add optional features as needed.

### Major Improvements
- **Zero Forced Dependencies**: Reduced core dependencies to only SLF4J API + Kotlin stdlib (from ~20+ transitive dependencies)
- **Modular Architecture**: All features except basic logging are now optional with `compileOnly` dependencies
- **Selective Feature Usage**: Users can choose exactly which features to include in their projects
- **Enhanced Documentation**: Comprehensive dependency management guides and feature matrix

### Improvements
- **DEPENDENCY_GUIDE.md**: Complete guide for dependency management
- **Updated README.md**: Clear feature-to-dependency mapping with modular design explanation
- **Updated USAGE.md**: Feature availability matrix and setup guides for different configurations
- **Modular dependency structure**: Core dependencies separated from optional features

### Bug Fixes
- None (optimization-focused release)

### Breaking Changes
- None (fully backward compatible)

### Deprecated
- None

### Key Features
- **Zero forced dependencies** beyond SLF4J API and Kotlin stdlib
- **Modular design** allowing selective feature usage
- **90% dependency reduction** for minimal installations (~2MB vs 15-20MB)
- **Developer choice** in logging implementation selection
- **Framework agnostic** - works with or without Spring Boot

### Requirements
- **Minimum**: Java 17+ and SLF4J 2.0+
- **For Kotlin features**: Kotlin 1.9+
- **For validation**: Jakarta Bean Validation 3.0+
- **For coroutine logging**: Kotlin Coroutines 1.7.3+
- **For Spring Boot integration**: Spring Boot 3.1.x

### Installation
```kotlin
dependencies {
    implementation("com.github.snowykte0426:peanut-butter:1.1.1")
}
```

### Migration Guide
**No migration required** - this is a fully backward-compatible optimization:

1. Update version to `1.1.1`
2. **If you use validation features**, add Jakarta Validation API
3. **If you use coroutine logging**, add Kotlin Coroutines  
4. **If you use Spring Boot auto-configuration**, add Spring Boot Starter
5. **Add a logging implementation** (Logback, Log4j2, etc.) if not already present

---

## v1.1.0

### Summary
Added automatic TimeZone configuration & utility features enabling application-wide default timezone control, conversion utilities, temporary timezone context execution, and Spring Boot auto-configuration support.

### New Features
- **Automatic TimeZone Configuration**: Sets the JVM default timezone at startup (`TimeZoneAutoConfiguration`, `TimeZoneProperties`)
- **Configuration Annotation**: `@EnableAutomaticTimeZone` for explicit activation when desired
- **Programmatic Control**: Runtime timezone switching via `TimeZoneInitializer` (`changeTimeZone()`, `getCurrentTimeZone()`)
- **Supported Enum**: `SupportedTimeZone` (UTC, KST, JST, GMT, WET, BST, CET, WEST, CEST, EET, EEST, MST, PT, ET)
- **Extension Utilities**:
  - Current time retrieval: `Any.getCurrentTimeIn(...)`
  - Conversions: `LocalDateTime.inTimeZone(...)`, `ZonedDateTime.convertToTimeZone(...)`
  - Default timezone comparison: `Any.isCurrentTimeZone(...)`
  - Temporary context execution: `Any.withTimeZone(...)`
  - Display name lookup: `Any.getCurrentTimeZoneDisplayName()`

### Improvements
- Property controls: `peanut-butter.timezone.enabled`, `zone`, `enable-logging` for fine‑grained behavior & logging
- Clearer error signaling: Unsupported timezone triggers descriptive `IllegalArgumentException`
- Logging integration: Successful initialization/change uses `logInfo`, failures use `logWarn`

### Bug Fixes
- None (feature-focused release)

### Breaking Changes
- None (no changes to existing public APIs)

### Deprecated
- None

### Key Features
- Global default TimeZone auto setup & runtime switching
- Safe temporary TimeZone context (`withTimeZone`)
- Enum-backed validation & standardized timezone identifiers
- Built-in observability through existing logging utilities

### Requirements
- Java 17+
- Kotlin 1.9+
- Spring Boot 3.1.x (when using auto-configuration)

### Installation
```kotlin
dependencies {
    implementation("com.github.snowykte0426:peanut-butter:1.1.0")
}
```

### Migration Guide
1. Bump dependency version 1.0.2 → 1.1.0
2. (Optional) Add application.yml configuration:
   ```yaml
   peanut-butter:
     timezone:
       enabled: true
       zone: KST
       enable-logging: true
   ```
3. Inject `TimeZoneInitializer` for runtime changes (e.g. `changeTimeZone("UTC")`)
4. Replace ad‑hoc timezone handling with `withTimeZone(...)` where appropriate

---

## v1.0.2

### Summary
Added **coroutine-aware logging** capabilities to the peanut-butter library. This release introduces comprehensive async logging support for Kotlin coroutines, enabling thread-safe logging with advanced features like correlation ID tracking, retry mechanisms, and parallel execution monitoring.

### New Features
- **Coroutine Logging**: Complete async logging support for Kotlin coroutines
  - **Async Logging**: `logInfoAsync()`, `logDebugAsync()`, `logErrorAsync()` etc. - Thread-safe logging in coroutines
  - **Async Performance**: `logExecutionTimeAsync()`, `logMethodExecutionAsync()` - Performance tracking for suspend functions
  - **Retry Mechanism**: `retryWithLogging()` - Exponential backoff with comprehensive logging
  - **Correlation Tracking**: `withLoggingContext()` - Enhanced logging with correlation ID for distributed tracing
  - **Parallel Execution**: `executeParallelWithLogging()` - Monitor parallel operations with individual timing
  - **Context Logging**: `logCoroutineContext()` - Debug coroutine context information

### Improvements
- **Coroutines Support**: Added Kotlin Coroutines 1.7.3 dependency for async features
- **MDC Support**: Mapped Diagnostic Context integration for correlation ID tracking
- **Enhanced Testing**: Added kotlinx-coroutines-test for comprehensive coroutine testing

### Bug Fixes
None - this is a feature expansion release

### Breaking Changes
None - fully backward compatible with v1.0.1

### Deprecated
None

### Key Features
- **🚀 Production-ready coroutine logging** with thread safety and context preservation
- **🔍 Distributed tracing support** with correlation ID tracking
- **⚡ Intelligent retry mechanisms** with exponential backoff and detailed logging
- **📊 Parallel operation monitoring** with individual timing and error tracking

### Requirements
- Java 17+
- Kotlin 1.9+ (for Kotlin features)
- SLF4J 2.0+ (for logging features)
- Jakarta Bean Validation API 3.0+ (for validation features)
- **Kotlin Coroutines 1.7.3+** (for coroutine logging features)

### Installation
```kotlin
dependencies {
    implementation("com.github.snowykte0426:peanut-butter:1.0.2")
}
```

### Migration Guide
No migration needed from v1.0.1 - simply update the version number. All existing code will continue to work without changes.

1. Update dependency version to 1.0.2
2. Start using new coroutine logging features in async code
3. Consider migrating existing async logging to new coroutine-aware methods (`logInfoAsync()` instead of `logInfo()` in suspend functions)

### Usage Examples

#### Basic Coroutine Logging
```kotlin
suspend fun processUser(userData: UserData) {
    logInfoAsync("Processing user: {}", userData.username)
    
    val result = logExecutionTimeAsync("User processing") {
        userService.processAsync(userData)
    }
}
```

#### Resilient Operations with Retry
```kotlin
suspend fun callExternalAPI(): APIResponse {
    return retryWithLogging(
        operation = "External API call",
        maxAttempts = 3,
        initialDelay = 1000L
    ) {
        externalAPIClient.call()
    }
}
```

#### Distributed Tracing with Correlation ID
```kotlin
suspend fun handleRequest(requestId: String) {
    withLoggingContext("req-$requestId") {
        logInfoAsync("Starting request processing")
        // All logs in this block will include the correlation ID
        processRequest()
    }
}
```

---

## v1.0.1

### Summary
Major feature expansion adding comprehensive Kotlin support and logging utilities to the peanut-butter library. This release transforms the library from a Java-only validation tool into a full-featured utility library supporting both Java and Kotlin development.

### New Features
- **Kotlin Support**: Added full Kotlin compatibility with JVM toolchain 17
- **Logger Extensions**: Simple logger creation with `logger()` extension for any class
- **Convenient Logging**: Direct logging methods - `logInfo()`, `logDebug()`, `logError()`, `logWarn()`, `logTrace()`
- **Performance Logging**: Execution time tracking with `logExecutionTime()` and `logMethodExecution()`
- **Exception Handling**: Safe execution with `logOnException()` and `logWarningOnException()`
- **Conditional Logging**: Performance-optimized logging with `logDebugIf{}`, `logInfoIf{}`, etc.
- **Advanced Logger Types**: Lazy initialization with `lazyLogger()` and companion object support with `companionLogger()`
- **Memory Monitoring**: Performance tracking including memory usage with `logPerformance()`

### Improvements
- **SLF4J Integration**: Added SLF4J 2.0.9 API as core logging foundation
- **Flexible Logging Backend**: Logback 1.5.13 as default but easily replaceable with Log4j2, JUL, etc.
- **Enhanced Testing**: Added Kotest framework for comprehensive Kotlin testing
- **Better Documentation**: Updated README with clear examples and comprehensive USAGE.md guide

### Bug Fixes
- Minor fixes in validation logic

### Breaking Changes
None - fully backward compatible with v1.0.0. All existing Java validation features remain unchanged.

### Deprecated
None

### Key Features
- Zero-configuration logging setup with sensible defaults
- SLF4J compatibility allows switching between logging frameworks
- Performance-first design with conditional logging to avoid expensive operations
- Natural Kotlin syntax with extension functions
- Comprehensive exception handling with automatic logging

### Requirements
- Java 17+
- Kotlin 1.9+ (for Kotlin features)
- SLF4J 2.0+ (for logging features)
- Jakarta Bean Validation API 3.0+ (for validation features)

### Installation
```kotlin
dependencies {
    implementation("com.github.snowykte0426:peanut-butter:1.0.1")
}
```

### Migration Guide
No migration needed from v1.0.0 - simply update the version number. All existing code will continue to work without changes.

1. Update dependency version to 1.0.1
2. Optionally start using new Kotlin logging features
3. No code changes required for existing validation functionality

---

## v1.0.0

### Summary
Initial release of Peanut-Butter, a comprehensive utility library for Java development.

### Field Validation Annotations
- **@FieldEquals**: Validates that specified fields have equal values
- **@FieldNotEquals**: Validates that specified fields have different values

### Key Features
- Class-level validation annotations
- Jakarta Bean Validation compatibility
- Custom error messages
- Inheritance support
- Null-safe validation

### Requirements
- Java 17+
- Jakarta Bean Validation API 3.0+

### Installation
```kotlin
dependencies {
    implementation("com.github.snowykte0426:peanut-butter:1.0.0")
}
```

---

*For detailed usage examples and documentation, please refer to the README.md and USAGE.md*