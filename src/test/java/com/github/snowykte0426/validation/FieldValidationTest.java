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
 * Test class for @FieldEquals and @FieldNotEquals annotations.
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
        assertTrue(violations.isEmpty(), "Fields with same values should pass validation");
    }
    
    @Test
    void testFieldEquals_DifferentValues_ShouldFail() {
        TestUserEquals user = new TestUserEquals();
        user.phoneNumber1 = "010-1234-5678";
        user.phoneNumber2 = "010-8765-4321"; // different value
        user.email1 = "test@example.com";
        user.email2 = "test@example.com";
        
        Set<ConstraintViolation<TestUserEquals>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Fields with different values should fail validation");

        boolean hasPhoneViolation = violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().contains("phoneNumber"));
        assertTrue(hasPhoneViolation, "Validation error should occur for phone number fields");
    }
    
    @Test
    void testFieldNotEquals_DifferentValues_ShouldPass() {
        TestUserNotEquals user = new TestUserNotEquals();
        user.username = "john_doe";
        user.password = "secret123";
        
        Set<ConstraintViolation<TestUserNotEquals>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "Fields with different values should pass validation");
    }
    
    @Test
    void testFieldNotEquals_SameValues_ShouldFail() {
        TestUserNotEquals user = new TestUserNotEquals();
        user.username = "john_doe";
        user.password = "john_doe";
        
        Set<ConstraintViolation<TestUserNotEquals>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Fields with same values should fail validation");
    }
    
    @Test
    void testFieldEquals_NullValues_ShouldPass() {
        TestUserEquals user = new TestUserEquals();
        user.phoneNumber1 = null;
        user.phoneNumber2 = null;
        user.email1 = "test@example.com";
        user.email2 = "test@example.com";
        
        Set<ConstraintViolation<TestUserEquals>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "Null values are equal so validation should pass");
    }
    
    /**
     * Test class for @FieldEquals
     */
    @FieldEquals(fields = {"phoneNumber1", "phoneNumber2"}, message = "Phone numbers must match")
    @FieldEquals(fields = {"email1", "email2"}, message = "Emails must match")
    static class TestUserEquals {
        String phoneNumber1;
        String phoneNumber2;
        String email1;
        String email2;
    }
    
    /**
     * Test class for @FieldNotEquals
     */
    @FieldNotEquals(fields = {"username", "password"}, message = "Username and password must be different")
    static class TestUserNotEquals {
        String username;
        String password;
    }
}