# Peanut-Butter

**Language**: [English](../../README.md)

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java Version](https://img.shields.io/badge/Java-17+-blue.svg)](https://openjdk.java.net/)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.25-purple.svg)](https://kotlinlang.org/)

Peanut-Butter는 JVM (Java & Kotlin) 프로젝트를 위한 **경량화, 모듈형** 유틸리티 라이브러리입니다. 검증, 구조화된 로깅 (동기 & 코루틴), 성능 계측, 애플리케이션 차원의 타임존 관리 (Spring Boot 자동 구성 포함), CORS 구성, JWT 인증, Discord 웹훅 알림, 그리고 아키텍처 명확성 도우미 (헥사고날 어노테이션)를 위한 프로덕션 지향적 빌딩 블록을 제공합니다.

## 기능 개요

| 영역 | 기능 | 필요한 의존성 |
|------|------|-------------|
| 필드 검증 | 클래스 수준 동등성/부동등성 (`@FieldEquals`, `@FieldNotEquals`), 멀티파트 파일 비어있지 않음 (`@NotEmptyFile`) | Jakarta Bean Validation API (선택사항) |
| 로깅 (동기) | 로거 헬퍼, 구조화 및 조건부 로깅, 성능 타이머 | SLF4J API만 |
| 로깅 (코루틴) | 비동기 안전 로깅, suspend 함수를 위한 실행 타이밍, 상관관계 (MDC) 컨텍스트 | Kotlin Coroutines (선택사항) |
| 타임존 관리 | Spring Boot 자동 구성, 런타임 스위칭, 안전한 임시 컨텍스트 | Spring Boot Starter (선택사항) |
| CORS 구성 | Spring Security CORS 자동 구성, 유연한 속성 기반 설정 | Spring (Web), Spring Security (선택사항) |
| JWT 인증 | 토큰 생성/검증, 리프레시 토큰 관리, 현재 사용자 컨텍스트, JWT 인증 필터 | JJWT, Spring Boot + Spring Security (선택사항) |
| Discord 알림 | 서버 생명주기 및 예외 알림, 다국어 지원, 풍부한 임베드 포맷팅 | Spring Web (RestTemplate용), Spring Boot (선택사항) |
| 성능 헬퍼 | 실행/메서드 타이밍, 메모리 사용량 스냅샷 로깅 | SLF4J API만 |
| 헥사고날 아키텍처 | `@Port`, `@Adapter`, `PortDirection` 의미적 마커 | (선택사항) Spring (`@Adapter`용) |

## 설치

### Gradle (Kotlin DSL)
```kotlin
dependencies {
    implementation("com.github.snowykte0426:peanut-butter:1.4.0")
    
    // 선택사항: 필요한 것만 추가
    // 검증 기능 (제약 조건 + 파일 업로드)
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")
    implementation("org.hibernate.validator:hibernate-validator:8.0.1.Final")
    implementation("org.glassfish:jakarta.el:4.0.2") // EL (Hibernate Validator에서 필요)
    
    // 코루틴 로깅 기능
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    
    // Spring Boot 자동 구성 & @Adapter 스테레오타입
    implementation("org.springframework.boot:spring-boot-starter:3.1.5")
    
    // CORS 구성 (선택사항) - 라이브러리 compileOnly 버전과 매치
    compileOnly("org.springframework.security:spring-security-web:6.3.5")
    compileOnly("org.springframework.security:spring-security-config:6.3.5")
    // (또는 애플리케이션에서: implementation("org.springframework.boot:spring-boot-starter-security:3.1.5"))
    
    // JWT 인증 (선택사항)
    implementation("io.jsonwebtoken:jjwt-api:0.12.3")
    implementation("io.jsonwebtoken:jjwt-impl:0.12.3")
    implementation("io.jsonwebtoken:jjwt-jackson:0.12.3")
    implementation("jakarta.servlet:jakarta.servlet-api:6.0.0") // 현재 사용자 프로바이더용
    // 선택사항: 리프레시 토큰 스토리지 백엔드
    // implementation("org.springframework.boot:spring-boot-starter-data-redis") // Redis 스토리지
    // implementation("org.springframework.boot:spring-boot-starter-data-jpa") // 데이터베이스 스토리지
    
    // 로깅 구현체 선택
    implementation("ch.qos.logback:logback-classic:1.5.13") // 또는 log4j2 등
}
```

### Maven
```xml
<dependency>
  <groupId>com.github.snowykte0426</groupId>
  <artifactId>peanut-butter</artifactId>
  <version>1.4.0</version>
</dependency>

<!-- 선택사항: 검증 -->
<dependency>
  <groupId>jakarta.validation</groupId>
  <artifactId>jakarta.validation-api</artifactId>
  <version>3.0.2</version>
</dependency>
<dependency>
  <groupId>org.hibernate.validator</groupId>
  <artifactId>hibernate-validator</artifactId>
  <version>8.0.1.Final</version>
</dependency>
<dependency>
  <groupId>org.glassfish</groupId>
  <artifactId>jakarta.el</artifactId>
  <version>4.0.2</version>
</dependency>

<!-- 선택사항: 코루틴 -->
<dependency>
  <groupId>org.jetbrains.kotlinx</groupId>
  <artifactId>kotlinx-coroutines-core</artifactId>
  <version>1.7.3</version>
</dependency>

<!-- 선택사항: Spring Boot + Adapter 스테레오타입 -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter</artifactId>
  <version>3.1.5</version>
</dependency>

<!-- 선택사항: CORS (Spring Security 모듈) -->
<dependency>
  <groupId>org.springframework.security</groupId>
  <artifactId>spring-security-web</artifactId>
  <version>6.3.5</version>
  <scope>provided</scope>
</dependency>
<dependency>
  <groupId>org.springframework.security</groupId>
  <artifactId>spring-security-config</artifactId>
  <version>6.3.5</version>
  <scope>provided</scope>
</dependency>
<!-- 또는 간단히 Boot starter security 사용 (Boot 버전과 정렬 확인) -->
<!--
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-security</artifactId>
  <version>3.1.5</version>
</dependency>
-->

<!-- 선택사항: JWT 인증 -->
<dependency>
  <groupId>io.jsonwebtoken</groupId>
  <artifactId>jjwt-api</artifactId>
  <version>0.12.3</version>
</dependency>
<dependency>
  <groupId>io.jsonwebtoken</groupId>
  <artifactId>jjwt-impl</artifactId>
  <version>0.12.3</version>
</dependency>
<dependency>
  <groupId>io.jsonwebtoken</groupId>
  <artifactId>jjwt-jackson</artifactId>
  <version>0.12.3</version>
</dependency>
<dependency>
  <groupId>jakarta.servlet</groupId>
  <artifactId>jakarta.servlet-api</artifactId>
  <version>6.0.0</version>
</dependency>
<!-- 선택사항: 리프레시 토큰 스토리지 백엔드 -->
<!-- 
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-redis</artifactId>
  <version>3.1.5</version>
</dependency>
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-jpa</artifactId>
  <version>3.1.5</version>
</dependency>
-->

<!-- 로깅 구현체 -->
<dependency>
  <groupId>ch.qos.logback</groupId>
  <artifactId>logback-classic</artifactId>
  <version>1.5.13</version>
</dependency>
```

## 최소 의존성

이 라이브러리는 **의존성 최소화**를 염두에 두고 설계되었습니다:

- **핵심 요구사항**: SLF4J API (2.0.9)만
- **다른 모든 의존성은 선택사항**이며 해당 기능을 사용할 때만 로드됩니다
- **강제 로깅 구현체 없음** - 선호하는 SLF4J 구현체를 선택하세요
- **모듈형 설계** - 필요한 기능만 사용하세요

### 기본적으로 포함된 것:
- 기본 로깅 확장 (SLF4J API만)
- 성능 타이밍 유틸리티
- 핵심 타임존 열거형 및 확장
- 헥사고날 포트/어댑터 어노테이션 (`@Adapter` 컴포넌트 등록을 위한 Spring 제외하고는 런타임 의존성 없음)

### 추가 의존성이 필요한 것:
- 검증 어노테이션 → Jakarta Validation API (+ 구현체)
- 파일 업로드 제약 조건 (`@NotEmptyFile`) → Jakarta Validation + Spring Web `MultipartFile`
- 코루틴 로깅 → Kotlin Coroutines
- Spring Boot 자동 구성 → Spring Boot Starter
- CORS 구성 → Spring Web + Spring Security (web + config) 모듈 (또는 Boot starter security)
- JWT 인증 → JJWT + Jakarta Servlet (현재 사용자 프로바이더용) + 선택적 스토리지 백엔드
- Discord 알림 → Spring Web (RestTemplate HTTP 클라이언트용)
- 실제 로깅 → 선택한 SLF4J 구현체

## 문서

- 사용 가이드: [docs/USAGE.md](docs/USAGE.md)
- 릴리즈 노트: [docs/RELEASE_NOTES.md](docs/RELEASE_NOTES.md)
- API (Javadoc): https://javadoc.jitpack.io/com/github/snowykte0426/peanut-butter/latest/javadoc/
- 보안 정책: [SECURITY.md](SECURITY.md)

## 요구사항

- **최소**: Java 17+ 및 SLF4J 2.0+
- **Kotlin 기능용**: Kotlin 1.9+
- **검증용**: Jakarta Bean Validation 3.0+
- **코루틴 로깅용**: Kotlin Coroutines 1.7.3+
- **Spring 통합용**: Spring Framework / Boot (자동 구성용) + Spring Security (CORS용) 사용된 API 수준과 호환되는 버전 (Spring Boot 3.1.5 + Spring Security 6.3.x API로 테스트됨)

## 버전 지원 정책
명시적으로 유지보수 및 보안 업데이트에서 제외된 **`1.1.1`을 제외한** 모든 출시된 버전이 지원됩니다.

| 버전 | 지원 |
|-----|------|
| 1.1.1 | 지원 안됨 (제외) |
| 기타 출시된 버전 | 지원됨 |

1.1.1을 사용 중이라면, 수정 사항 및 보안 업데이트를 계속 받기 위해 최신 패치 또는 마이너 릴리즈로 업그레이드하세요.

## 보안
취약점 보고 및 전체 공개 프로세스는 전용 [보안 정책](SECURITY.md)을 참조하세요.