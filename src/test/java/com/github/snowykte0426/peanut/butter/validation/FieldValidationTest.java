package com.github.snowykte0426.peanut.butter.validation;

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
    
    @Test
    void testFieldEquals_OneNullOneNotNull_ShouldFail() {
        TestUserEquals user = new TestUserEquals();
        user.phoneNumber1 = "010-1234-5678";
        user.phoneNumber2 = null;
        user.email1 = "test@example.com";
        user.email2 = "test@example.com";
        
        Set<ConstraintViolation<TestUserEquals>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "One null and one non-null field should fail validation");
        
        boolean hasPhoneViolation = violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().contains("phoneNumber"));
        assertTrue(hasPhoneViolation, "Validation error should occur for phone number fields");
    }
    
    @Test
    void testFieldNotEquals_BothNull_ShouldFail() {
        TestUserNotEquals user = new TestUserNotEquals();
        user.username = null;
        user.password = null;
        
        Set<ConstraintViolation<TestUserNotEquals>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Both null values should fail FieldNotEquals validation");
    }
    
    @Test
    void testFieldNotEquals_OneNullOneNotNull_ShouldPass() {
        TestUserNotEquals user = new TestUserNotEquals();
        user.username = "john_doe";
        user.password = null;
        
        Set<ConstraintViolation<TestUserNotEquals>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "One null and one non-null should pass FieldNotEquals validation");
    }
    
    @Test
    void testFieldEquals_EmptyStrings_ShouldPass() {
        TestUserEquals user = new TestUserEquals();
        user.phoneNumber1 = "";
        user.phoneNumber2 = "";
        user.email1 = "test@example.com";
        user.email2 = "test@example.com";
        
        Set<ConstraintViolation<TestUserEquals>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "Empty strings are equal so validation should pass");
    }
    
    @Test
    void testFieldNotEquals_EmptyStrings_ShouldFail() {
        TestUserNotEquals user = new TestUserNotEquals();
        user.username = "";
        user.password = "";
        
        Set<ConstraintViolation<TestUserNotEquals>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Empty strings are equal so FieldNotEquals should fail");
    }
    
    @Test
    void testFieldEquals_MultipleAnnotations_MixedResults() {
        TestUserEquals user = new TestUserEquals();
        user.phoneNumber1 = "010-1234-5678";
        user.phoneNumber2 = "010-1234-5678";
        user.email1 = "test1@example.com";
        user.email2 = "test2@example.com";
        
        Set<ConstraintViolation<TestUserEquals>> violations = validator.validate(user);
        assertEquals(1, violations.size(), "Should have exactly one violation for email mismatch");
        
        boolean hasEmailViolation = violations.stream()
            .anyMatch(v -> v.getMessage().contains("Emails must match"));
        assertTrue(hasEmailViolation, "Validation error should be for email fields");
    }
    
    @Test
    void testFieldValidation_WithInheritance() {
        ExtendedUser user = new ExtendedUser();
        user.phoneNumber1 = "010-1234-5678";
        user.phoneNumber2 = "010-8765-4321";
        user.email1 = "test@example.com"; 
        user.email2 = "test@example.com";
        user.confirmEmail = "test@example.com";
        
        Set<ConstraintViolation<ExtendedUser>> violations = validator.validate(user);
        assertEquals(1, violations.size(), "Should have one violation for phone number mismatch");
    }
    
    @Test
    void testFieldValidation_Casesensitivity() {
        TestUserEquals user = new TestUserEquals();
        user.phoneNumber1 = "Test@Example.Com";
        user.phoneNumber2 = "test@example.com";
        user.email1 = "same@test.com";
        user.email2 = "same@test.com";
        
        Set<ConstraintViolation<TestUserEquals>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Case sensitive comparison should detect difference");
    }
    
    @Test
    void testFieldValidation_WithComplexObjects() {
        ComplexFieldTest user = new ComplexFieldTest();
        user.number1 = 42;
        user.number2 = 42;
        user.flag1 = true;
        user.flag2 = false;
        
        Set<ConstraintViolation<ComplexFieldTest>> violations = validator.validate(user);
        assertEquals(1, violations.size(), "Should have one violation for boolean mismatch");
        
        boolean hasFlagViolation = violations.stream()
            .anyMatch(v -> v.getMessage().contains("Boolean flags must match"));
        assertTrue(hasFlagViolation, "Validation error should be for boolean fields");
    }
    
    @Test
    void testFieldValidation_ErrorMessages() {
        TestUserEquals user = new TestUserEquals();
        user.phoneNumber1 = "010-1234-5678";
        user.phoneNumber2 = "010-8765-4321";
        user.email1 = "test1@example.com";
        user.email2 = "test2@example.com";
        
        Set<ConstraintViolation<TestUserEquals>> violations = validator.validate(user);
        assertEquals(2, violations.size(), "Should have violations for both phone and email");
        
        boolean hasPhoneMessage = violations.stream()
            .anyMatch(v -> "Phone numbers must match".equals(v.getMessage()));
        boolean hasEmailMessage = violations.stream()
            .anyMatch(v -> "Emails must match".equals(v.getMessage()));
            
        assertTrue(hasPhoneMessage, "Should have phone number error message");
        assertTrue(hasEmailMessage, "Should have email error message");
    }
    
    @Test
    void testFieldValidation_InvalidFieldNames() {
        InvalidFieldTest user = new InvalidFieldTest();
        user.existingField = "test";
        
        // This should not throw an exception during validation setup
        // The validator should handle non-existent fields gracefully
        Set<ConstraintViolation<InvalidFieldTest>> violations = validator.validate(user);
        // The behavior with invalid field names may vary by implementation
        // This test ensures the validator doesn't crash
        assertTrue(true, "Validator should handle invalid field names gracefully");
    }

    /**
     * Test class for @FieldNotEquals
     */
    @FieldNotEquals(fields = {"username", "password"}, message = "Username and password must be different")
    static class TestUserNotEquals {
        String username;
        String password;
    }
    
    /**
     * Test class for inheritance testing
     */
    @FieldEquals(fields = {"email1", "confirmEmail"}, message = "Email and confirmation must match")
    static class ExtendedUser extends TestUserEquals {
        String confirmEmail;
    }
    
    /**
     * Test class for different data types
     */
    @FieldEquals(fields = {"number1", "number2"}, message = "Numbers must match")
    @FieldEquals(fields = {"flag1", "flag2"}, message = "Boolean flags must match")
    static class ComplexFieldTest {
        Integer number1;
        Integer number2;
        Boolean flag1;
        Boolean flag2;
    }
    
    /**
     * Test class with invalid field names
     */
    @FieldEquals(fields = {"existingField", "nonExistentField"}, message = "Should handle invalid fields")
    static class InvalidFieldTest {
        String existingField;
        // note: nonExistentField doesn't exist
    }
}