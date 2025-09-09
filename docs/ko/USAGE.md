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

### 7. v1.1.0으로 마이그레이션

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

#### 디버그 로깅

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

## JWT 인증

**필요한 의존성**: JJWT + Spring Boot (현재 사용자 프로바이더용 선택적 서블릿 컨텍스트)

Peanut-Butter는 유연한 리프레시 토큰 관리, 다중 스토리지 백엔드, 프로덕션 준비된 보안 기능을 갖춘 포괄적인 JWT (JSON Web Token) 인증을 제공합니다.

### 1. 기본 설정과 구성

JJWT 의존성이 클래스패스에 있을 때 JWT 기능이 자동으로 활성화됩니다. 모듈은 프로덕션 준비된 커스터마이징 옵션과 함께 개발을 위한 합리적인 기본값을 제공합니다.

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
        // 커스텀 클레임과 함께 액세스 토큰 생성
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

#### 토큰 정보 추출

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

#### 커스텀 사용자 해결자

```kotlin
@Component
class CustomUserResolver(
    private val userService: UserService,
    private val roleService: RoleService
) : JwtUserResolver<User> {
    
    override fun resolveUser(subject: String, claims: Map<String, Any>): User? {
        return try {
            val user = userService.findById(subject) ?: return null
            
            // 클레임에서 추가 정보로 사용자 보강
            val role = claims["role"] as? String
            val department = claims["department"] as? String
            
            user.copy(
                currentRole = role?.let { roleService.findByName(it) },
                currentDepartment = department
            )
        } catch (e: Exception) {
            logError("사용자 해결 실패", e)
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
    secret: "${JWT_SECRET}"  # 환경에서 로드
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
    secret: "${JWT_SECRET_KEY}"  # 필수 환경 변수
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
    refresh-token-expiry: "PT1H"  # 짧은 수명의 리프레시 토큰
    refresh-token-enabled: true
    refresh-token-rotation-enabled: true
    refresh-token-mode: "STORE_AND_VALIDATE"
    refresh-token-store-type: "RDB"  # 영구 스토리지
    used-refresh-token-handling: "BLACKLIST"
```

### 8. JWT 인증 필터

**필요한 의존성**: Spring Security + JJWT (JWT 필터링 기능용)

JWT 인증 필터는 지능적인 경로 제외와 원활한 Spring Security 통합을 통해 자동 요청 레벨 JWT 인증을 제공합니다.

#### JWT 필터 활성화

`application.yml`에 JWT 필터 구성을 추가하세요:

```yaml
peanut-butter:
  security:
    jwt:
      filter:
        enabled: true                          # JWT 필터링 활성화
        auto-detect-permit-all-paths: true     # permitAll() 경로 자동 제외
        excluded-paths:                        # 추가 수동 제외
          - "/api/public/**"
          - "/health/**" 
          - "/actuator/**"
          - "/swagger-ui/**"
          - "/v3/api-docs/**"
```

#### 자동 permitAll() 감지

필터는 기존 Spring Security 구성을 자동으로 분석하고 `permitAll()`로 구성된 경로를 제외합니다:

```kotlin
@Configuration
@EnableWebSecurity
class SecurityConfig {
    
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .authorizeHttpRequests { auth ->
                // 이 경로들은 자동으로 감지되어 JWT 필터링에서 제외됩니다
                auth.requestMatchers("/api/public/**").permitAll()
                auth.requestMatchers("/health", "/metrics").permitAll() 
                auth.requestMatchers("/login", "/register").permitAll()
                auth.anyRequest().authenticated()
            }
            .build()
    }
}
```

#### 수동 경로 제외

JWT 인증을 우회해야 하는 추가 경로를 구성하세요:

```yaml
peanut-butter:
  security:
    jwt:
      filter:
        enabled: true
        excluded-paths:
          # Ant 스타일 패턴 지원
          - "/api/public/**"           # 모든 공개 API 엔드포인트 제외
          - "/health/*"                # 헬스 체크 엔드포인트 제외
          - "/admin/*/status"          # 관리자 상태 엔드포인트 제외
          - "/static/**"               # 정적 리소스 제외
          - "/docs"                    # 정확한 경로 제외
```

#### 결합된 구성 예제

```yaml
peanut-butter:
  jwt:
    # JWT 서비스 구성
    secret: "your-production-secret-key"
    access-token-expiry: "PT15M"
    refresh-token-enabled: true
    refresh-token-expiry: "PT24H"
    refresh-token-store-type: "REDIS"
    
  security:
    jwt:
      filter:
        # JWT 필터 구성  
        enabled: true
        auto-detect-permit-all-paths: true  # 자동 감지와
        excluded-paths:                     # 수동 제외 모두 사용
          - "/api/docs/**"
          - "/metrics/**"
```

#### 필터 동작

JWT 필터는 다음과 같이 작동합니다:

1. **경로 제외 확인**: 제외 패턴과 일치하는 요청은 JWT 인증을 우회
2. **토큰 추출**: Authorization 헤더에서 Bearer 토큰 추출 (`Authorization: Bearer <token>`)
3. **토큰 검증**: 구성된 JWT 서비스를 사용하여 토큰 검증
4. **인증 설정**: Spring Security 인증 컨텍스트를 다음과 함께 생성:
   - **Principal**: 토큰 주체 (일반적으로 사용자 ID)
   - **Authorities**: 토큰 클레임에서 추출한 역할 및 권한
5. **오류 처리**: 인증 없이 필터 체인을 계속하여 유효하지 않은 토큰을 우아하게 처리

#### 보안 컨텍스트 채우기

유효한 JWT 토큰이 발견되면 필터가 자동으로 Spring Security 컨텍스트를 채웁니다:

```kotlin
@RestController
class SecuredController {
    
    @GetMapping("/profile")
    fun getUserProfile(authentication: Authentication): UserProfile {
        val userId = authentication.principal as String
        val authorities = authentication.authorities.map { it.authority }
        
        // 토큰 클레임은 JWT 서비스를 통해 사용 가능
        return userService.getProfile(userId)
    }
    
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    fun getAdminData(): AdminData {
        // 역할 기반 보안이 자동으로 작동
        return adminService.getData()
    }
}
```

#### 프로덕션 구성

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
    # JWT 필터에 대한 디버그 로깅 활성화
    com.github.snowykte0426.peanut.butter.security.jwt: DEBUG
```

#### 디버그 로깅

```yaml
logging:
  level:
    com.github.snowykte0426.peanut.butter.security.jwt: DEBUG
    io.jsonwebtoken: DEBUG
```

## Discord 웹훅 알림

**필요한 의존성**: Spring Web (RestTemplate HTTP 클라이언트용) + Spring Boot (자동 구성용 선택사항)

Peanut-Butter는 다국어 지원, 서버 생명주기 모니터링, 예외 처리, 풍부한 임베드 형식을 갖춘 포괄적인 Discord 웹훅 알림을 제공합니다. Discord 모듈은 애플리케이션 이벤트에 대한 알림을 자동으로 전송하고 광범위한 커스터마이징 옵션을 제공합니다.

### 1. 기본 설정과 구성

Spring Web이 클래스패스에 있고 웹훅 구성이 제공되면 Discord 웹훅 알림이 자동으로 활성화됩니다. 모듈은 서버 생명주기 이벤트와 커스텀 예외 알림을 모두 지원합니다.

#### 자동 구성 (Spring Boot)

`application.yml`에 Discord 웹훅 속성을 추가하세요:

```yaml
peanut-butter:
  notification:
    discord:
      webhook:
        enabled: true                                    # Discord 알림 활성화
        url: "https://discord.com/api/webhooks/your-id/your-token"  # Discord 웹훅 URL
        timeout: 5000                                   # 요청 타임아웃(밀리초)
      embed:
        color: "#3498db"                                # 임베드 색상 (16진수)
        thumbnail-url: ""                               # 선택적 썸네일 URL
        footer-text: "Peanut-Butter 알림"              # 푸터 텍스트
        footer-icon-url: ""                             # 선택적 푸터 아이콘 URL
        timestamp-enabled: true                         # 임베드에 타임스탬프 포함
      locale: "ko"                                      # 언어: "en" 또는 "ko"
```

### 2. Discord 웹훅 URL 가져오기

Discord 웹훅 알림을 설정하려면:

1. **알림을 받을 Discord 서버로 이동**
2. **채널 우클릭** → "채널 편집" 
3. **"연동" 탭으로 이동** → "웹훅"
4. **"새 웹훅" 또는 "웹훅 만들기" 클릭**
5. **웹훅 구성**:
   - 이름: 예: "서버 알림"
   - 채널: 대상 채널 선택
   - 아바타: 선택적 커스텀 아바타
6. **웹훅 URL 복사**하여 구성에 추가

예제 웹훅 URL 형식:
```
https://discord.com/api/webhooks/1234567890123456789/abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456
```

### 3. 자동 서버 생명주기 알림

Discord 모듈은 서버 시작 및 종료 이벤트에 대한 알림을 자동으로 전송합니다:

#### 애플리케이션 시작 알림

Spring Boot 애플리케이션이 성공적으로 시작되면 다음을 포함하는 임베드 메시지가 전송됩니다:
- **애플리케이션 이름** (`spring.application.name`에서)
- **활성 프로파일** (`spring.profiles.active`에서) 
- **시작 타임스탬프**
- **서버 환경 정보**

```yaml
# 더 나은 알림을 위해 애플리케이션 이름 구성
spring:
  application:
    name: "내 멋진 API"
  profiles:
    active: "production"
```

#### 애플리케이션 종료 알림

애플리케이션이 정상적으로 종료되면 다음을 포함하는 알림이 전송됩니다:
- **애플리케이션 이름**
- **종료 타임스탬프** 
- **가동 시간 지속시간**
- **종료 이유** (사용 가능한 경우)

### 4. 예외 처리 및 알림

스택 트레이스와 함께 예외 알림을 전송하려면 `DiscordExceptionHandler` 서비스를 사용하세요:

#### 기본 예외 처리

```kotlin
@Service
class UserService(
    private val discordExceptionHandler: DiscordExceptionHandler
) {
    
    fun createUser(userData: UserData): User {
        return try {
            userRepository.save(userData)
        } catch (ex: Exception) {
            // 전체 스택 트레이스와 함께 Discord 알림 전송
            discordExceptionHandler.handleException(
                exception = ex,
                context = "${userData.username}에 대한 사용자 생성 실패"
            )
            throw ex // 다시 던지거나 필요에 따라 처리
        }
    }
}
```

#### 컨트롤러 예외 처리

```kotlin
@RestController
class UserController(
    private val userService: UserService,
    private val discordExceptionHandler: DiscordExceptionHandler
) {
    
    @PostMapping("/users")
    fun createUser(@RequestBody request: CreateUserRequest): ResponseEntity<User> {
        return try {
            val user = userService.createUser(request.toUserData())
            ResponseEntity.ok(user)
        } catch (ex: ValidationException) {
            discordExceptionHandler.handleException(
                exception = ex,
                context = "유효하지 않은 사용자 데이터 수신",
                includeRequestInfo = true  // HTTP 요청 세부 정보 포함
            )
            ResponseEntity.badRequest().build()
        } catch (ex: Exception) {
            discordExceptionHandler.handleException(
                exception = ex,
                context = "사용자 생성 중 예기치 않은 오류"
            )
            ResponseEntity.internalServerError().build()
        }
    }
}
```

#### 글로벌 예외 핸들러

```kotlin
@RestControllerAdvice
class GlobalExceptionHandler(
    private val discordExceptionHandler: DiscordExceptionHandler
) {
    
    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(ex: RuntimeException): ResponseEntity<ErrorResponse> {
        discordExceptionHandler.handleException(
            exception = ex,
            context = "처리되지 않은 런타임 예외 발생",
            includeRequestInfo = true
        )
        
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse("내부 서버 오류"))
    }
    
    @ExceptionHandler(SecurityException::class)
    fun handleSecurityException(ex: SecurityException): ResponseEntity<ErrorResponse> {
        discordExceptionHandler.handleException(
            exception = ex,
            context = "보안 위반 감지",
            includeRequestInfo = true
        )
        
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(ErrorResponse("권한 없음"))
    }
}
```

### 5. 커스텀 알림

`DiscordWebhookService`를 사용하여 커스텀 Discord 알림을 전송하세요:

#### 수동 알림

```kotlin
@Service
class OrderService(
    private val discordWebhookService: DiscordWebhookService
) {
    
    fun processLargeOrder(order: Order) {
        if (order.amount > 10000) {
            // 큰 주문에 대한 커스텀 알림 전송
            discordWebhookService.sendCustomNotification(
                title = "큰 주문 알림",
                description = "주문 #${order.id} ${order.amount}원이 접수되었습니다",
                color = "#f39c12", // 주황색
                fields = mapOf(
                    "고객" to order.customerName,
                    "금액" to "${order.amount}원",
                    "항목" to order.items.size.toString()
                )
            )
        }
        
        processOrder(order)
    }
}
```

#### 비즈니스 이벤트 알림

```kotlin
@EventListener
class BusinessEventHandler(
    private val discordWebhookService: DiscordWebhookService
) {
    
    @EventListener
    fun handleUserRegistration(event: UserRegistrationEvent) {
        discordWebhookService.sendCustomNotification(
            title = "새 사용자 등록",
            description = "${event.username}님, 환영합니다!",
            color = "#2ecc71", // 초록색
            fields = mapOf(
                "사용자명" to event.username,
                "이메일" to event.email,
                "등록일" to event.registrationDate.toString()
            )
        )
    }
    
    @EventListener
    fun handlePaymentSuccess(event: PaymentSuccessEvent) {
        discordWebhookService.sendCustomNotification(
            title = "결제 처리됨",
            description = "결제 #${event.paymentId}가 성공적으로 완료되었습니다",
            color = "#27ae60", // 초록색
            fields = mapOf(
                "금액" to "${event.amount}원",
                "고객" to event.customerName,
                "결제 방법" to event.paymentMethod
            )
        )
    }
}
```

### 6. 환경별 구성 예제

#### 개발 구성

```yaml
peanut-butter:
  notification:
    discord:
      webhook:
        enabled: true
        url: "https://discord.com/api/webhooks/dev-webhook-id/dev-token"
        timeout: 10000  # 개발용 더 긴 타임아웃
      embed:
        color: "#e74c3c"  # 개발 환경용 빨간색
        footer-text: "개발 환경"
        timestamp-enabled: true
      locale: "ko"

spring:
  application:
    name: "MyApp (개발)"
  profiles:
    active: "development"
```

#### 프로덕션 구성

```yaml
peanut-butter:
  notification:
    discord:
      webhook:
        enabled: true
        url: "${DISCORD_WEBHOOK_URL}"  # 환경 변수에서 로드
        timeout: 5000
      embed:
        color: "#2ecc71"  # 프로덕션용 초록색
        footer-text: "프로덕션 환경"
        thumbnail-url: "https://mycompany.com/logo.png"
        footer-icon-url: "https://mycompany.com/favicon.ico"
        timestamp-enabled: true
      locale: "ko"

spring:
  application:
    name: "MyApp 프로덕션"
  profiles:
    active: "production"
```

#### 스테이징 구성

```yaml
peanut-butter:
  notification:
    discord:
      webhook:
        enabled: true
        url: "${DISCORD_WEBHOOK_URL}"
        timeout: 7000
      embed:
        color: "#f39c12"  # 스테이징용 주황색
        footer-text: "스테이징 환경"
        timestamp-enabled: true
      locale: "ko"

spring:
  application:
    name: "MyApp 스테이징" 
  profiles:
    active: "staging"
```

### 7. 다국어 지원

Discord 모듈은 자동 메시지 현지화와 함께 영어와 한국어를 모두 지원합니다:

#### 영어 메시지 (기본값)

```yaml
peanut-butter:
  notification:
    discord:
      locale: "en"  # 영어 메시지
```

영어 알림 예제:
- **제목**: "🚀 Server Started"
- **설명**: "MyApp is now running"
- **필드**: "Environment: production", "Startup Time: 2.3s"

#### 한국어 메시지

```yaml
peanut-butter:
  notification:
    discord:
      locale: "ko"  # 한국어 메시지  
```

한국어 알림 예제:
- **제목**: "🚀 서버 시작됨"
- **설명**: "MyApp이 현재 실행 중입니다"
- **필드**: "환경: production", "시작 시간: 2.3초"

#### 런타임 언어 전환

```kotlin
@Service
class NotificationService(
    private val discordProperties: DiscordProperties
) {
    
    fun switchToKorean() {
        // 언어는 시작 시 구성되지만 다른 언어용으로
        // 다른 DiscordWebhookService 인스턴스를 생성할 수 있습니다
        val koreanService = DiscordWebhookService(
            discordProperties.copy(locale = "ko")
        )
        
        koreanService.sendCustomNotification(
            title = "언어 변경됨",
            description = "알림 언어가 한국어로 변경되었습니다"
        )
    }
}
```

## 고급 예제

이 섹션에서는 Peanut-Butter의 여러 기능을 결합한 실제 시나리오와 고급 사용 패턴을 보여줍니다.

### 1. 전체 스택 인증 시스템

JWT, CORS, Discord 알림을 결합한 완전한 인증 시스템:

```kotlin
@RestController
@RequestMapping("/api/auth")
class AuthenticationController(
    private val jwtService: JwtService,
    private val userService: UserService,
    private val discordWebhookService: DiscordWebhookService,
    private val currentUserProvider: CurrentUserProvider<User>
) {
    
    @PostMapping("/login")
    suspend fun login(@RequestBody request: LoginRequest): ResponseEntity<LoginResponse> {
        return logExecutionTimeAsync("사용자 로그인") {
            try {
                val user = userService.authenticate(request.username, request.password)
                    ?: return@logExecutionTimeAsync ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(LoginResponse(error = "유효하지 않은 자격 증명"))
                
                val accessToken = jwtService.generateAccessToken(
                    subject = user.id,
                    claims = mapOf(
                        "role" to user.role,
                        "department" to user.department,
                        "permissions" to user.permissions
                    )
                )
                
                val refreshToken = if (jwtService.refreshTokenEnabled) {
                    jwtService.generateRefreshToken(user.id)
                } else null
                
                // 성공적인 로그인 알림
                discordWebhookService.sendCustomNotification(
                    title = "🔐 사용자 로그인",
                    description = "${user.username}님이 로그인했습니다",
                    color = "#2ecc71",
                    fields = mapOf(
                        "사용자" to user.username,
                        "역할" to user.role,
                        "IP 주소" to (request.remoteAddr ?: "알 수 없음"),
                        "시간" to LocalDateTime.now().toString()
                    )
                )
                
                ResponseEntity.ok(LoginResponse(accessToken, refreshToken))
                
            } catch (ex: Exception) {
                logErrorAsync("로그인 실패", ex)
                
                discordWebhookService.sendCustomNotification(
                    title = "❌ 로그인 실패",
                    description = "로그인 시도 실패: ${request.username}",
                    color = "#e74c3c"
                )
                
                ResponseEntity.internalServerError()
                    .body(LoginResponse(error = "로그인 처리 중 오류"))
            }
        }
    }
    
    @PostMapping("/refresh")
    suspend fun refreshToken(@RequestBody request: RefreshTokenRequest): ResponseEntity<TokenResponse> {
        return try {
            val tokenPair = jwtService.refreshTokens(request.refreshToken)
                ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(TokenResponse(error = "유효하지 않은 리프레시 토큰"))
            
            ResponseEntity.ok(TokenResponse(tokenPair.accessToken, tokenPair.refreshToken))
        } catch (ex: Exception) {
            logWarningAsync("토큰 새로고침 실패", ex)
            ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(TokenResponse(error = "토큰 새로고침 실패"))
        }
    }
    
    @PostMapping("/logout")
    suspend fun logout(@RequestHeader("Authorization") authHeader: String): ResponseEntity<Unit> {
        return try {
            val token = authHeader.removePrefix("Bearer ")
            val userId = currentUserProvider.getCurrentUserId()
            
            // 리프레시 토큰 무효화 (구현에 따라)
            jwtService.invalidateUserTokens(userId)
            
            logInfoAsync("사용자 로그아웃: {}", userId)
            
            ResponseEntity.ok().build()
        } catch (ex: Exception) {
            logErrorAsync("로그아웃 실패", ex)
            ResponseEntity.internalServerError().build()
        }
    }
}
```

### 2. 고급 에러 처리 및 모니터링

포괄적인 오류 처리, 로깅, Discord 알림을 결합:

```kotlin
@RestControllerAdvice
class GlobalExceptionHandler(
    private val discordExceptionHandler: DiscordExceptionHandler,
    private val discordWebhookService: DiscordWebhookService
) {
    
    @ExceptionHandler(ValidationException::class)
    suspend fun handleValidationException(
        ex: ValidationException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        
        val errorId = UUID.randomUUID().toString()
        
        logWarnAsync("검증 오류 [{}]: {}", errorId, ex.message)
        
        // 심각한 검증 오류에 대해서만 Discord 알림
        if (ex.severity == ValidationSeverity.HIGH) {
            withLoggingContext(errorId) {
                discordWebhookService.sendCustomNotification(
                    title = "⚠️ 높은 심각도 검증 오류",
                    description = "심각한 검증 오류가 감지되었습니다",
                    color = "#f39c12",
                    fields = mapOf(
                        "오류 ID" to errorId,
                        "메시지" to ex.message.orEmpty(),
                        "엔드포인트" to request.requestURI,
                        "사용자 에이전트" to (request.getHeader("User-Agent") ?: "알 수 없음")
                    )
                )
            }
        }
        
        return ResponseEntity.badRequest().body(
            ErrorResponse(
                message = "검증 실패",
                errorId = errorId,
                details = ex.validationErrors
            )
        )
    }
    
    @ExceptionHandler(SecurityException::class)
    suspend fun handleSecurityException(
        ex: SecurityException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        
        val errorId = UUID.randomUUID().toString()
        
        // 보안 예외는 항상 Discord로 알림
        withLoggingContext(errorId) {
            logErrorAsync("보안 위반 [{}]", errorId, ex)
            
            discordExceptionHandler.handleException(
                exception = ex,
                context = "보안 위반 감지 - 즉시 조치 필요",
                includeRequestInfo = true
            )
            
            // 추가 보안 알림
            discordWebhookService.sendCustomNotification(
                title = "🚨 보안 알림",
                description = "보안 위반이 감지되었습니다",
                color = "#dc3545",
                fields = mapOf(
                    "오류 ID" to errorId,
                    "IP 주소" to (request.remoteAddr ?: "알 수 없음"),
                    "엔드포인트" to request.requestURI,
                    "메서드" to request.method,
                    "시간" to LocalDateTime.now().toString()
                )
            )
        }
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
            ErrorResponse(
                message = "접근이 거부되었습니다",
                errorId = errorId
            )
        )
    }
    
    @ExceptionHandler(Exception::class)
    suspend fun handleGenericException(
        ex: Exception,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        
        val errorId = UUID.randomUUID().toString()
        
        withLoggingContext(errorId) {
            logExecutionTimeAsync("예외 처리") {
                logErrorAsync("처리되지 않은 예외 [{}]", errorId, ex)
                
                // 예외 유형에 따른 조건부 알림
                val shouldNotify = when {
                    ex is OutOfMemoryError -> true
                    ex.message?.contains("database", ignoreCase = true) == true -> true
                    ex.stackTrace.any { it.className.contains("payment", ignoreCase = true) } -> true
                    else -> false
                }
                
                if (shouldNotify) {
                    discordExceptionHandler.handleException(
                        exception = ex,
                        context = "중요한 시스템 예외 발생",
                        includeRequestInfo = true
                    )
                }
            }
        }
        
        return ResponseEntity.internalServerError().body(
            ErrorResponse(
                message = "내부 서버 오류가 발생했습니다",
                errorId = errorId
            )
        )
    }
}
```

### 3. 멀티 테넌트 애플리케이션

다중 테넌트 지원, 타임존 처리, 커스텀 로깅을 결합:

```kotlin
@Service
class MultiTenantUserService(
    private val userRepository: UserRepository,
    private val tenantService: TenantService,
    private val currentUserProvider: CurrentUserProvider<User>,
    private val discordWebhookService: DiscordWebhookService
) {
    
    suspend fun createUserWithTenantContext(userData: UserData): User {
        val tenantId = currentUserProvider.getCurrentUserClaims()?.get("tenantId") as? String
            ?: throw SecurityException("테넌트 컨텍스트를 찾을 수 없습니다")
        
        val tenant = tenantService.findById(tenantId)
            ?: throw IllegalArgumentException("유효하지 않은 테넌트: $tenantId")
        
        return withLoggingContext("tenant-$tenantId") {
            // 테넌트의 타임존으로 전환
            withTimeZone(tenant.timeZone) {
                logExecutionTimeAsync("테넌트별 사용자 생성") {
                    
                    logInfoAsync("테넌트 {}에서 사용자 생성 시작: {}", tenant.name, userData.username)
                    
                    val user = retryWithLogging(
                        operation = "사용자 데이터베이스 저장",
                        maxAttempts = 3,
                        initialDelay = 1000L
                    ) {
                        userRepository.saveWithTenant(userData.copy(tenantId = tenantId))
                    }
                    
                    // 테넌트별 성공 알림
                    discordWebhookService.sendCustomNotification(
                        title = "👤 새 사용자 생성됨",
                        description = "테넌트 ${tenant.name}에서 새 사용자가 생성되었습니다",
                        color = "#28a745",
                        fields = mapOf(
                            "테넌트" to tenant.name,
                            "사용자명" to user.username,
                            "이메일" to user.email,
                            "타임존" to tenant.timeZone.toString(),
                            "현지 시간" to getCurrentTimeIn(tenant.timeZone).toString()
                        )
                    )
                    
                    logInfoAsync("사용자 생성 완료: {} (테넌트: {})", user.username, tenant.name)
                    user
                }
            }
        }
    }
    
    suspend fun getUsersByTenantWithPagination(
        tenantId: String,
        page: Int,
        size: Int
    ): Page<User> {
        return withLoggingContext("tenant-$tenantId-query") {
            logMethodExecutionAsync(
                "getUsersByTenantWithPagination",
                mapOf(
                    "tenantId" to tenantId,
                    "page" to page,
                    "size" to size
                )
            ) {
                val tenant = tenantService.findById(tenantId)
                    ?: throw IllegalArgumentException("테넌트를 찾을 수 없음: $tenantId")
                
                // 대용량 쿼리에 대한 성능 모니터링
                val result = logPerformanceAsync("테넌트별 사용자 쿼리") {
                    userRepository.findByTenantIdWithPagination(tenantId, page, size)
                }
                
                // 대용량 결과 집합에 대한 알림
                if (result.totalElements > 10000) {
                    discordWebhookService.sendCustomNotification(
                        title = "📊 대용량 쿼리 감지",
                        description = "테넌트 ${tenant.name}에서 대용량 사용자 쿼리가 실행되었습니다",
                        color = "#ffc107",
                        fields = mapOf(
                            "총 결과" to result.totalElements.toString(),
                            "페이지 크기" to size.toString(),
                            "테넌트" to tenant.name
                        )
                    )
                }
                
                result
            }
        }
    }
}
```

### 4. 마이크로서비스 통합

여러 Peanut-Butter 기능을 사용한 마이크로서비스 간 통신:

```kotlin
@Service
class OrderProcessingService(
    private val paymentService: PaymentServiceClient,
    private val inventoryService: InventoryServiceClient,
    private val notificationService: NotificationServiceClient,
    private val discordWebhookService: DiscordWebhookService,
    private val jwtService: JwtService
) {
    
    suspend fun processOrderWithDistributedLogging(order: Order): OrderResult {
        val correlationId = "order-${order.id}-${UUID.randomUUID()}"
        
        return withLoggingContext(correlationId) {
            logInfoAsync("주문 처리 시작: {}", order.id)
            
            // 분산 트랜잭션 처리
            val result = executeParallelWithLogging {
                listOf(
                    suspend {
                        logInfoAsync("재고 확인 시작")
                        checkInventoryAsync(order, correlationId)
                    },
                    suspend {
                        logInfoAsync("결제 검증 시작")
                        validatePaymentAsync(order, correlationId)
                    },
                    suspend {
                        logInfoAsync("고객 알림 준비")
                        prepareNotificationAsync(order, correlationId)
                    }
                )
            }
            
            val (inventoryResult, paymentResult, notificationResult) = result
            
            if (inventoryResult.success && paymentResult.success) {
                // 성공적인 주문 처리
                val orderResult = completeOrderAsync(order, correlationId)
                
                // 비즈니스 성공 알림
                discordWebhookService.sendCustomNotification(
                    title = "✅ 주문 처리 완료",
                    description = "주문 #${order.id}가 성공적으로 처리되었습니다",
                    color = "#28a745",
                    fields = mapOf(
                        "주문 ID" to order.id,
                        "고객" to order.customerName,
                        "금액" to "${order.totalAmount}원",
                        "상관관계 ID" to correlationId,
                        "처리 시간" to "${orderResult.processingTimeMs}ms"
                    )
                )
                
                orderResult
                
            } else {
                // 실패한 주문 처리
                val failureReasons = mutableListOf<String>()
                if (!inventoryResult.success) failureReasons.add("재고 부족")
                if (!paymentResult.success) failureReasons.add("결제 실패")
                
                logWarnAsync("주문 처리 실패: {} - 사유: {}", order.id, failureReasons)
                
                discordWebhookService.sendCustomNotification(
                    title = "❌ 주문 처리 실패",
                    description = "주문 #${order.id} 처리가 실패했습니다",
                    color = "#dc3545",
                    fields = mapOf(
                        "주문 ID" to order.id,
                        "실패 사유" to failureReasons.joinToString(", "),
                        "상관관계 ID" to correlationId
                    )
                )
                
                OrderResult.failure(failureReasons)
            }
        }
    }
    
    private suspend fun checkInventoryAsync(order: Order, correlationId: String): ServiceResult {
        return retryWithLogging(
            operation = "재고 서비스 호출",
            maxAttempts = 3,
            backoffFactor = 1.5
        ) {
            // 서비스 간 JWT 토큰 전파
            val serviceToken = jwtService.generateServiceToken(correlationId)
            
            inventoryService.checkAvailability(
                request = InventoryCheckRequest(
                    items = order.items,
                    correlationId = correlationId
                ),
                authorization = "Bearer $serviceToken"
            )
        }
    }
    
    private suspend fun validatePaymentAsync(order: Order, correlationId: String): ServiceResult {
        return logOnExceptionAsync("결제 검증 실패") {
            val serviceToken = jwtService.generateServiceToken(correlationId)
            
            paymentService.validatePayment(
                request = PaymentValidationRequest(
                    amount = order.totalAmount,
                    paymentMethodId = order.paymentMethodId,
                    correlationId = correlationId
                ),
                authorization = "Bearer $serviceToken"
            )
        } ?: ServiceResult.failure("결제 서비스 오류")
    }
}
```

