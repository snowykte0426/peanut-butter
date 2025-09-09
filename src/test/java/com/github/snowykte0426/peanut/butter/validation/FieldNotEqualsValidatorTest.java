package com.github.snowykte0426.peanut.butter.validation;

import jakarta.validation.ConstraintValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class FieldNotEqualsValidatorTest {

    private FieldNotEquals fieldNotEquals;
    private ConstraintValidatorContext context;
    private ConstraintViolationBuilder violationBuilder;
    private NodeBuilderCustomizableContext nodeBuilder;
    private FieldNotEqualsValidator validator;

    @BeforeEach
    void setUp() {
        fieldNotEquals = mock(FieldNotEquals.class);
        context = mock(ConstraintValidatorContext.class);
        violationBuilder = mock(ConstraintViolationBuilder.class);
        nodeBuilder = mock(NodeBuilderCustomizableContext.class);

        validator = new FieldNotEqualsValidator();

        when(fieldNotEquals.fields()).thenReturn(new String[]{"field1", "field2"});
        when(fieldNotEquals.message()).thenReturn("Fields must have different values");

        validator.initialize(fieldNotEquals);
    }

    @Test
    void shouldImplementConstraintValidator() {
        assertTrue(validator instanceof ConstraintValidator);
    }

    @Test
    void shouldReturnTrueForNullObject() {
        boolean result = validator.isValid(null, context);
        assertTrue(result);
    }

    @Test
    void shouldReturnTrueForLessThanTwoFields() {
        when(fieldNotEquals.fields()).thenReturn(new String[]{"field1"});
        validator.initialize(fieldNotEquals);

        TestObject obj = new TestObject("value1", "value2");
        boolean result = validator.isValid(obj, context);

        assertTrue(result);
    }

    @Test
    void shouldReturnTrueForDifferentValues() {
        TestObject obj = new TestObject("value1", "value2");
        boolean result = validator.isValid(obj, context);

        assertTrue(result);
    }

    @Test
    void shouldReturnFalseForEqualValues() {
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        when(violationBuilder.addPropertyNode(anyString())).thenReturn(nodeBuilder);
        when(nodeBuilder.addConstraintViolation()).thenReturn(context);

        TestObject obj = new TestObject("same", "same");
        boolean result = validator.isValid(obj, context);

        assertFalse(result);
        verify(context).disableDefaultConstraintViolation();
        verify(context, times(2)).buildConstraintViolationWithTemplate("Fields must have different values");
    }

    @Test
    void shouldReturnTrueForOneNullValue() {
        TestObject obj = new TestObject("value1", null);
        boolean result = validator.isValid(obj, context);

        assertTrue(result);
    }

    @Test
    void shouldReturnFalseForBothNullValues() {
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        when(violationBuilder.addPropertyNode(anyString())).thenReturn(nodeBuilder);
        when(nodeBuilder.addConstraintViolation()).thenReturn(context);

        TestObject obj = new TestObject(null, null);
        boolean result = validator.isValid(obj, context);

        assertFalse(result);
        verify(context).disableDefaultConstraintViolation();
    }

    @Test
    void shouldHandleMultipleFields() {
        when(fieldNotEquals.fields()).thenReturn(new String[]{"field1", "field2", "field3"});
        validator.initialize(fieldNotEquals);

        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        when(violationBuilder.addPropertyNode(anyString())).thenReturn(nodeBuilder);
        when(nodeBuilder.addConstraintViolation()).thenReturn(context);

        MultiFieldTestObject obj = new MultiFieldTestObject("same", "different", "same");
        boolean result = validator.isValid(obj, context);

        assertFalse(result);
        verify(context).disableDefaultConstraintViolation();
        verify(context, times(2)).buildConstraintViolationWithTemplate(anyString());
    }

    @Test
    void shouldReturnTrueForNonExistentFields() {
        when(fieldNotEquals.fields()).thenReturn(new String[]{"nonExistent1", "nonExistent2"});
        validator.initialize(fieldNotEquals);

        TestObject obj = new TestObject("value1", "value2");
        boolean result = validator.isValid(obj, context);

        assertTrue(result);
    }

    @Test
    void shouldHandleInheritedFields() {
        when(fieldNotEquals.fields()).thenReturn(new String[]{"parentField", "field1"});
        validator.initialize(fieldNotEquals);

        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        when(violationBuilder.addPropertyNode(anyString())).thenReturn(nodeBuilder);
        when(nodeBuilder.addConstraintViolation()).thenReturn(context);

        // parentField와 field1이 같은 값("same")을 가지므로 validation이 실패해야 함
        ExtendedTestObject obj = new ExtendedTestObject("same", "same", "different");
        boolean result = validator.isValid(obj, context);

        assertFalse(result);
        verify(context).disableDefaultConstraintViolation();
    }

    static class TestObject {
        public String field1;
        public String field2;

        public TestObject(String field1, String field2) {
            this.field1 = field1;
            this.field2 = field2;
        }
    }

    static class MultiFieldTestObject {
        public String field1;
        public String field2;
        public String field3;

        public MultiFieldTestObject(String field1, String field2, String field3) {
            this.field1 = field1;
            this.field2 = field2;
            this.field3 = field3;
        }
    }

    static class ParentTestObject {
        public String parentField;

        public ParentTestObject(String parentField) {
            this.parentField = parentField;
        }
    }

    static class ExtendedTestObject extends ParentTestObject {
        public String field1;
        public String field2;

        public ExtendedTestObject(String parentField, String field1, String field2) {
            super(parentField);
            this.field1 = field1;
            this.field2 = field2;
        }
    }
}
