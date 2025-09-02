# Peanut-Butter 사용 가이드 🥜🧈

**Language**: [English](../USAGE.md)

이 가이드는 Peanut-Butter 라이브러리 사용을 위한 포괄적인 예제와 모범 사례를 제공합니다.

## 목차

- [의존성 관리](#의존성-관리)
- [필드 검증](#필드-검증)
- [파일 업로드 검증](#파일-업로드-검증)
- [헥사고날 아키텍처 어노테이션](#헥사고날-아키텍처-어노테이션)
- [로깅 확장](#로깅-확장)
- [코루틴 로깅](#코루틴-로깅)
- [타임존 유틸리티](#타임존-유틸리티)
- [CORS 구성](#cors-구성)
- [JWT 인증](#jwt-인증)
- [JWT 인증 필터](#jwt-인증-필터)
- [모범 사례](#모범-사례)
- [고급 예제](#고급-예제)

## 의존성 관리

Peanut-Butter는 **경량화 및 모듈형**으로 설계되었습니다. 실제로 사용하는 기능에 대한 의존성만 포함하면 됩니다.

### 핵심 설치 (최소)

```kotlin
dependencies {
    implementation("com.github.snowykte0426:peanut-butter:1.3.1")
    
    // 로깅 구현체 선택 (모든 로깅 기능에 필요)
    implementation("ch.qos.logback:logback-classic:1.5.13")
    // 또는 implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.21.1")
}
```

이것으로 다음을 사용할 수 있습니다:
- ✅ 기본 로깅 확장 (`logger()`, `logInfo()` 등)
- ✅ 성능 타이밍 유틸리티
- ✅ 핵심 타임존 열거형과 유틸리티
- ✅ 헥사고날 어노테이션

### 필요에 따라 기능 추가

#### 검증 어노테이션용
```kotlin
dependencies {
    // 검증 지원 추가
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")
    implementation("org.hibernate.validator:hibernate-validator:8.0.1.Final")
    implementation("org.glassfish:jakarta.el:4.0.2") // 표현식 평가용
}
```

#### 파일 업로드 제약 조건 (`@NotEmptyFile`)용
```kotlin
dependencies {
    // 파일 업로드 검증 지원 추가
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")
    implementation("org.hibernate.validator:hibernate-validator:8.0.1.Final")
    implementation("org.springframework:spring-web:6.2.8") // MultipartFile
}
```

#### 코루틴 로깅용
```kotlin
dependencies {
    // 코루틴 지원 추가
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
}
```

#### Spring Boot 자동 구성용
```kotlin
dependencies {
    // Spring Boot 통합 추가
    implementation("org.springframework.boot:spring-boot-starter:3.1.5")
}
```

#### CORS 구성용
```kotlin
dependencies {
    // 옵션 1: Spring Security 모듈을 직접 사용 (라이브러리 compileOnly 버전과 매치)
    implementation("org.springframework.security:spring-security-web:6.3.5")
    implementation("org.springframework.security:spring-security-config:6.3.5")
    implementation("org.springframework.boot:spring-boot-starter:3.1.5") // Spring Boot 자동 구성도 필요한 경우
    
    // 옵션 2: 더 간단함 (매치되는 security 모듈을 전이적으로 가져옴)
    // implementation("org.springframework.boot:spring-boot-starter-security:3.1.5")
}
```

#### JWT 인증용
```kotlin
dependencies {
    // JWT 지원 (필수)
    implementation("io.jsonwebtoken:jjwt-api:0.12.3")
    implementation("io.jsonwebtoken:jjwt-impl:0.12.3")
    implementation("io.jsonwebtoken:jjwt-jackson:0.12.3")
    
    // 서블릿 컨텍스트 지원용 (현재 사용자 프로바이더에 필요)
    implementation("jakarta.servlet:jakarta.servlet-api:6.0.0")
    
    // Redis 리프레시 토큰 스토리지용 (선택사항)
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    
    // 데이터베이스 리프레시 토큰 스토리지용 (선택사항)  
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    
    // Spring Boot 통합용
    implementation("org.springframework.boot:spring-boot-starter:3.1.5")
}
```

### 기능 가용성 매트릭스

| 기능 | 핵심만 | + 검증 | + 멀티파트 | + 코루틴 | + Spring Boot | + CORS | + JWT |
|------|---------|---------|------------|----------|---------------|--------|-------|
| 기본 로깅 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| 성능 타이밍 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| 타임존 유틸리티 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `@FieldEquals` / `@FieldNotEquals` | ❌ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `@NotEmptyFile` | ❌ | ❌ | ✅ | ✅ | ✅ | ✅ | ✅ |
| 비동기 로깅 (`logInfoAsync`) | ❌ | ❌ | ❌ | ✅ | ✅ | ✅ | ✅ |
| 자동 타임존 구성 | ❌ | ❌ | ❌ | ❌ | ✅ | ✅ | ✅ |
| CORS 자동 구성 | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ | ✅ |
| JWT 토큰 관리 | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ |
| 리프레시 토큰 스토리지 | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ |
| 현재 사용자 프로바이더 | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ |
| JWT 인증 필터 | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ |
| 헥사고날 어노테이션 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |

## 필드 검증

**필요한 의존성**: Jakarta Validation API + 구현체

### @FieldEquals 어노테이션

지정된 필드들이 같은 값을 갖는지 검증합니다.

```java
@FieldEquals(fields = {"password", "passwordConfirm"})
public class UserRegistrationForm {
    private String username;
    private String password;
    private String passwordConfirm;
    
    // 생성자, getter, setter...
}
```

#### 다중 필드 동등성 검사

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

#### 커스텀 오류 메시지

```java
@FieldEquals(
    fields = {"password", "passwordConfirm"}, 
    message = "비밀번호와 확인 비밀번호가 일치하지 않습니다."
)
public class UserForm {
    private String password;
    private String passwordConfirm;
}
```

### @FieldNotEquals 어노테이션

지정된 필드들이 다른 값을 갖는지 검증합니다.

```java
@FieldNotEquals(fields = {"username", "password"})
public class UserForm {
    private String username;
    private String password;

    // 생성자, getter, setter...
}
```

#### 보안 예제

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

## 파일 업로드 검증

`@NotEmptyFile`은 `MultipartFile` 필드가 존재하고 비어있지 않음을 보장합니다.

```java
public class UploadRequest {
    @NotEmptyFile(message = "이미지 파일이 필요합니다")
    private MultipartFile image;
}
```

동작:
- 필드가 null이 아니고 `!file.isEmpty()`일 때 유효
- null이거나 `file.isEmpty()`일 때 무효
- Jakarta Bean Validation과 함께 작동. Spring Web의 `MultipartFile` 타입 필요.

## 헥사고날 아키텍처 어노테이션

경계를 명시적으로 만들기 위한 경량 의미적 마커입니다.

```java
@Port(direction = PortDirection.INBOUND)
public interface UserQueryPort { UserView find(String id); }

@Adapter(direction = PortDirection.INBOUND)
public class UserQueryRestAdapter implements UserQueryPort {
    public UserView find(String id) { /* ... */ return null; }
}
```

가이드라인:
- 도메인으로 들어오는 외부 요청을 받는 인터페이스/어댑터 진입점(REST, 메시징)에는 `INBOUND` 사용
- 도메인이 외부 시스템(DB, 다른 서비스)에 도달하기 위해 사용하는 인터페이스에는 `OUTBOUND` 사용
- `@Port`는 SOURCE retention (런타임 오버헤드 없음). `@Adapter`는 런타임이며 `@Component`로 메타 어노테이션되어 Spring이 발견할 수 있음.

이점:
- 포트를 위한 프레임워크 의존성을 도입하지 않고 구조적 명확성
- 향후 정적 분석 가능 (어노테이션 프로세서 또는 커스텀 빌드 체크)

## 로깅 확장

### 기본 로거 생성

```kotlin
class UserService {
    // 간단한 로거 생성
    private val logger = logger()
    
    // 지연 로거 (성능 최적화)
    private val lazyLogger by lazyLogger()
    
    // 커스텀 로거 이름
    private val customLogger = logger("UserServiceCustom")
}
```

### 컴패니언 객체 로깅

```kotlin
class UserService {
    companion object {
        private val logger = companionLogger()
        
        fun staticMethod() {
            logger.info("정적 메서드 호출됨")
        }
    }
}
```

### 편리한 로깅 메서드

```kotlin
class UserService {
    fun createUser(userData: UserData) {
        // 기본 로깅
        logInfo("사용자 생성 중: {}", userData.username)
        logDebug("사용자 데이터: {}", userData)
        logWarn("경고: 사용자 생성 시도됨")
        logError("오류 발생", RuntimeException("테스트"))
        
        // 조건부 로깅 (성능 최적화)
        logDebugIf { "비용이 많이 드는 디버그 정보: ${generateExpensiveDebugInfo()}" }
        logInfoIf { "조건부 정보: ${someExpensiveOperation()}" }
    }
}
```

### 성능 로깅

```kotlin
class UserService {
    fun createUser(userData: UserData) {
        // 간단한 실행 시간 로깅
        val result = logExecutionTime("사용자 생성") {
            userRepository.save(userData)
        }
        
        // 메서드 실행 추적
        val validatedUser = logMethodExecution(
            "validateUser",
            mapOf("userId" to userData.id, "type" to userData.type)
        ) {
            userValidator.validate(userData)
        }
        
        // 메모리 사용량을 포함한 성능 로깅
        val processedData = logPerformance("데이터 처리") {
            dataProcessor.process(userData)
        }
    }
}
```

### 예외 처리와 로깅

```kotlin
class UserService {
    fun createUser(userData: UserData): User? {
        // 예외 로그하고 실패 시 null 반환
        return logOnException("사용자 생성") {
            userRepository.save(userData)
        }
    }
    
    fun createUserWithDefault(userData: UserData): User {
        // 경고 로그하고 실패 시 기본값 반환
        return logWarningOnException("사용자 생성", User.defaultUser()) {
            userRepository.save(userData)
        }
    }
}
```

## 코루틴 로깅

### 기본 비동기 로깅

```kotlin
class AsyncUserService {
    suspend fun createUser(userData: UserData) {
        // 비동기 안전 로깅
        logInfoAsync("사용자 생성 시작: {}", userData.username)
        logDebugAsync("사용자 데이터: {}", userData)
        logErrorAsync("오류 발생", RuntimeException("비동기 오류"))
        
        // 모든 로깅 작업은 코루틴 컨텍스트에서 스레드 안전
        repeat(10) {
            launch {
                logInfoAsync("동시 로깅: {}", it)
            }
        }
    }
}
```

### 비동기 성능 로깅

```kotlin
class AsyncUserService {
    suspend fun createUser(userData: UserData) {
        // suspend 함수의 실행 시간 측정
        val result = logExecutionTimeAsync("비동기 사용자 생성") {
            delay(100) // 비동기 작업 시뮬레이션
            userRepository.saveAsync(userData)
        }
        
        // 코루틴에서 메서드 실행 추적
        val validatedUser = logMethodExecutionAsync(
            "validateUserAsync",
            mapOf("userId" to userData.id)
        ) {
            validationService.validateAsync(userData)
        }
    }
}
```

### 코루틴에서 예외 처리

```kotlin
class AsyncUserService {
    suspend fun createUser(userData: UserData): User? {
        // 코루틴 컨텍스트에서 예외 처리
        return logOnExceptionAsync("비동기 사용자 생성") {
            userRepository.saveAsync(userData)
        }
    }
}
```

### 지수 백오프를 포함한 재시도

```kotlin
class AsyncUserService {
    suspend fun callExternalAPI(request: APIRequest): APIResponse {
        return retryWithLogging(
            operation = "외부 API 호출",
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

### 상관관계 ID 추적

```kotlin
class AsyncUserService {
    suspend fun processUserOperation(userId: String) {
        // 이 블록 내의 모든 로그는 상관관계 ID를 포함
        withLoggingContext("user-op-${UUID.randomUUID()}") {
            logInfoAsync("사용자 작업 시작")
            
            val user = fetchUserAsync(userId)
            val result = processUserAsync(user)
            
            logInfoAsync("사용자 작업 완료")
        }
    }
    
    suspend fun processWithCustomCorrelationId(correlationId: String) {
        withLoggingContext(correlationId) {
            logInfoAsync("커스텀 상관관계 ID로 처리")
            // 비즈니스 로직
        }
    }
}
```

### 로깅을 포함한 병렬 실행

```kotlin
class AsyncUserService {
    suspend fun processMultipleUsers(users: List<UserData>) {
        val results = executeParallelWithLogging {
            users.map { user ->
                suspend {
                    logInfoAsync("사용자 처리 중: {}", user.id)
                    processUserAsync(user)
                }
            }
        }
        
        logInfoAsync("{}명의 사용자를 처리했습니다", results.size)
    }
}
```

### 코루틴 컨텍스트 로깅

```kotlin
class AsyncUserService {
    suspend fun debugCoroutineContext() {
        // 현재 코루틴 컨텍스트 정보 로그
        logCoroutineContext()
        
        withContext(Dispatchers.IO) {
            logCoroutineContext() // 다른 디스패처 표시됨
        }
    }
}
```

## 모범 사례

### 1. 로거 생성

```kotlin
// ✅ 좋음: 더 나은 성능을 위해 지연 로거 사용
class UserService {
    private val logger by lazyLogger()
}

// ✅ 좋음: 정적 접근을 위해 컴패니언 객체 사용
class UserService {
    companion object {
        private val logger = companionLogger()
    }
}

// ❌ 피하기: 모든 메서드에서 로거 생성
class UserService {
    fun someMethod() {
        val logger = logger() // 매번 새 로거 생성
    }
}
```

### 2. 조건부 로깅

```kotlin
// ✅ 좋음: 비용이 많이 드는 작업에 조건부 로깅 사용
logDebugIf { "비용이 많이 드는 디버그 정보: ${generateExpensiveDebugInfo()}" }

// ❌ 피하기: 항상 비용이 많이 드는 디버그 정보 생성
logDebug("비용이 많이 드는 디버그 정보: ${generateExpensiveDebugInfo()}")
```

### 3. 코루틴 로깅

```kotlin
// ✅ 좋음: suspend 함수에서 비동기 로깅 사용
suspend fun createUser() {
    logInfoAsync("사용자 생성 중")
}

// ❌ 피하기: suspend 함수에서 동기 로깅 사용
suspend fun createUser() {
    logInfo("사용자 생성 중") // 현재 스레드를 블록
}
```

### 4. 예외 처리

```kotlin
// ✅ 좋음: 로깅 예외 핸들러 사용
val result = logOnException("위험한 작업") {
    riskyOperation()
}

// ✅ 좋음: 폴백 값 제공
val result = logWarningOnException("작업", defaultValue) {
    riskyOperation()
}
```

## 타임존 유틸리티

애플리케이션 차원의 타임존 구성 및 작업을 위한 강력한 유틸리티입니다. Spring Boot 자동 구성, 런타임 스위칭, 편리한 변환/검사 헬퍼를 포함합니다.

### 1. 자동 구성 (Spring Boot)

`application.yml`에 속성을 추가하세요 (자동 구성이 비활성화되지 않는 한 기본적으로 활성):

```yaml
peanut-butter:
  timezone:
    enabled: true          # 자동 구성 활성화/비활성화 (기본 true)
    zone: UTC              # 대상 타임존 (기본 UTC)
    enable-logging: true   # 초기화/실패 시 로그 (기본 true)
```

시작 시 라이브러리는 `user.timezone`을 설정하고 `TimeZone.setDefault(...)`를 호출합니다.

### 2. 지원되는 타임존

`SupportedTimeZone` 열거형:
```
UTC, KST, JST, GMT, WET, BST, CET, WEST, CEST, EET, EEST, MST, PT, ET
```
매칭은 대소문자를 구분하지 않으며 기본 Zone ID(예: `Asia/Seoul`)도 허용합니다.

### 3. 어노테이션을 통한 활성화

```kotlin
@SpringBootApplication
@EnableAutomaticTimeZone
class Application
```

(클래스패스 스캔과 속성이 충분하다면 어노테이션은 선택사항입니다.)

### 4. 프로그래매틱 제어

런타임에 전환하려면 `TimeZoneInitializer` 빈(Spring이 주입)을 사용하세요:

```kotlin
@Service
class TimeZoneService(private val initializer: TimeZoneInitializer) {
    fun switchToKst() = initializer.changeTimeZone("KST")
    fun switchToUtc() = initializer.changeTimeZone(SupportedTimeZone.UTC)
    fun current(): TimeZone = initializer.getCurrentTimeZone()
    fun supported(): List<SupportedTimeZone> = initializer.getSupportedTimeZones()
}
```

### 5. 확장 함수 개요

| 목적 | 함수 | 예제 |
|------|------|------|
| 특정 타임존의 현재 시간 | `Any.getCurrentTimeIn(zone)` | `getCurrentTimeIn(SupportedTimeZone.UTC)` |
| 문자열로 현재 시간 | `Any.getCurrentTimeIn("KST")` | `getCurrentTimeIn("KST")` |
| `LocalDateTime` -> `ZonedDateTime` 변환 | `LocalDateTime.inTimeZone(zone)` | `now.inTimeZone("ET")` |
| 타임존 간 변환 | `ZonedDateTime.convertToTimeZone(zone)` | `zdt.convertToTimeZone("PT")` |
| 현재 기본값 확인 | `Any.isCurrentTimeZone(zone)` | `isCurrentTimeZone("UTC")` |
| 표시 이름 가져오기 | `Any.getCurrentTimeZoneDisplayName()` | `getCurrentTimeZoneDisplayName()` |
| 임시 타임존 컨텍스트 | `Any.withTimeZone(zone) {}` | `withTimeZone("UTC") { runJob() }` |

### 6. 사용 예제

#### 여러 타임존에서 현재 시간 가져오기
```kotlin
val utcNow = getCurrentTimeIn(SupportedTimeZone.UTC)
val seoulNow = getCurrentTimeIn("KST")
```

#### LocalDateTime 변환
```kotlin
val createdAt: LocalDateTime = LocalDateTime.now()
val createdInEt = createdAt.inTimeZone("ET")
```

#### ZonedDateTime 변환
```kotlin
val utcZdt = ZonedDateTime.now(ZoneId.of("UTC"))
val inPacific = utcZdt.convertToTimeZone(SupportedTimeZone.PT)
```

#### 임시 타임존 컨텍스트
```kotlin
withTimeZone("UTC") {
    // 내부의 모든 날짜/시간 API는 임시 기본값을 봄
    generateDailyReport()
}
```

#### 로깅과 함께 안전한 런타임 스위치
```kotlin
try {
    initializer.changeTimeZone("KST")
} catch (ex: IllegalArgumentException) {
    logWarn("지원되지 않는 타임존 요청: {}", ex.message)
}
```

### 7. 오류 처리

유효하지 않은 타임존 문자열은 `IllegalArgumentException`을 던집니다:
```kotlin
val t = runCatching { getCurrentTimeIn("NOT_A_ZONE") }
if (t.isFailure) logWarn("유효하지 않은 타임존 입력")
```

### 8. 모범 사례

- 컴파일 타임 안전성을 위해 원시 문자열보다 열거형 상수 선호
- 런타임 스위치는 드물게 유지; 빈번한 스위치는 예약된 작업을 혼란스럽게 할 수 있음
- 수동으로 기본값을 복원하는 대신 `withTimeZone`으로 중요한 비즈니스 로직을 감쌈
- 기본 타임존에 대한 모든 관리적 변경 사항을 로그
- 입력을 우아하게 검증하려면 `SupportedTimeZone.fromString(value)` 사용

### 9. v1.1.0으로 마이그레이션

<= 1.0.2에서 업그레이드하는 경우:
1. 의존성 버전을 `1.1.0`으로 변경
2. (선택사항) `application.yml`에 `peanut-butter.timezone` 섹션 추가
3. 명시적 옵트인을 원한다면 `@EnableAutomaticTimeZone` 사용
4. 임시 `TimeZone.setDefault(...)` 호출을 `TimeZoneInitializer` 또는 `withTimeZone`으로 교체

## CORS 구성

**필요한 의존성**: Spring Boot + Spring Security (web + config 모듈 또는 Boot starter security)

Peanut-Butter는 Spring Boot 자동 구성 지원과 함께 포괄적인 CORS (Cross-Origin Resource Sharing) 구성을 제공합니다. CORS 모듈은 애플리케이션 속성을 기반으로 CORS 정책을 자동으로 구성하고 Spring Security와 완벽하게 통합됩니다.

### 1. 기본 설정과 구성

Spring Security가 클래스패스에 있을 때 CORS가 자동으로 활성화됩니다. 모듈은 완전한 커스터마이징을 허용하면서 합리적인 기본값을 제공합니다.

#### 자동 구성 (Spring Boot)

`application.yml`에 CORS 속성을 추가하세요:

```yaml
peanut-butter:
  security:
    cors:
      enabled: true                    # CORS 활성화/비활성화 (기본: true)
      allowed-origins: ["*"]           # 허용된 출처 (기본: ["*"])
      allowed-headers: ["*"]           # 허용된 헤더 (기본: ["*"])
      allow-credentials: true          # 자격 증명 허용 (기본: true)
      max-age: 3600                    # 프리플라이트 캐시 지속시간(초) (기본: 3600)
      exposed-headers: []              # 클라이언트에 노출된 헤더 (기본: 빈 값)
      allowed-methods:                 # HTTP 메서드 구성 (기본: TRACE 제외 모두)
        GET: true
        POST: true
        PUT: true
        DELETE: true
        PATCH: true
        HEAD: true
        OPTIONS: true
        TRACE: false
```

### 2. 속성 구성 예제

#### 개발 구성
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

#### 프로덕션 구성
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
        DELETE: false          # 프로덕션에서 DELETE 비활성화
        PATCH: true
        HEAD: true
        OPTIONS: true
        TRACE: false
      allow-credentials: true
      max-age: 86400            # 24시간
      exposed-headers:
        - "X-Total-Count"
        - "X-Page-Count"
        - "Link"
```

#### API 전용 구성
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

## JWT 인증

**필요한 의존성**: JJWT + Spring Boot (현재 사용자 프로바이더를 위한 선택적 서블릿 컨텍스트)

Peanut-Butter는 유연한 리프레시 토큰 관리, 다중 스토리지 백엔드, 프로덕션 준비 보안 기능과 함께 포괄적인 JWT (JSON Web Token) 인증을 제공합니다.

### 1. 기본 설정과 구성

JJWT 의존성이 클래스패스에 있을 때 JWT 기능이 자동으로 활성화됩니다. 모듈은 프로덕션 준비 커스터마이징 옵션과 함께 개발을 위한 합리적인 기본값을 제공합니다.

#### 자동 구성 (Spring Boot)

`application.yml`에 JWT 속성을 추가하세요:

```yaml
peanut-butter:
  jwt:
    secret: "your-production-secret-key-minimum-256-bits-long"
    access-token-expiry: "PT15M"      # 15분
    refresh-token-expiry: "PT24H"     # 24시간
    refresh-token-enabled: true       # 리프레시 토큰 활성화
    refresh-token-rotation-enabled: false  # 기본적으로 로테이션 비활성화
    refresh-token-mode: "SIMPLE_VALIDATION"  # 간단한 검증 모드
    refresh-token-store-type: "IN_MEMORY"   # 인메모리 스토리지
    used-refresh-token-handling: "REMOVE"   # 사용된 토큰 제거
```

### 2. JWT 서비스 사용법

#### 기본 토큰 작업

```kotlin
@Service
class AuthenticationService(
    private val jwtService: JwtService
) {
    fun login(user: User): LoginResponse {
        // 커스텀 클레임으로 액세스 토큰 생성
        val claims = mapOf(
            "role" to user.role,
            "department" to user.department,
            "permissions" to user.permissions
        )
        val accessToken = jwtService.generateAccessToken(user.id, claims)
        
        // 활성화된 경우 리프레시 토큰 생성
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

### 3. 현재 사용자 컨텍스트

#### 현재 사용자 프로바이더 사용

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
            ?: throw SecurityException("인증된 사용자가 없습니다")
            
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

#### 커스텀 사용자 리졸버

```kotlin
@Component
class CustomUserResolver(
    private val userService: UserService,
    private val roleService: RoleService
) : JwtUserResolver<User> {
    
    override fun resolveUser(subject: String, claims: Map<String, Any>): User? {
        return try {
            val user = userService.findById(subject) ?: return null
            
            // 클레임의 추가 정보로 사용자 강화
            val role = claims["role"] as? String
            val department = claims["department"] as? String
            
            user.copy(
                currentRole = role?.let { roleService.findByName(it) },
                currentDepartment = department
            )
        } catch (e: Exception) {
            logError("사용자 리졸브 실패", e)
            null
        }
    }
}
```

### 4. 리프레시 토큰 스토리지 구성

#### 인메모리 스토리지 (기본값)

```yaml
peanut-butter:
  jwt:
    refresh-token-store-type: "IN_MEMORY"
    refresh-token-mode: "STORE_AND_VALIDATE"
```

#### Redis 스토리지

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

#### 데이터베이스 스토리지

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

### 5. 고급 보안 구성

#### 토큰 로테이션 구성

```yaml
peanut-butter:
  jwt:
    refresh-token-rotation-enabled: true
    used-refresh-token-handling: "BLACKLIST"  # 또는 "REMOVE"
```

#### 프로덕션 보안 구성

```yaml
peanut-butter:
  jwt:
    secret: "${JWT_SECRET}"  # 환경변수에서 로드
    access-token-expiry: "PT5M"        # 높은 보안을 위해 5분
    refresh-token-expiry: "PT7D"       # 7일
    refresh-token-rotation-enabled: true
    refresh-token-mode: "STORE_AND_VALIDATE"
    refresh-token-store-type: "REDIS"
    used-refresh-token-handling: "BLACKLIST"
```

### 6. 환경별 구성 예제

#### 개발 구성

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

#### 프로덕션 구성

```yaml
peanut-butter:
  jwt:
    secret: "${JWT_SECRET_KEY}"  # 필수 환경변수
    access-token-expiry: "PT15M"
    refresh-token-expiry: "PT7D"
    refresh-token-enabled: true
    refresh-token-rotation-enabled: true
    refresh-token-mode: "STORE_AND_VALIDATE"
    refresh-token-store-type: "REDIS"
    used-refresh-token-handling: "BLACKLIST"
```

#### 높은 보안 구성

```yaml
peanut-butter:
  jwt:
    secret: "${JWT_SECRET_KEY}"
    access-token-expiry: "PT5M"   # 매우 짧은 수명
    refresh-token-expiry: "PT1H"  # 짧은 수명 리프레시 토큰
    refresh-token-enabled: true
    refresh-token-rotation-enabled: true
    refresh-token-mode: "STORE_AND_VALIDATE"
    refresh-token-store-type: "RDB"  # 영구 스토리지
    used-refresh-token-handling: "BLACKLIST"
```

## JWT 인증 필터

**필요한 의존성**: JJWT + Spring Boot + Spring Security

Peanut-Butter 1.3.1부터 자동 permitAll() 경로 탐지와 유연한 구성 옵션을 갖춘 포괄적인 JWT 인증 필터를 제공합니다. 이 필터는 기존 Spring Security 설정과 원활하게 통합되어 요청 수준에서 JWT 인증을 처리합니다.

### 1. 기본 설정과 구성

Spring Security가 클래스패스에 있고 JWT 필터가 활성화되어 있을 때 JWT 인증 필터가 자동으로 활성화됩니다.

#### 자동 구성 (Spring Boot)

`application.yml`에 JWT 필터 속성을 추가하세요:

```yaml
peanut-butter:
  security:
    jwt:
      filter:
        enabled: true                       # JWT 필터 활성화/비활성화
        auto-detect-permit-all-paths: true  # permitAll() 경로 자동 탐지
        excluded-paths:                     # 수동 제외 경로 (선택사항)
          - "/api/public/**"
          - "/health/**"
          - "/actuator/**"
          - "/swagger-ui/**"
          - "/v3/api-docs/**"
```

### 2. 스마트 경로 탐지 기능

JWT 인증 필터의 주요 특징 중 하나는 기존 Spring Security 구성을 분석하여 `permitAll()` 경로를 자동으로 탐지하고 JWT 필터링에서 제외하는 능력입니다.

#### 자동 탐지 작동 방식

```kotlin
@Configuration
@EnableWebSecurity
class SecurityConfig {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .authorizeHttpRequests { auth ->
                // 이 경로들은 자동으로 탐지되어 JWT 필터링에서 제외됩니다
                auth.requestMatchers("/api/public/**").permitAll()
                auth.requestMatchers("/health", "/metrics").permitAll() 
                auth.requestMatchers("/login", "/register").permitAll()
                auth.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                auth.anyRequest().authenticated()
            }
            .build()
    }
}
```

자동 탐지가 활성화되면 (기본값), 필터는:
- Spring Security 구성을 스캔하여 `permitAll()` 매처를 찾음
- 해당 경로 패턴을 추출하여 JWT 필터링에서 자동 제외
- 수동 `excluded-paths`와 병합하여 완전한 제외 목록 생성

### 3. 구성 옵션

#### 개발 구성

```yaml
peanut-butter:
  security:
    jwt:
      filter:
        enabled: true
        auto-detect-permit-all-paths: true
        excluded-paths:
          - "/h2-console/**"     # 개발 DB 콘솔
          - "/debug/**"          # 개발 디버그 엔드포인트
```

#### 프로덕션 구성

```yaml
peanut-butter:
  security:
    jwt:
      filter:
        enabled: true
        auto-detect-permit-all-paths: true
        excluded-paths:
          - "/actuator/health"   # 헬스체크만 허용
          - "/actuator/info"     # 정보 엔드포인트만 허용
          # 다른 actuator 엔드포인트는 JWT 보호
```

#### 수동 경로 제어

```yaml
peanut-butter:
  security:
    jwt:
      filter:
        enabled: true
        auto-detect-permit-all-paths: false  # 자동 탐지 비활성화
        excluded-paths:                      # 수동으로만 제외 경로 설정
          - "/api/auth/login"
          - "/api/auth/register"
          - "/api/public/**"
          - "/health"
```

### 4. 필터 동작 방식

JWT 인증 필터는 다음 단계로 작동합니다:

1. **경로 확인**: 요청 경로가 제외 목록에 있는지 확인
2. **토큰 추출**: Authorization 헤더에서 Bearer 토큰 추출
3. **토큰 검증**: JWT 서비스를 통해 토큰 유효성 검증
4. **컨텍스트 설정**: 유효한 토큰의 경우 Spring Security 컨텍스트에 인증 설정
5. **권한 부여**: 토큰 클레임에서 역할과 권한 추출하여 설정

#### 토큰 클레임 처리

```kotlin
// JWT 토큰에 다음과 같은 클레임이 있다면:
val claims = mapOf(
    "roles" to listOf("USER", "ADMIN"),
    "authorities" to listOf("READ", "WRITE", "DELETE")
)

// 필터는 다음과 같이 Spring Security 권한을 설정합니다:
// - ROLE_USER, ROLE_ADMIN (역할에 ROLE_ 접두사 자동 추가)
// - READ, WRITE, DELETE (권한은 그대로 사용)
```

### 5. Spring Security와의 통합

#### SecurityFilterChain과의 통합

JWT 필터는 자동으로 기존 SecurityFilterChain에 통합되어 `UsernamePasswordAuthenticationFilter` 앞에 배치됩니다:

```kotlin
@Configuration
class SecurityConfig {
    
    // JWT 필터가 자동으로 이 설정과 통합됩니다
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .authorizeHttpRequests { auth ->
                auth.requestMatchers("/api/public/**").permitAll()
                auth.requestMatchers("/api/admin/**").hasRole("ADMIN")
                auth.requestMatchers("/api/user/**").hasRole("USER")
                auth.anyRequest().authenticated()
            }
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .csrf { it.disable() }
            .build()
    }
}
```

#### 커스텀 보안 설정과의 호환성

```kotlin
@Configuration
@EnableWebSecurity
class CustomSecurityConfig {
    
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .authorizeHttpRequests { auth ->
                // JWT 필터가 자동으로 이 허용 경로들을 탐지
                auth.requestMatchers("/api/auth/**").permitAll()
                auth.requestMatchers(HttpMethod.GET, "/api/products").permitAll()
                auth.requestMatchers("/api/admin/**").hasAuthority("ADMIN")
                auth.anyRequest().authenticated()
            }
            .oauth2ResourceServer { oauth2 ->
                oauth2.jwt { jwt ->
                    // JWT 필터와 OAuth2 JWT가 함께 작동 가능
                }
            }
            .build()
    }
}
```

### 6. 고급 구성 시나리오

#### 마이크로서비스 환경

```yaml
peanut-butter:
  security:
    jwt:
      filter:
        enabled: true
        auto-detect-permit-all-paths: true
        excluded-paths:
          - "/actuator/health"      # 서비스 메시
          - "/actuator/prometheus"   # 모니터링
          - "/api/internal/**"       # 내부 서비스 통신
```

#### API 게이트웨이 환경

```yaml
peanut-butter:
  security:
    jwt:
      filter:
        enabled: true
        auto-detect-permit-all-paths: false  # 게이트웨이에서 경로 제어
        excluded-paths: []                   # 모든 요청을 JWT로 보호
```

#### 하이브리드 인증 환경

```yaml
peanut-butter:
  security:
    jwt:
      filter:
        enabled: true
        auto-detect-permit-all-paths: true
        excluded-paths:
          - "/api/oauth/**"      # OAuth2 인증 경로
          - "/api/saml/**"       # SAML 인증 경로
          - "/api/basic/**"      # Basic 인증 경로
```

### 7. 모니터링과 디버깅

JWT 필터는 포괄적한 로깅을 제공합니다:

```yaml
# application.yml에서 로깅 활성화
logging:
  level:
    com.github.snowykte0426.peanut.butter.security.jwt: DEBUG
```

로그 예제:
```
DEBUG - JWT Filter: Processing request to /api/secure
DEBUG - JWT Filter: Token extracted from Authorization header
DEBUG - JWT Filter: Token validation successful for user: user123
DEBUG - JWT Filter: Authentication set in SecurityContext
DEBUG - JWT Filter: Request to /api/public/info excluded from JWT filtering
```

### 8. 오류 처리

JWT 필터는 우아한 오류 처리를 제공합니다:

- **토큰 없음**: 필터 체인 계속 (다른 인증 메커니즘에서 처리 가능)
- **잘못된 토큰**: 로그 기록 후 필터 체인 계속
- **만료된 토큰**: 경고 로그 후 필터 체인 계속
- **서버 오류**: 오류 로그 후 필터 체인 계속 (안전한 폴백)

### 9. 성능 최적화

JWT 필터는 성능을 위해 최적화되었습니다:

- **조기 제외**: 경로 매칭을 먼저 수행하여 불필요한 JWT 처리 방지
- **효율적인 패턴 매칭**: Ant 스타일 패턴 매치를 최적화
- **최소한의 오버헤드**: 제외된 경로는 거의 즉시 처리
- **캐시된 클레임**: 동일 요청 내에서 클레임 재사용

---

더 많은 정보는 [메인 README](../README.md)를 참조하거나 [GitHub 저장소](https://github.com/snowykte0426/peanut-butter)를 방문하세요.