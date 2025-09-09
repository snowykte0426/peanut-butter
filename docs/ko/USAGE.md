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
- [Discord 웹훅 알림](#discord-웹훅-알림)
- [모범 사례](#모범-사례)
- [고급 예제](#고급-예제)

## 의존성 관리

Peanut-Butter는 **경량화 및 모듈형**으로 설계되었습니다. 실제로 사용하는 기능에 대한 의존성만 포함하면 됩니다.

### 핵심 설치 (최소)

```kotlin
dependencies {
    implementation("com.github.snowykte0426:peanut-butter:1.4.0")
    
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

#### Discord 웹훅 알림용
```kotlin
dependencies {
    // Discord 웹훅 지원 (필수)
    implementation("org.springframework.boot:spring-boot-starter-web") // RestTemplate용
    
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
| Discord 웹훅 알림 | ❌ | ❌ | ❌ | ❌ | ✅ | ✅ | ✅ |
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

### 3. 고급 구성 예제

#### 프로그래매틱 구성

더 많은 제어가 필요한 경우 `CorsConfigurationSource` 빈에 접근할 수 있습니다:

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

#### 커스텀 CORS 구성 빈

자신만의 빈을 제공하여 기본 구성을 재정의할 수 있습니다:

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

### 4. SecurityFilterChain 통합 예제

#### 기본 Security Filter Chain 사용

라이브러리는 CORS가 활성화된 기본 SecurityFilterChain을 제공합니다:

```kotlin
// 추가 구성 필요 없음 - 자동 구성된 SecurityFilterChain이
// 속성에서 CORS 구성을 포함합니다
```

#### CORS를 포함한 커스텀 SecurityFilterChain

프로덕션 애플리케이션의 경우 자신만의 SecurityFilterChain을 생성하세요:

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
                csrf.disable() // API용으로 비활성화, 필요에 따라 구성
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

#### 다중 CORS 구성

다른 엔드포인트에 대해 다른 CORS 정책을 구성하세요:

```kotlin
@Configuration
class MultiCorsConfiguration {
    
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val source = UrlBasedCorsConfigurationSource()
        
        // API 엔드포인트 - 엄격한 CORS
        val apiConfig = CorsConfiguration().apply {
            allowedOrigins = listOf("https://app.mycompany.com")
            allowedHeaders = listOf("Content-Type", "Authorization")
            allowedMethods = listOf("GET", "POST", "PUT", "DELETE")
            allowCredentials = true
            maxAge = 3600L
        }
        source.registerCorsConfiguration("/api/**", apiConfig)
        
        // 공개 엔드포인트 - 완화된 CORS
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

#### WebMvc CORS 구성

비보안 CORS 구성 (WebMvc만 해당):

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

### 5. 모범 사례

#### 보안 모범 사례

```yaml
# ✅ 좋음: 프로덕션에서 특정 출처 사용
peanut-butter:
  security:
    cors:
      allowed-origins: 
        - "https://myapp.com"
        - "https://www.myapp.com"

# ❌ 피하기: 자격 증명과 함께 프로덕션에서 와일드카드 출처
peanut-butter:
  security:
    cors:
      allowed-origins: ["*"]
      allow-credentials: true  # 보안 위험!
```

#### 메서드 구성

```yaml
# ✅ 좋음: 필요한 메서드만 명시적으로 구성
peanut-butter:
  security:
    cors:
      allowed-methods:
        GET: true
        POST: true
        PUT: true
        DELETE: false        # 필요하지 않으면 비활성화
        PATCH: true
        HEAD: true
        OPTIONS: true
        TRACE: false         # 항상 TRACE 비활성화

# ❌ 피하기: 고려 없이 모든 메서드 허용
```

#### 헤더 구성

```yaml
# ✅ 좋음: 보안을 위한 특정 헤더
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

# ❌ 피하기: 민감한 애플리케이션에서 와일드카드 헤더
peanut-butter:
  security:
    cors:
      allowed-headers: ["*"]   # 너무 허용적
```

#### 환경별 구성

```kotlin
// 개발 프로파일
@Configuration
@Profile("dev")
class DevCorsConfiguration {
    
    @Bean
    @Primary
    fun devCorsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration().apply {
            allowedOriginPatterns = listOf("*")  // 개발에서 모두 허용
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

// 프로덕션 프로파일  
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

#### CORS 구성 테스팅

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

#### 일반적인 구성 패턴

| 사용 사례 | 구성 | 비고 |
|----------|------|------|
| **개발** | `allowed-origins: ["*"]`, `allow-credentials: true` | 로컬 개발을 위한 허용적 설정 |
| **단일 페이지 앱** | 특정 출처, `allow-credentials: true` | 인증된 SPA용 |
| **공개 API** | 특정 출처, `allow-credentials: false` | 인증 없는 공개 API용 |
| **마이크로서비스** | 패턴 기반 출처, 특정 메서드 | 서비스 간 통신용 |
| **CDN 통합** | 다중 출처, 특정 헤더 | CDN을 통해 제공되는 자산용 |

#### CORS 문제 디버깅

CORS 문제를 해결하기 위해 디버그 로깅을 활성화하세요:

```yaml
logging:
  level:
    org.springframework.web.cors: DEBUG
    com.github.snowykte0426.peanut.butter.security.cors: DEBUG
```

일반적인 CORS 오류 시나리오:
- **"Access to fetch at '...' has been blocked"**: `allowed-origins` 확인
- **"Method not allowed"**: `allowed-methods`에서 HTTP 메서드 확인
- **"Request header field X is not allowed"**: `allowed-headers`에 헤더 추가
- **"Credentials flag is 'true', but origin is '*'"**: 자격 증명과 함께 특정 출처 사용
