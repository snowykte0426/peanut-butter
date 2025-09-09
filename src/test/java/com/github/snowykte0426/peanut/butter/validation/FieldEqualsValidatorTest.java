package com.github.snowykte0426.peanut.butter.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FieldEqualsValidatorTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @FieldEquals(fields = {"password", "confirmPassword"}, message = "Passwords must match")
    static class PasswordTestObject {
        private String password;
        private String confirmPassword;

        public PasswordTestObject(String password, String confirmPassword) {
            this.password = password;
            this.confirmPassword = confirmPassword;
        }
    }

    @FieldEquals(fields = {"field1", "field2", "field3"})
    static class MultipleFieldTestObject {
        private String field1;
        private String field2;
        private String field3;

        public MultipleFieldTestObject(String field1, String field2, String field3) {
            this.field1 = field1;
            this.field2 = field2;
            this.field3 = field3;
        }
    }

    @FieldEquals(fields = {"value"})
    static class SingleFieldTestObject {
        private String value;

        public SingleFieldTestObject(String value) {
            this.value = value;
        }
    }

    @Test
    void shouldPassValidationWhenFieldsAreEqual() {
        PasswordTestObject obj = new PasswordTestObject("password123", "password123");
        Set<ConstraintViolation<PasswordTestObject>> violations = validator.validate(obj);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailValidationWhenFieldsAreNotEqual() {
        PasswordTestObject obj = new PasswordTestObject("password123", "different");
        Set<ConstraintViolation<PasswordTestObject>> violations = validator.validate(obj);
        assertFalse(violations.isEmpty());
        assertEquals(2, violations.size()); // One violation for each field
    }

    @Test
    void shouldPassValidationWhenAllFieldsAreNull() {
        PasswordTestObject obj = new PasswordTestObject(null, null);
        Set<ConstraintViolation<PasswordTestObject>> violations = validator.validate(obj);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailValidationWhenOneFieldIsNull() {
        PasswordTestObject obj = new PasswordTestObject("password123", null);
        Set<ConstraintViolation<PasswordTestObject>> violations = validator.validate(obj);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldPassValidationWithMultipleEqualFields() {
        MultipleFieldTestObject obj = new MultipleFieldTestObject("same", "same", "same");
        Set<ConstraintViolation<MultipleFieldTestObject>> violations = validator.validate(obj);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailValidationWithMultipleUnequalFields() {
        MultipleFieldTestObject obj = new MultipleFieldTestObject("same", "different", "same");
        Set<ConstraintViolation<MultipleFieldTestObject>> violations = validator.validate(obj);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldPassValidationWithSingleField() {
        SingleFieldTestObject obj = new SingleFieldTestObject("value");
        Set<ConstraintViolation<SingleFieldTestObject>> violations = validator.validate(obj);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldPassValidationWhenObjectIsNull() {
        // Hibernate Validator는 null 객체 검증을 허용하지 않으므로 이 테스트를 수정합니다
        // 대신 validator의 isValid 메서드를 직접 호출해서 테스트합니다
        FieldEqualsValidator validator = new FieldEqualsValidator();
        FieldEquals mockAnnotation = mock(FieldEquals.class);
        when(mockAnnotation.fields()).thenReturn(new String[]{"field1", "field2"});
        when(mockAnnotation.message()).thenReturn("Fields must be equal");

        validator.initialize(mockAnnotation);

        // null 객체에 대해서는 validator가 true를 반환해야 함
        boolean result = validator.isValid(null, mock(ConstraintValidatorContext.class));
        assertTrue(result);
    }

    @Test
    void shouldUseCustomMessage() {
        PasswordTestObject obj = new PasswordTestObject("password123", "different");
        Set<ConstraintViolation<PasswordTestObject>> violations = validator.validate(obj);

        violations.forEach(violation -> {
            assertEquals("Passwords must match", violation.getMessage());
        });
    }
}
