package com.github.snowykte0426.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @FieldEquals와 @FieldNotEquals 어노테이션의 테스트 클래스입니다.
 * 
 * @author Kim Tae Eun
 */
public class FieldValidationTest {
    
    private Validator validator;
    
    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    
    @Test
    void testFieldEquals_SameValues_ShouldPass() {
        TestUserEquals user = new TestUserEquals();
        user.phoneNumber1 = "010-1234-5678";
        user.phoneNumber2 = "010-1234-5678";
        user.email1 = "test@example.com";
        user.email2 = "test@example.com";
        
        Set<ConstraintViolation<TestUserEquals>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "같은 값을 가진 필드들은 검증을 통과해야 합니다");
    }
    
    @Test
    void testFieldEquals_DifferentValues_ShouldFail() {
        TestUserEquals user = new TestUserEquals();
        user.phoneNumber1 = "010-1234-5678";
        user.phoneNumber2 = "010-8765-4321"; // 다른 값
        user.email1 = "test@example.com";
        user.email2 = "test@example.com";
        
        Set<ConstraintViolation<TestUserEquals>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "다른 값을 가진 필드들은 검증에 실패해야 합니다");
        
        // phone 그룹의 필드들에서 오류가 발생했는지 확인
        boolean hasPhoneViolation = violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().contains("phoneNumber"));
        assertTrue(hasPhoneViolation, "전화번호 필드에서 검증 오류가 발생해야 합니다");
    }
    
    @Test
    void testFieldNotEquals_DifferentValues_ShouldPass() {
        TestUserNotEquals user = new TestUserNotEquals();
        user.username = "john_doe";
        user.password = "secret123";
        
        Set<ConstraintViolation<TestUserNotEquals>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "서로 다른 값을 가진 필드들은 검증을 통과해야 합니다");
    }
    
    @Test
    void testFieldNotEquals_SameValues_ShouldFail() {
        TestUserNotEquals user = new TestUserNotEquals();
        user.username = "john_doe";
        user.password = "john_doe"; // username과 같은 값
        
        Set<ConstraintViolation<TestUserNotEquals>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "같은 값을 가진 필드들은 검증에 실패해야 합니다");
    }
    
    @Test
    void testFieldEquals_NullValues_ShouldPass() {
        TestUserEquals user = new TestUserEquals();
        user.phoneNumber1 = null;
        user.phoneNumber2 = null;
        user.email1 = "test@example.com";
        user.email2 = "test@example.com";
        
        Set<ConstraintViolation<TestUserEquals>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "null 값들도 동일하므로 검증을 통과해야 합니다");
    }
    
    /**
     * @FieldEquals 테스트용 클래스
     */
    @FieldEquals(fields = {"phoneNumber1", "phoneNumber2"}, message = "전화번호가 일치하지 않습니다")
    @FieldEquals(fields = {"email1", "email2"}, message = "이메일이 일치하지 않습니다")
    static class TestUserEquals {
        String phoneNumber1;
        String phoneNumber2;
        String email1;
        String email2;
    }
    
    /**
     * @FieldNotEquals 테스트용 클래스
     */
    @FieldNotEquals(fields = {"username", "password"}, message = "사용자명과 비밀번호는 달라야 합니다")
    static class TestUserNotEquals {
        String username;
        String password;
    }
}