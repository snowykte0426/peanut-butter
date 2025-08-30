# Release Notes

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
- **🔥 Coroutine Logging**: Complete async logging support for Kotlin coroutines
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