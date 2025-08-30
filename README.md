# Peanut-Butter 🥜🧈

Java 개발을 위한 종합 유틸리티 라이브러리입니다. 필수 도구와 헬퍼 함수들을 제공합니다.

## 현재 제공 기능

### 필드 검증 어노테이션

Jakarta Bean Validation을 기반으로 한 커스텀 검증 어노테이션들을 제공합니다.

#### 📋 @FieldEquals
지정된 필드들이 동일한 값을 가져야 함을 검증합니다.

```java
@FieldEquals(fields = {"password", "passwordConfirm"}, message = "비밀번호가 일치하지 않습니다")
@FieldEquals(fields = {"email", "emailConfirm"}, message = "이메일이 일치하지 않습니다")
public class UserForm {
    private String password;
    private String passwordConfirm;
    private String email;
    private String emailConfirm;
}
```

#### 📋 @FieldNotEquals
지정된 필드들이 서로 다른 값을 가져야 함을 검증합니다.

```java
@FieldNotEquals(fields = {"username", "password"}, message = "사용자명과 비밀번호는 달라야 합니다")
public class SecurityForm {
    private String username;
    private String password;
}
```

### 주요 특징

- **필드 기반 검증**: `fields` 배열로 검증할 필드들을 명시적으로 지정
- **다중 검증**: 하나의 클래스에 여러 개의 검증 규칙 적용 가능
- **커스텀 메시지**: `message` 속성으로 검증 실패 시 표시할 메시지 커스터마이징
- **Jakarta Bean Validation 호환**: 표준 검증 프레임워크와 완전 호환
- **상속 지원**: 부모 클래스의 필드도 검증에 포함
- **null 안전**: null 값에 대한 안전한 처리

## 설치

### Gradle (build.gradle.kts)

```kotlin
dependencies {
    implementation("com.github.snowykte0426:peanut-butter:1.0.0")
    
    // Jakarta Bean Validation 의존성
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")
    implementation("org.hibernate.validator:hibernate-validator:8.0.1.Final")
    implementation("org.glassfish:jakarta.el:4.0.2")
}
```

### JitPack을 통한 설치

```kotlin
repositories {
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.github.snowykte0426:peanut-butter:1.0.0")
}
```

## 사용법

### 기본 사용법

```java
import com.github.snowykte0426.validation.FieldEquals;
import com.github.snowykte0426.validation.FieldNotEquals;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

public class Example {
    public static void main(String[] args) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        
        MyForm form = new MyForm();
        form.setPassword("password123");
        form.setPasswordConfirm("password123");
        form.setUsername("user");
        
        Set<ConstraintViolation<MyForm>> violations = validator.validate(form);
        
        if (violations.isEmpty()) {
            System.out.println("검증 성공!");
        } else {
            violations.forEach(v -> 
                System.out.println(v.getPropertyPath() + ": " + v.getMessage())
            );
        }
    }
}

@FieldEquals(fields = {"password", "passwordConfirm"}, message = "비밀번호가 일치하지 않습니다")
@FieldNotEquals(fields = {"username", "password"}, message = "사용자명과 비밀번호는 달라야 합니다")
class MyForm {
    private String password;
    private String passwordConfirm;
    private String username;
    
    // getters and setters...
}
```

### Spring Boot와 함께 사용

```java
@RestController
public class UserController {
    
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody UserRegistrationDto dto) {
        // 검증이 자동으로 수행됨
        return ResponseEntity.ok("등록 성공");
    }
}

@FieldEquals(fields = {"email", "emailConfirm"}, message = "이메일이 일치하지 않습니다")
class UserRegistrationDto {
    private String email;
    private String emailConfirm;
    
    // getters and setters...
}
```

## 요구사항

- Java 17 이상
- Jakarta Bean Validation API 3.0+

## 라이선스

MIT License - 자세한 내용은 [LICENSE](LICENSE) 파일을 참조하세요.

## 기여하기

이슈 리포트, 기능 요청, Pull Request는 언제나 환영합니다!

## 작성자

**Kim Tae Eun** (snowykte0426)
- Email: snowykte0426@naver.com
- GitHub: [@snowykte0426](https://github.com/snowykte0426)
