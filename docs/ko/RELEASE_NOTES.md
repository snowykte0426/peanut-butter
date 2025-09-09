# 릴리즈 노트

**Language**: [English](../RELEASE_NOTES.md)

## v1.4.1

### 요약
Redis 기반 리프레시 토큰 저장소 테스트에서 JSON 직렬화를 위한 향상된 의존성 관리로 일관된 테스트 신뢰성과 안정성을 보장하는 테스트 인프라 개선.

### 새로운 기능
- 없음 (테스트 인프라에 중점을 둔 유지보수 릴리즈)

### 개선사항
- **테스트 안정성**: Java 8 시간 타입을 위한 적절한 Jackson 모듈 구성으로 테스트 인프라 강화
- **향상된 오류 처리**: 테스트 오류 리포팅 및 진단 기능 개선
- **의존성 관리**: 포괄적인 JSON 직렬화 지원을 위한 누락된 Jackson JSR310 모듈 추가

### 버그 수정
- **Jackson JSR310 모듈**: Redis 리프레시 토큰 저장소 테스트에서 `java.time.Instant` 직렬화 문제를 해결하기 위해 누락된 `jackson-datatype-jsr310` 의존성을 추가
- **테스트 의존성**: Java 8 시간 타입을 위한 적절한 Jackson 모듈 구성으로 테스트 인프라 수정

### 호환성 변경사항
- 없음 (완전한 하위 호환성)

### 폐지됨
- 없음

### 주요 하이라이트
- 적절한 의존성 해결로 361개의 모든 테스트가 일관되게 통과
- 모든 모듈에서 테스트 안정성과 신뢰성 강화
- 원활한 업그레이드 경험을 보장하는 호환성 변경사항 없음

### 요구사항
- Java 17+
- SLF4J 2.0+
- (선택적) Kotlin 확장을 위한 Kotlin 1.9+
- (선택적) 검증 기능을 위한 Jakarta Bean Validation 3.0+
- (선택적) 비동기 로깅을 위한 Kotlin Coroutines 1.7.3+
- (선택적) JWT 및 CORS 기능을 위한 Spring Boot 3.1.x + Spring Security 6.3.x
- **Discord 웹훅 HTTP 클라이언트 기능을 위한 Spring Web 6.2.8+**

### 설치
```kotlin
dependencies {
    implementation("com.github.snowykte0426:peanut-butter:1.4.1")
    
    // Discord 웹훅 기능용 (필수)
    implementation("org.springframework.boot:spring-boot-starter-web")
    
    // JWT 기능용 (선택적)
    implementation("io.jsonwebtoken:jjwt-api:0.12.3")
    implementation("io.jsonwebtoken:jjwt-impl:0.12.3")
    implementation("io.jsonwebtoken:jjwt-jackson:0.12.3")
    
    // Spring Security 기능용 (선택적)
    implementation("org.springframework.boot:spring-boot-starter-security")
}
```

### 마이그레이션 가이드
**v1.4.0에서 v1.4.1로**: 완전한 하위 호환성. 단순히 버전 번호를 업데이트하세요.

1. **버전 업데이트**: 의존성을 `1.4.1`로 변경
2. **코드 변경 불필요**: 모든 기존 구성과 코드가 변경 없이 유지됨
3. **향상된 테스트 안정성**: 개선된 테스트 인프라의 혜택을 자동으로 받음

---
*자세한 예제와 사용 패턴은 README.md와 docs/USAGE.md를 참조하세요.*

---

## v1.4.0

### 요약
**Discord 웹훅 알림 기능** – 서버 생명주기 이벤트와 예외 모니터링을 위한 포괄적인 Discord 웹훅 알림 기능을 추가합니다. 이 주요 기능은 임베드 메시지, 다국어 지원, 현대적인 DevOps와 모니터링 워크플로우를 위한 유연한 구성 옵션을 갖춘 실시간 서버 상태 알림을 제공합니다.

### 새로운 기능
- **Discord 웹훅 통합**: 완전한 Discord 알림 시스템 (`DiscordWebhookService`, `DiscordProperties`)
  - 풍부한 임베드 형식을 갖춘 서버 시작 및 종료 알림
  - 전체 스택 트레이스 포함을 갖춘 예외 알림 (Discord 제한에 맞게 자동 절단)
  - 구성 가능한 웹훅 URL, 타임아웃, 재시도 메커니즘
  - 모든 알림에서 애플리케이션 컨텍스트 정보 (이름, 프로파일, 호스트명)
- **다국어 지원**: 포괄적인 국제화 (`DiscordMessageLocalizer`)
  - **영어** (기본값): 모든 알림 유형을 위한 프로덕션 준비 영어 메시지
  - **한국어**: 로컬라이즈된 배포를 위한 완전한 한국어 번역 지원
  - **구성 가능한 로케일**: `peanut-butter.notification.discord.locale`을 통한 런타임 언어 선택
- **자동 이벤트 리스닝**: 무설정 생명주기 모니터링 (`DiscordApplicationEventListener`)
  - **ApplicationReadyEvent**: 서버가 준비되었을 때 자동 시작 알림
  - **ContextClosedEvent**: 애플리케이션 종료 시 우아한 종료 알림
  - **Spring Boot 통합**: Spring Boot 생명주기 이벤트와의 원활한 통합
- **예외 처리 통합**: 프로덕션 준비 오류 모니터링 (`DiscordExceptionHandler`)
  - **수동 예외 보고**: 사용자 정의 예외 알림을 위한 `handleException()` 메서드
  - **요청 컨텍스트 통합**: 웹 예외를 위한 HTTP 요청 정보 자동 포함
  - **글로벌 예외 리스너**: 시스템 전체 오류 모니터링을 위한 `UncaughtExceptionEvent` 지원
- **풍부한 임베드 형식**: 사용자 정의 가능한 Discord 임베드 모양
  - **구성 가능한 색상**: 다양한 알림 유형 (성공, 경고, 오류)을 위한 16진수 색상 지원
  - **타임스탬프 통합**: 타임존 지원을 갖춘 자동 ISO 타임스탬프 포함
  - **필드 사용자 정의**: 애플리케이션 이름, 프로파일, 호스트명 표시 제어
  - **푸터 브랜딩**: 라이브러리 귀속을 갖춘 구성 가능한 푸터 텍스트

### 개선사항
- **무설정 설정**: 최소한의 구성 요구사항으로 즉시 작동
- **프로덕션 준비 기본값**: 즉시 배포를 위한 안전하고 합리적인 기본값
- **유연한 구성**: `application.yml`을 통한 완전한 속성 기반 구성
- **성능 최적화**: 구성 가능한 타임아웃을 갖춘 비동기 웹훅 호출
- **오류 복원력**: 애플리케이션 기능에 영향을 주지 않는 Discord API 실패의 우아한 처리
- **Spring Boot 자동 구성**: 자동 빈 와이어링과 조건부 활성화
- **포괄적인 테스트**: 모든 컴포넌트를 위한 단위 테스트를 통한 전체 테스트 커버리지

### 버그 수정
- 없음 (기능 중심 릴리즈)

### 브레이킹 체인지
- 없음 (v1.3.1과 완전한 하위 호환성)

### 폐지됨
- 없음

### 주요 하이라이트
- DevOps 팀을 위한 Discord 웹훅 알림을 통한 **실시간 서버 모니터링**
- 국제 배포 환경을 위한 **다국어 알림 지원**
- 컨텍스트 정보와 적절한 형식을 갖춘 **풍부한 임베드 메시징**
- 전체 스택 트레이스 보고와 요청 컨텍스트를 갖춘 **예외 모니터링 통합**
- 즉시 배포 준비를 위한 **무설정 생명주기 추적**
- 색상, 내용, 형식 기본 설정을 위한 **유연한 사용자 정의 옵션**

### 요구사항
- Java 17+
- SLF4J 2.0+
- (선택적) Kotlin 확장을 위한 Kotlin 1.9+
- (선택적) 검증 기능을 위한 Jakarta Bean Validation 3.0+
- (선택적) 비동기 로깅을 위한 Kotlin Coroutines 1.7.3+
- (선택적) JWT 및 CORS 기능을 위한 Spring Boot 3.1.x + Spring Security 6.3.x
- **새로움: Discord 웹훅 HTTP 클라이언트 기능을 위한 Spring Web 6.2.8+**

### 설치
```kotlin
dependencies {
    implementation("com.github.snowykte0426:peanut-butter:1.4.0")
    
    // Discord 웹훅 기능용 (필수)
    implementation("org.springframework.boot:spring-boot-starter-web")
    
    // JWT 기능용 (선택적)
    implementation("io.jsonwebtoken:jjwt-api:0.12.3")
    implementation("io.jsonwebtoken:jjwt-impl:0.12.3")
    implementation("io.jsonwebtoken:jjwt-jackson:0.12.3")
    
    // Spring Security 기능용 (선택적)
    implementation("org.springframework.boot:spring-boot-starter-security")
}
```

### 마이그레이션 가이드
**v1.3.1에서 v1.4.0으로**: 완전한 하위 호환성. 단순히 버전 번호를 업데이트하세요.

1. **버전 업데이트**: 의존성을 `1.4.0`으로 변경
2. **Discord 알림 활성화** (선택적): `application.yml`에 Discord 웹훅 속성 추가:
   ```yaml
   peanut-butter:
     notification:
       discord:
         webhook:
           enabled: true
           url: "https://discord.com/api/webhooks/YOUR_WEBHOOK_ID/YOUR_WEBHOOK_TOKEN"
           timeout: 5000
         embed:
           color: 0x00ff00
           include-timestamp: true
           include-hostname: true
         locale: "ko"  # 한국어의 경우 "ko", 영어의 경우 "en"
   ```
3. **사용자 정의 예외 처리** (선택적): 수동 예외 보고를 위해 `DiscordExceptionHandler` 주입:
   ```kotlin
   @Service
   class MyService(private val discordExceptionHandler: DiscordExceptionHandler) {
       fun riskyOperation() {
           try {
               // 여러분의 코드
           } catch (e: Exception) {
               discordExceptionHandler.handleException(e, "사용자 정의 작업 실패")
           }
       }
   }
   ```
4. **기존 구성**: 모든 JWT, CORS, 타임존, 검증 구성은 변경되지 않음

---
*자세한 예제와 Discord 웹훅 설정 가이드는 README.md와 docs/USAGE.md를 참조하세요.*

---

## v1.3.1

### 요약
**JWT 보안 필터 향상** – 자동 permitAll() 경로 탐지, 구성 가능한 제외 패턴, 원활한 SecurityFilterChain 통합을 갖춘 포괄적인 JWT 인증 필터를 추가합니다. 이 향상은 기존 Spring Security 구성과 자동으로 통합되는 프로덕션 준비 요청 수준 JWT 필터링 기능을 제공합니다.

### 새로운 기능
- **JWT 인증 필터**: 포괄적인 요청 수준 JWT 인증 (`JwtAuthenticationFilter`)
  - 자동 Bearer 토큰 추출 및 Authorization 헤더로부터 검증
  - 역할 및 권한을 갖는 Spring Security 컨텍스트 인증 설정
  - 필터 체인 연속으로 우아한 폴백을 갖는 예외 처리
  - 유연한 경로 제외를 위한 Ant 스타일 경로 패턴 매칭
- **스마트 경로 탐지**: 기존 SecurityFilterChain 구성에서 자동 permitAll() 경로 탐지
  - **자동 탐지** (`JwtFilterProperties.autoDetectPermitAllPaths`): Spring Security 구성을 분석하고 자동으로 permitAll() 경로 제외
  - **수동 제외** (`JwtFilterProperties.excludedPaths`): 제외를 위한 추가 사용자 정의 경로 패턴
  - **통합 접근법**: 자동 탐지와 수동 구성을 동시에 지원
- **필터 구성**: 유연한 속성 기반 필터 설정 (`JwtFilterProperties`)
  - **활성화/비활성화 제어** (`enabled`): 코드 변경 없이 JWT 필터링 토글
  - **경로 제외 목록** (`excludedPaths`): Ant 스타일 패턴 (예: "/api/public/**", "/health/*")
  - **자동 탐지 토글** (`autoDetectPermitAllPaths`): 자동 permitAll() 경로 탐지 제어
- **보안 통합**: 원활한 Spring Security FilterChain 통합 (`JwtSecurityFilterChain`)
  - **자동 등록**: JWT 필터 삽입을 갖는 자체 구성 SecurityFilterChain
  - **보안 매처**: JWT 보호 엔드포인트를 위한 지능적인 요청 매칭
  - **필터 포지셔닝**: UsernamePasswordAuthenticationFilter 앞에 올바르게 위치
- **구성 속성**: 완전한 Spring Boot 구성 지원
  - **속성 메타데이터**: spring-configuration-metadata.json을 통한 전체 IDE 자동완성 지원
  - **자동 구성**: spring.factories 등록을 통한 자동 빈 와이어링
  - **조건부 로딩**: JWT 및 Security 의존성이 있을 때만 활성화

### 개선사항
- **무설정 JWT 필터링**: 기존 JWT 서비스 구현과 즉시 작동
- **자동 보안 통합**: 충돌 없이 기존 Spring Security 구성과 원활하게 통합
- **성능 최적화**: 처리 오버헤드를 최소화하는 조기 제외 확인을 갖는 효율적인 경로 매칭
- **프로덕션 준비 보안**: 보안 우선 설계 원칙을 갖는 포괄적인 오류 처리
- **유연한 구성**: 다중 구성 접근법 (자동 탐지, 수동 제외, 또는 하이브리드)
- **포괄적인 테스트**: FunSpec 스타일 테스트 프레임워크를 사용한 전체 테스트 커버리지

### 버그 수정
- 없음 (기능 중심 릴리즈)

### 브레이킹 체인지
- 없음 (v1.3.0과 완전한 하위 호환성)

### 폐지됨
- 없음

### 주요 특징
- **요청 수준 JWT 인증**과 자동 Security 컨텍스트 채우기
- **스마트 permitAll() 탐지**로 공개 엔드포인트 자동 제외
- **유연한 경로 제외 패턴**으로 와일드카드를 갖는 Ant 스타일 매칭 지원
- **무설정 통합**으로 기존 Spring Security 설정과 통합
- **프로덕션 준비 오류 처리**와 우아한 폴백 메커니즘
- **포괄적인 속성 구성**으로 모든 필터링 동작 설정

### 요구사항
- Java 17+
- SLF4J 2.0+
- (선택사항) Kotlin 1.9+ for Kotlin 확장
- (선택사항) Jakarta Bean Validation 3.0+ for 검증 기능
- (선택사항) Kotlin Coroutines 1.7.3+ for 비동기 로깅
- **JWT 필터 기능을 위한 Spring Boot 3.1.x + Spring Security 6.3.x**
- **JWT 기능을 위한 JJWT 0.12.3+** (기존 요구사항)
- **데이터베이스 리프레시 토큰 스토리지를 위한 Spring Data JPA 3.1.x** (기존 요구사항)
- **Redis 리프레시 토큰 스토리지를 위한 Spring Data Redis 3.1.x** (기존 요구사항)

### 설치
```kotlin
dependencies {
    implementation("com.github.snowykte0426:peanut-butter:1.3.1")
    
    // JWT 기능용 (필수)
    implementation("io.jsonwebtoken:jjwt-api:0.12.3")
    implementation("io.jsonwebtoken:jjwt-impl:0.12.3")
    implementation("io.jsonwebtoken:jjwt-jackson:0.12.3")
    
    // Spring Security JWT 필터 기능용 (필터에 필수)
    implementation("org.springframework.boot:spring-boot-starter-security")
    
    // Redis 리프레시 토큰 스토리지용 (선택사항)
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    
    // 데이터베이스 리프레시 토큰 스토리지용 (선택사항)
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
}
```

### 마이그레이션 가이드
**v1.3.0에서 v1.3.1로**: 완전한 하위 호환성. 단순히 버전 번호를 업데이트하세요.

1. **버전 업데이트**: 의존성을 `1.3.1`로 변경
2. **JWT 필터 활성화** (선택사항): `application.yml`에 JWT 필터 속성 추가:
   ```yaml
   peanut-butter:
     security:
       jwt:
         filter:
           enabled: true
           auto-detect-permit-all-paths: true
           excluded-paths:
             - "/api/public/**"
             - "/health/**"
             - "/actuator/**"
   ```
3. **기존 JWT 설정**: 모든 기존 JWT 구성은 변경되지 않음
4. **보안 구성**: 필터는 기존 SecurityFilterChain 구성과 자동으로 통합

---
*자세한 예제와 사용 패턴은 README.md와 docs/USAGE.md를 참조하세요.*

---

## v1.3.0

### 요약
**JWT 인증 기능 릴리즈** – 유연한 리프레시 토큰 관리, 다중 스토리지 옵션, 구성 가능한 보안 정책을 갖춘 포괄적인 JSON Web Token (JWT) 지원을 추가합니다. 이 주요 기능 추가는 Spring Boot 애플리케이션을 위한 프로덕션 준비 JWT 인증 기능을 제공합니다.

### 새로운 기능
- **JWT 토큰 관리**: 완전한 JWT 토큰 생성 및 검증 (`JwtService`, `DefaultJwtService`)
  - 사용자 정의 클레임 지원을 통한 액세스 토큰 생성
  - 구성 가능한 만료 시간을 갖는 선택적 리프레시 토큰 기능
  - 토큰 검증 및 정보 추출 (주체, 클레임, 만료)
  - 구성 가능한 시크릿 키를 사용한 안전한 HMAC-SHA256 서명
- **유연한 리프레시 토큰 스토리지**: 확장 가능한 리프레시 토큰 관리를 위한 다중 스토리지 백엔드
  - **인메모리 스토어** (`InMemoryRefreshTokenStore`): 기본값, 무설정 옵션
  - **Redis 스토어** (`RedisRefreshTokenStore`): 자동 TTL 관리를 갖는 분산 스토리지
  - **데이터베이스 스토어** (`JpaRefreshTokenStore`): JPA 엔티티 지원을 갖는 영구 스토리지
- **고급 보안 기능**: 프로덕션 준비 보안 정책
  - **토큰 로테이션**: 구성 가능한 사용된 토큰 처리를 갖는 선택적 리프레시 토큰 로테이션
  - **블랙리스트 지원**: 사용된 리프레시 토큰을 제거하거나 블랙리스트로 이동 가능
  - **자동 정리**: 모든 스토리지 타입에서 만료된 토큰의 예약된 정리
- **현재 사용자 컨텍스트**: JWT 토큰에서 원활한 사용자 정보 추출
  - **현재 사용자 프로바이더** (`CurrentUserProvider`, `JwtCurrentUserProvider`): 요청 컨텍스트에서 사용자 정보 추출
  - **사용자 리졸버 인터페이스** (`JwtUserResolver`): 사용자 정의 사용자 객체 변환 지원
  - **요청 컨텍스트 통합**: Authorization 헤더에서 자동 토큰 추출
- **포괄적인 구성**: 안전한 기본값을 갖는 속성 기반 구성
  - **애플리케이션 속성**: `peanut-butter.jwt.*` 하에서 `application.yml`을 통한 전체 구성
  - **프로그래매틱 구성**: 코드 기반 설정을 위한 `JwtConfiguration`
  - **자동 구성**: 조건부 빈 생성을 갖는 Spring Boot 자동 구성

### 개선사항
- **무설정 설정**: 개발을 위한 안전한 기본값으로 즉시 작동
- **프로덕션 준비 보안**: 구성 가능한 시크릿 키, 만료 시간, 보안 정책
- **모듈형 아키텍처**: 선택적 의존성으로 선별적 기능 사용 허용
- **포괄적인 테스트**: 단위 및 통합 테스트를 통한 전체 테스트 커버리지
- **Spring Boot 통합**: Spring Security 및 Spring Boot 생태계와의 원활한 통합
- **성능 최적화**: 최소한의 오버헤드를 갖는 효율적인 토큰 작업
- **스레드 안전 작업**: 모든 컴포넌트에서 동시 액세스 지원

### 버그 수정
- 없음 (기능 중심 릴리즈)

### 브레이킹 체인지
- 없음 (v1.2.x와 완전한 하위 호환성)

### 폐지됨
- 없음

### 주요 하이라이트
- JJWT 라이브러리 통합을 통한 **프로덕션급 JWT 구현**
- 다양한 배포 시나리오를 위한 **다중 스토리지 백엔드** (인메모리, Redis, 데이터베이스)
- 로테이션 및 블랙리스트 기능을 갖는 **고급 리프레시 토큰 관리**
- 사용자 정의 가능한 사용자 객체 매핑을 갖는 **유연한 사용자 컨텍스트 해결**
- 프로덕션 준비 보안 기본값을 갖는 **무설정 개발**
- 모든 JWT 관련 설정을 위한 **포괄적인 속성 구성**

### 요구사항
- Java 17+
- SLF4J 2.0+
- (선택적) Kotlin 확장을 위한 Kotlin 1.9+
- (선택적) 검증 기능을 위한 Jakarta Bean Validation 3.0+
- (선택적) 비동기 로깅을 위한 Kotlin Coroutines 1.7.3+
- (선택적) JWT 및 CORS 기능을 위한 Spring Boot 3.1.x + Spring Security 6.3.x
- **새로움: JWT 기능을 위한 JJWT 0.12.3+**
- **새로움: 데이터베이스 리프레시 토큰 스토리지를 위한 Spring Data JPA 3.1.x**
- **새로움: Redis 리프레시 토큰 스토리지를 위한 Spring Data Redis 3.1.x**

### 설치
```kotlin
dependencies {
    implementation("com.github.snowykte0426:peanut-butter:1.3.0")
    
    // JWT 기능용 (필수)
    implementation("io.jsonwebtoken:jjwt-api:0.12.3")
    implementation("io.jsonwebtoken:jjwt-impl:0.12.3")
    implementation("io.jsonwebtoken:jjwt-jackson:0.12.3")
    
    // Redis 리프레시 토큰 스토리지용 (선택적)
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    
    // 데이터베이스 리프레시 토큰 스토리지용 (선택적)
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
}
```

### 마이그레이션 가이드
**v1.2.x에서 v1.3.0으로**: 완전한 하위 호환성. 단순히 버전 번호를 업데이트하세요.

1. **버전 업데이트**: 의존성을 `1.3.0`으로 변경
2. **JWT 의존성 추가**: JWT 기능을 위해 JJWT 의존성 포함
3. **JWT 구성** (선택적): `application.yml`에 JWT 속성 추가:
   ```yaml
   peanut-butter:
     jwt:
       secret: "your-production-secret-key"
       access-token-expiry: "PT15M"
       refresh-token-enabled: true
   ```
4. **스토리지 백엔드 선택**: 적절한 리프레시 토큰 스토리지 선택 (인메모리, Redis, 또는 데이터베이스)
5. **사용자 리졸버 구현**: 사용자 정의 사용자 객체 해결을 위한 `JwtUserResolver` 구현 생성

---
*자세한 예제와 사용 패턴은 README.md와 docs/USAGE.md를 참조하세요.*

---

## v1.2.2

### 요약
마이너 문서 & 명확성 업데이트: 명시적인 Spring Security 모듈 버전 가이드와 의존성 명확화. 코드 변경 없음, v1.2.1에서 안전한 드롭인 업그레이드.

### 새로운 기능
- N/A (새로운 기능 없음)

### 개선사항
- **문서 명확성**: CORS 통합을 위한 명시적인 Spring Security 모듈 버전 (6.3.5) 추가.
- **의존성 가이드**: 직접 보안 모듈 vs spring-boot-starter-security 간의 대안 명확화.
- **일관성**: 현재 빌드 스크립트 compileOnly 선언과 문서 정렬.

### 버그 수정
- N/A (기능적 결함 해결 없음)

### 브레이킹 체인지
- 없음 (v1.2.1과 완전한 하위 호환성)

### 폐지됨
- 없음

### 주요 하이라이트
- 더 명확한 CORS 의존성 지침 (security-web & security-config vs starter).
- 순수하게 문서—런타임이나 API 영향 없음.

### 요구사항
- Java 17+
- SLF4J 2.0+
- (선택적) Kotlin 확장을 위한 Kotlin 1.9+
- (선택적) 검증 기능을 위한 Jakarta Bean Validation 3.0+
- (선택적) 비동기 로깅을 위한 Kotlin Coroutines 1.7.3+
- (선택적) CORS 자동 구성을 위한 Spring Boot 3.1.x + Spring Security 6.3.x

### 설치
```kotlin
dependencies {
    implementation("com.github.snowykte0426:peanut-butter:1.2.2")
}
```

### 마이그레이션 가이드
1. 의존성을 `1.2.2`로 업데이트.
2. 코드 변경 불필요.
3. (선택적) 필요시 명확화된 가이드에 따라 CORS 의존성 조정.

---
*자세한 예제와 사용 패턴은 README.md와 docs/USAGE.md를 참조하세요.*

---

## v1.2.1

### 요약
포괄적인 버그 수정과 향상된 검증 동작을 통한 라이브러리 신뢰성 향상에 중점을 둔 품질 & 안정성 개선 릴리즈.

### 새로운 기능
- N/A (유지보수 릴리즈)

### 개선사항
- **향상된 검증**: 필드 검증 어노테이션에서 에지 케이스 처리 개선
- **더 나은 에러 처리**: 모든 모듈에서 더 견고한 예외 처리
- **안정성 개선**: 테스트 중 발견된 다양한 에지 케이스 수정

### 버그 수정
- **검증 로직**: `@FieldEquals` 및 `@FieldNotEquals` 검증에서 에지 케이스 수정
- **파일 검증**: `@NotEmptyFile` 검증기에서 null 처리 이슈 해결
- **헥사고날 어노테이션**: 아키텍처 마커를 위한 어노테이션 리플렉션 동작 수정

### 브레이킹 체인지
- 없음 (v1.2.0과 완전한 하위 호환성)

### 폐지됨
- 없음

### 주요 하이라이트
- 모든 모듈에서 향상된 안정성과 신뢰성
- 더 나은 에지 케이스 처리를 통한 검증 로직 개선
- 원활한 업그레이드 경험을 보장하는 브레이킹 체인지 없음

### 요구사항
- Java 17+
- SLF4J 2.0+
- (선택적) Kotlin 확장을 위한 Kotlin 1.9+
- (선택적) 검증 기능을 위한 Jakarta Bean Validation 3.0+
- (선택적) 비동기 로깅을 위한 Kotlin Coroutines 1.7.3+
- (선택적) CORS 자동 구성을 위한 Spring Boot 3.1.x + Spring Security 6.2+

### 설치
```kotlin
dependencies {
    implementation("com.github.snowykte0426:peanut-butter:1.2.1")
}
```

### 마이그레이션 가이드
1. 의존성 버전을 `1.2.1`로 업데이트
2. 브레이킹 체인지 검토 (이 릴리즈에는 없음)
3. 폐지된 사용법 업데이트 (이 릴리즈에는 없음)

---
*자세한 예제와 사용 패턴은 README.md와 docs/USAGE.md를 참조하세요.*

---

## v1.2.0

### 요약
**CORS 구성 기능 릴리즈** – Peanut-Butter에 포괄적인 Cross-Origin Resource Sharing (CORS) 자동 구성 기능을 추가하여, 현대적인 웹 애플리케이션과 API를 위한 유연한 속성 기반 구성과 원활한 Spring Security 통합을 제공합니다.

### 새로운 기능
- **CORS 자동 구성**: Spring Security 통합을 통한 완전한 CORS 설정 (`CorsConfiguration`, `CorsProperties`)
- **유연한 속성 구성**: 활성화/비활성화 세분성을 갖는 맵 기반 HTTP 메서드 구성
  - `peanut-butter.security.cors.allowed-methods.GET: true|false`
  - 개별 제어를 통한 모든 표준 HTTP 메서드 지원
- **보안 통합**: CORS 지원을 통한 자동 구성된 `SecurityFilterChain` (`CorsSecurityFilterChain`)
- **프로덕션 준비 기본값**: 포괄적인 사용자 정의 옵션을 갖는 안전한 기본값
- **다중 구성 패턴**: 개발, 프로덕션, API 특화 CORS 정책 지원

### 개선사항
- **속성 기반 구성**: `peanut-butter.security.cors.*` 하에서 `application.yml`을 통한 모든 CORS 설정 구성 가능
- **조건부 자동 구성**: Spring Security가 클래스패스에 있을 때만 CORS 기능 활성화
- **다중 빈 지원**: 유연성을 위해 `CorsConfigurationSource`와 `SpringCorsConfiguration` 빈 모두 제공
- **포괄적인 문서**: 개발, 프로덕션, API 예제를 포함한 완전한 사용 가이드
- **향상된 기능 매트릭스**: 모든 기능 조합에서 CORS 기능을 포함하도록 문서 업데이트

### 버그 수정
- 없음 (기능 중심 릴리즈)

### 브레이킹 체인지
- 없음 (v1.1.x와 완전한 하위 호환성)

### 폐지됨
- 없음

### 주요 하이라이트
- Spring Security 애플리케이션을 위한 무설정 CORS
- 속성 맵을 통한 세분화된 HTTP 메서드 제어
- 다중 사전 구성된 보안 패턴 (개발, 프로덕션, API 전용)
- 기존 Spring Security 구성과의 원활한 통합
- Kotest 프레임워크를 통한 포괄적인 테스트 커버리지

### 요구사항
- Java 17+
- SLF4J 2.0+
- (선택적) Kotlin 확장을 위한 Kotlin 1.9+
- (선택적) 검증 기능을 위한 Jakarta Bean Validation 3.0+
- (선택적) 비동기 로깅을 위한 Kotlin Coroutines 1.7.3+
- (선택적) CORS 자동 구성을 위한 Spring Boot 3.1.x + Spring Security 6.2+

### 설치
```kotlin
dependencies {
    implementation("com.github.snowykte0426:peanut-butter:1.2.0")
    
    // CORS 기능용
    implementation("org.springframework.boot:spring-boot-starter-security:3.1.5")
}
```

### 마이그레이션 가이드
**v1.1.4에서 v1.2.0으로**: 완전한 하위 호환성. 단순히 버전 번호를 업데이트하세요.

1. **버전 업데이트**: 의존성을 `1.2.0`으로 변경
2. **선택적 CORS 설정**: CORS 자동 구성을 활성화하기 위해 Spring Security 의존성 추가
3. **CORS 구성** (선택적): `application.yml`에 CORS 속성 추가:
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

### 요약
네임스페이스 & 빌드 품질 업데이트로 모든 공용 API를 단일 패키지 루트 아래로 통합하고 사소한 빌드/리포트 개선을 추가. 하나의 작은 브레이킹 체인지 (import 경로 업데이트) – 동작이나 API 표면 변경 없음.

### 새로운 기능
- **유틸리티 Gradle 작업**: 현재 프로젝트 버전을 출력하는 `printVersion` 작업.

### 개선사항
- **통합 네임스페이스**: 일관된 import 구조를 위해 모든 모듈을 `com.github.snowykte0426.peanut.butter.*`로 이동.
- **Jar 매니페스트 메타데이터**: 출처 & 도구를 위해 `Implementation-Title`, `Implementation-Version`, 벤더 정보 추가.
- **향상된 테스트 로깅**: JUnit 테스트 작업이 이제 전체 스택 트레이스와 함께 통과/건너뜀/실패를 리포트.
- **명시적 JitPack 리포지토리**: 구성 미러링 시 명확성을 위해 빌드 스크립트에 추가.

### 버그 수정
- 없음 (이 릴리즈에서 기능적 결함 해결 없음).

### 브레이킹 체인지
- **패키지 이름 변경**: 이전 루트들 (예: `com.github.snowykte0426.logging`, `.timezone`, `.validation`, `.hexagonal.annotation`)이 `com.github.snowykte0426.peanut.butter.*`로 대체. 그에 따라 import 업데이트. (마이그레이션 가이드 참조.)

### 폐지됨
- 없음.

### 주요 하이라이트
- 단일하고 예측 가능한 패키지 계층.
- 동작 변경 없음 – 순수한 구조적 & 빌드 투명성 개선.
- 풍부한 테스트 로깅을 통한 더 빠른 진단.

### 요구사항
- Java 17+
- SLF4J 2.0+
- (선택적) Kotlin 확장을 위한 Kotlin 1.9+
- (선택적) 검증 기능을 위한 Jakarta Bean Validation 3.0+
- (선택적) 비동기 로깅을 위한 Kotlin Coroutines 1.7.3+
- (선택적) 자동 구성 / `@Adapter`를 위한 Spring Boot 3.1.x

### 설치
```kotlin
dependencies {
    implementation("com.github.snowykte0426:peanut-butter:1.1.4")
}
```

### 마이그레이션 가이드
1. **버전 업데이트**: 의존성을 `1.1.4`로 변경.
2. **Import 업데이트**: 찾기 & 바꾸기 실행:
   - `com.github.snowykte0426.logging` → `com.github.snowykte0426.peanut.butter.logging`
   - `com.github.snowykte0426.timezone` → `com.github.snowykte0426.peanut.butter.timezone`
   - `com.github.snowykte0426.validation` → `com.github.snowykte0426.peanut.butter.validation`
   - `com.github.snowykte0426.hexagonal.annotation` → `com.github.snowykte0426.peanut.butter.hexagonal.annotation`
3. **재빌드 & 테스트 실행**: import 외에는 코드 변경 예상되지 않음.
4. (선택적) CI에서 검증을 위해 `./gradlew printVersion` 활용.

---

## v1.1.3

### 요약
아키텍처 & 검증 강화 릴리즈 – 가벼운 헥사고날 아키텍처 메타 어노테이션 (`@Port`, `@Adapter`, `PortDirection`)과 새로운 파일 업로드 검증 제약 (`@NotEmptyFile`) 추가. 브레이킹 API 변경 없음.

### 새로운 기능
- **헥사고날 아키텍처 어노테이션**:
  - `@Port(direction = INBOUND|OUTBOUND)` – 도메인 포트 인터페이스를 위한 소스 보존 마커 (가벼움 유지; 런타임 비용 없음)
  - `@Adapter(direction = INBOUND|OUTBOUND)` – Spring 어댑터를 위한 런타임 컴포넌트 스테레오타입 (`@Component`로 메타 어노테이션됨), 명확성 & 스캔 개선
  - `PortDirection` 열거형 (INBOUND, OUTBOUND)
- **파일 업로드 검증**:
  - `@NotEmptyFile` – 비어있지 않은 업로드를 보장하는 `MultipartFile` 필드를 위한 Jakarta Bean Validation 제약 (`NotEmptyFileValidator`)

### 개선사항
- 새로운 어노테이션 & 예제를 포함하도록 문서 업데이트 (README / USAGE)
- 프로젝트 버전을 1.1.3으로 범프; 의존성 가이드를 최소한으로 유지 (필수 의존성 추가 없음)

### 버그 수정
- N/A (1.1.2 이후 보고된 이슈 없음)

### 브레이킹 체인지
- 없음 (1.1.x와 완전한 하위 호환성)

### 폐지됨
- 없음

### 마이그레이션 가이드 (1.1.2 → 1.1.3)
1. 의존성 버전을 `1.1.3`으로 업데이트
2. (선택적) 더 명확한 아키텍처 경계를 위해 어댑터 & 포트 어노테이션 사용 시작
3. (선택적) 멀티파트 업로드 DTO 필드에 `@NotEmptyFile` 적용

코드 변경 불필요; 모든 기존 API는 안정적으로 유지됩니다.

---

## v1.1.2

### 요약
**Spring Boot 3.x 호환성 수정** - Spring Boot 3.x 환경에서 애플리케이션 시작 실패를 일으킨 `@ConstructorBinding` 어노테이션 이슈 해결.

### 버그 수정
- **Spring Boot 3.x 호환성 수정**: 시작 오류 해결을 위해 `TimeZoneProperties` 클래스에서 `@ConstructorBinding` 어노테이션 제거: `com.github.snowykte0426.timezone.TimeZoneProperties declares @ConstructorBinding on a no-args constructor`
- **구성 바인딩 업데이트**: 기본값을 갖는 데이터 클래스를 위한 Spring Boot 3.x 호환 속성 바인딩 방식으로 변경

### 개선사항
- **향상된 Spring Boot 호환성**: 이제 Spring Boot 3.1.x - 3.5.x와 완전 호환
- **간소화된 속성 구성**: 더 깔끔한 코드를 위해 불필요한 어노테이션 제거

### 브레이킹 체인지
- 없음 (완전한 하위 호환성)

### 폐지됨
- 없음

### 주요 기능
- v1.1.1의 모든 기능이 변경 없이 유지
- SLF4J API와 Kotlin stdlib 외에 **강제 의존성 없음**
- 선택적 기능 사용을 허용하는 **모듈형 설계**
- **프레임워크 무관** - Spring Boot와 함께 또는 없이도 작동

### 요구사항
- **최소**: Java 17+ 및 SLF4J 2.0+
- **Kotlin 기능용**: Kotlin 1.9+
- **검증용**: Jakarta Bean Validation 3.0+
- **코루틴 로깅용**: Kotlin Coroutines 1.7.3+
- **Spring Boot 통합용**: Spring Boot 3.1.x - 3.5.x

### 설치
```kotlin
dependencies {
    implementation("com.github.snowykte0426:peanut-butter:1.1.2")
}
```

### 마이그레이션 가이드
**v1.1.1에서 v1.1.2로**: 단순히 버전 번호를 업데이트하세요. 코드 변경 불필요.

```kotlin
// 이전
implementation("com.github.snowykte0426:peanut-butter:1.1.1")

// 이후
implementation("com.github.snowykte0426:peanut-butter:1.1.2")
```

---

## v1.1.1

### 요약
**의존성 최소화 릴리즈** - Peanut-Butter를 **강제 의존성이 없는** 진정으로 가볍고 모듈형 라이브러리로 변환. 사용자는 이제 필수 의존성만 받고 필요에 따라 선택적 기능을 추가할 수 있습니다.

### 주요 개선사항
- **강제 의존성 없음**: 핵심 의존성을 SLF4J API + Kotlin stdlib만으로 감소 (~20+ 전이 의존성에서)
- **모듈형 아키텍처**: 기본 로깅 외의 모든 기능이 이제 `compileOnly` 의존성으로 선택적
- **선택적 기능 사용**: 사용자가 자신의 프로젝트에 포함할 기능을 정확히 선택 가능
- **향상된 문서**: 포괄적인 의존성 관리 가이드와 기능 매트릭스

### 개선사항
- **DEPENDENCY_GUIDE.md**: 의존성 관리를 위한 완전한 가이드
- **업데이트된 README.md**: 모듈형 설계 설명과 함께 명확한 기능-의존성 매핑
- **업데이트된 USAGE.md**: 다양한 구성을 위한 기능 가용성 매트릭스와 설정 가이드
- **모듈형 의존성 구조**: 핵심 의존성과 선택적 기능 분리

### 버그 수정
- 없음 (최적화 중심 릴리즈)

### 브레이킹 체인지
- 없음 (완전한 하위 호환성)

### 폐지됨
- 없음

### 주요 기능
- SLF4J API와 Kotlin stdlib 외에 **강제 의존성 없음**
- 선택적 기능 사용을 허용하는 **모듈형 설계**
- 최소 설치를 위한 **90% 의존성 감소** (~2MB vs 15-20MB)
- 로깅 구현 선택에서 **개발자 선택권**
- **프레임워크 무관** - Spring Boot와 함께 또는 없이도 작동

### 요구사항
- **최소**: Java 17+ 및 SLF4J 2.0+
- **Kotlin 기능용**: Kotlin 1.9+
- **검증용**: Jakarta Bean Validation 3.0+
- **코루틴 로깅용**: Kotlin Coroutines 1.7.3+
- **Spring Boot 통합용**: Spring Boot 3.1.x

### 설치
```kotlin
dependencies {
    implementation("com.github.snowykte0426:peanut-butter:1.1.1")
}
```

### 마이그레이션 가이드
**마이그레이션 불필요** - 이는 완전한 하위 호환성 최적화입니다:

1. 버전을 `1.1.1`로 업데이트
2. **검증 기능을 사용하는 경우**, Jakarta Validation API 추가
3. **코루틴 로깅을 사용하는 경우**, Kotlin Coroutines 추가
4. **Spring Boot 자동 구성을 사용하는 경우**, Spring Boot Starter 추가
5. 아직 없다면 **로깅 구현** (Logback, Log4j2 등) 추가

---

## v1.1.0

### 요약
애플리케이션 전체 기본 시간대 제어, 변환 유틸리티, 임시 시간대 컨텍스트 실행, Spring Boot 자동 구성 지원을 가능하게 하는 자동 TimeZone 구성 & 유틸리티 기능 추가.

### 새로운 기능
- **자동 TimeZone 구성**: 시작 시 JVM 기본 시간대 설정 (`TimeZoneAutoConfiguration`, `TimeZoneProperties`)
- **구성 어노테이션**: 원할 때 명시적 활성화를 위한 `@EnableAutomaticTimeZone`
- **프로그래매틱 제어**: `TimeZoneInitializer`를 통한 런타임 시간대 전환 (`changeTimeZone()`, `getCurrentTimeZone()`)
- **지원되는 열거형**: `SupportedTimeZone` (UTC, KST, JST, GMT, WET, BST, CET, WEST, CEST, EET, EEST, MST, PT, ET)
- **확장 유틸리티**:
  - 현재 시간 검색: `Any.getCurrentTimeIn(...)`
  - 변환: `LocalDateTime.inTimeZone(...)`, `ZonedDateTime.convertToTimeZone(...)`
  - 기본 시간대 비교: `Any.isCurrentTimeZone(...)`
  - 임시 컨텍스트 실행: `Any.withTimeZone(...)`
  - 표시 이름 조회: `Any.getCurrentTimeZoneDisplayName()`

### 개선사항
- 속성 제어: 세밀한 동작 & 로깅을 위한 `peanut-butter.timezone.enabled`, `zone`, `enable-logging`
- 더 명확한 오류 신호: 지원되지 않는 시간대는 설명적인 `IllegalArgumentException` 발생
- 로깅 통합: 성공적인 초기화/변경은 `logInfo` 사용, 실패는 `logWarn` 사용

### 버그 수정
- 없음 (기능 중심 릴리즈)

### 브레이킹 체인지
- 없음 (기존 공용 API에 변경 없음)

### 폐지됨
- 없음

### 주요 기능
- 글로벌 기본 TimeZone 자동 설정 & 런타임 전환
- 안전한 임시 TimeZone 컨텍스트 (`withTimeZone`)
- 열거형 기반 검증 & 표준화된 시간대 식별자
- 기존 로깅 유틸리티를 통한 내장 관찰 가능성

### 요구사항
- Java 17+
- Kotlin 1.9+
- Spring Boot 3.1.x (자동 구성 사용 시)

### 설치
```kotlin
dependencies {
    implementation("com.github.snowykte0426:peanut-butter:1.1.0")
}
```

### 마이그레이션 가이드
1. 의존성 버전 1.0.2 → 1.1.0으로 범프
2. (선택적) application.yml 구성 추가:
   ```yaml
   peanut-butter:
     timezone:
       enabled: true
       zone: KST
       enable-logging: true
   ```
3. 런타임 변경을 위해 `TimeZoneInitializer` 주입 (예: `changeTimeZone("UTC")`)
4. 적절한 곳에서 임시 시간대 처리를 `withTimeZone(...)`로 교체

---

## v1.0.2

### 요약
peanut-butter 라이브러리에 **코루틴 인식 로깅** 기능을 추가했습니다. 이 릴리즈는 Kotlin 코루틴을 위한 포괄적인 비동기 로깅 지원을 도입하여, 상관관계 ID 추적, 재시도 메커니즘, 병렬 실행 모니터링과 같은 고급 기능을 갖춘 스레드 안전 로깅을 가능하게 합니다.

### 새로운 기능
- **코루틴 로깅**: Kotlin 코루틴을 위한 완전한 비동기 로깅 지원
  - **비동기 로깅**: `logInfoAsync()`, `logDebugAsync()`, `logErrorAsync()` 등 - 코루틴에서 스레드 안전 로깅
  - **비동기 성능**: `logExecutionTimeAsync()`, `logMethodExecutionAsync()` - suspend 함수를 위한 성능 추적
  - **재시도 메커니즘**: `retryWithLogging()` - 포괄적인 로깅과 함께 지수 백오프
  - **상관관계 추적**: `withLoggingContext()` - 분산 추적을 위한 상관관계 ID를 통한 향상된 로깅
  - **병렬 실행**: `executeParallelWithLogging()` - 개별 타이밍으로 병렬 작업 모니터링
  - **컨텍스트 로깅**: `logCoroutineContext()` - 코루틴 컨텍스트 정보 디버그

### 개선사항
- **코루틴 지원**: 비동기 기능을 위한 Kotlin Coroutines 1.7.3 의존성 추가
- **MDC 지원**: 상관관계 ID 추적을 위한 Mapped Diagnostic Context 통합
- **향상된 테스트**: 포괄적인 코루틴 테스트를 위한 kotlinx-coroutines-test 추가

### 버그 수정
없음 - 기능 확장 릴리즈입니다

### 브레이킹 체인지
없음 - v1.0.1과 완전한 하위 호환성

### 폐지됨
없음

### 주요 기능
- 스레드 안전성과 컨텍스트 보존을 갖춘 **🚀 프로덕션 준비 코루틴 로깅**
- 상관관계 ID 추적을 통한 **🔍 분산 추적 지원**
- 지수 백오프와 상세한 로깅을 통한 **⚡ 지능적 재시도 메커니즘**
- 개별 타이밍과 오류 추적을 통한 **📊 병렬 작업 모니터링**

### 요구사항
- Java 17+
- Kotlin 1.9+ (Kotlin 기능용)
- SLF4J 2.0+ (로깅 기능용)
- Jakarta Bean Validation API 3.0+ (검증 기능용)
- **Kotlin Coroutines 1.7.3+** (코루틴 로깅 기능용)

### 설치
```kotlin
dependencies {
    implementation("com.github.snowykte0426:peanut-butter:1.0.2")
}
```

### 마이그레이션 가이드
v1.0.1에서 마이그레이션 불필요 - 단순히 버전 번호를 업데이트하세요. 모든 기존 코드는 변경 없이 계속 작동합니다.

1. 의존성 버전을 1.0.2로 업데이트
2. 비동기 코드에서 새로운 코루틴 로깅 기능 사용 시작
3. suspend 함수에서 기존 비동기 로깅을 새로운 코루틴 인식 메서드로 마이그레이션 고려 (`logInfo()` 대신 `logInfoAsync()`)

### 사용 예제

#### 기본 코루틴 로깅
```kotlin
suspend fun processUser(userData: UserData) {
    logInfoAsync("Processing user: {}", userData.username)
    
    val result = logExecutionTimeAsync("User processing") {
        userService.processAsync(userData)
    }
}
```

#### 재시도를 통한 복원력 있는 작업
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

#### 상관관계 ID를 통한 분산 추적
```kotlin
suspend fun handleRequest(requestId: String) {
    withLoggingContext("req-$requestId") {
        logInfoAsync("Starting request processing")
        // 이 블록의 모든 로그는 상관관계 ID를 포함합니다
        processRequest()
    }
}
```

