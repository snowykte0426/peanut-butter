package com.github.snowykte0426.peanut.butter.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FieldEqualsValidatorUnitTest {

    private FieldEqualsValidator validator;
    private FieldEquals annotation;
    private ConstraintValidatorContext context;
    private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;
    private ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext nodeBuilder;

    @BeforeEach
    void setUp() {
        validator = new FieldEqualsValidator();
        annotation = mock(FieldEquals.class);
        context = mock(ConstraintValidatorContext.class);
        violationBuilder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        nodeBuilder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext.class);
    }

    @Test
    void shouldImplementConstraintValidator() {
        assertTrue(validator instanceof ConstraintValidator);
    }

    @Test
    void shouldReturnTrueForNullObject() {
        when(annotation.fields()).thenReturn(new String[]{"field1", "field2"});
        when(annotation.message()).thenReturn("Fields must be equal");
        validator.initialize(annotation);

        boolean result = validator.isValid(null, context);

        assertTrue(result);
    }

    @Test
    void shouldReturnTrueForLessThanTwoFields() {
        when(annotation.fields()).thenReturn(new String[]{"field1"});
        when(annotation.message()).thenReturn("Fields must be equal");
        validator.initialize(annotation);

        TestObject obj = new TestObject("value1", "value2");
        boolean result = validator.isValid(obj, context);

        assertTrue(result);
    }

    @Test
    void shouldReturnTrueForEqualValues() {
        when(annotation.fields()).thenReturn(new String[]{"field1", "field2"});
        when(annotation.message()).thenReturn("Fields must be equal");
        validator.initialize(annotation);

        TestObject obj = new TestObject("same", "same");
        boolean result = validator.isValid(obj, context);

        assertTrue(result);
    }

    @Test
    void shouldReturnFalseForDifferentValues() {
        when(annotation.fields()).thenReturn(new String[]{"field1", "field2"});
        when(annotation.message()).thenReturn("Fields must be equal");
        validator.initialize(annotation);

        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        when(violationBuilder.addPropertyNode(anyString())).thenReturn(nodeBuilder);
        when(nodeBuilder.addConstraintViolation()).thenReturn(context);

        TestObject obj = new TestObject("value1", "value2");
        boolean result = validator.isValid(obj, context);

        assertFalse(result);
        verify(context).disableDefaultConstraintViolation();
        verify(context, times(2)).buildConstraintViolationWithTemplate("Fields must be equal");
    }

    @Test
    void shouldReturnTrueForAllNullValues() {
        when(annotation.fields()).thenReturn(new String[]{"field1", "field2"});
        when(annotation.message()).thenReturn("Fields must be equal");
        validator.initialize(annotation);

        TestObject obj = new TestObject(null, null);
        boolean result = validator.isValid(obj, context);

        assertTrue(result);
    }

    @Test
    void shouldReturnFalseForMixedNullValues() {
        when(annotation.fields()).thenReturn(new String[]{"field1", "field2"});
        when(annotation.message()).thenReturn("Fields must be equal");
        validator.initialize(annotation);

        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        when(violationBuilder.addPropertyNode(anyString())).thenReturn(nodeBuilder);
        when(nodeBuilder.addConstraintViolation()).thenReturn(context);

        TestObject obj = new TestObject("value1", null);
        boolean result = validator.isValid(obj, context);

        assertFalse(result);
        verify(context).disableDefaultConstraintViolation();
    }

    @Test
    void shouldHandleMultipleFields() {
        when(annotation.fields()).thenReturn(new String[]{"field1", "field2", "field3"});
        when(annotation.message()).thenReturn("All fields must be equal");
        validator.initialize(annotation);

        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        when(violationBuilder.addPropertyNode(anyString())).thenReturn(nodeBuilder);
        when(nodeBuilder.addConstraintViolation()).thenReturn(context);

        MultiFieldTestObject obj = new MultiFieldTestObject("same", "different", "same");
        boolean result = validator.isValid(obj, context);

        assertFalse(result);
        verify(context).disableDefaultConstraintViolation();
    }

    @Test
    void shouldHandleInheritedFields() {
        when(annotation.fields()).thenReturn(new String[]{"parentField", "field1"});
        when(annotation.message()).thenReturn("Fields must be equal");
        validator.initialize(annotation);

        ExtendedTestObject obj = new ExtendedTestObject("same", "same", "different");
        boolean result = validator.isValid(obj, context);

        assertTrue(result);
    }

    @Test
    void shouldReturnTrueForNonExistentFields() {
        when(annotation.fields()).thenReturn(new String[]{"nonExistent1", "nonExistent2"});
        when(annotation.message()).thenReturn("Fields must be equal");
        validator.initialize(annotation);

        TestObject obj = new TestObject("value1", "value2");
        boolean result = validator.isValid(obj, context);

        assertTrue(result);
    }

    @Test
    void shouldHandleReflectionExceptionGracefully() {
        when(annotation.fields()).thenReturn(new String[]{"field1", "field2"});
        when(annotation.message()).thenReturn("Fields must be equal");
        validator.initialize(annotation);

        // Private fields should still be accessible via setAccessible(true)
        PrivateFieldTestObject obj = new PrivateFieldTestObject("same", "same");
        boolean result = validator.isValid(obj, context);

        assertTrue(result);
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

    static class PrivateFieldTestObject {
        private String field1;
        private String field2;

        public PrivateFieldTestObject(String field1, String field2) {
            this.field1 = field1;
            this.field2 = field2;
        }
    }
}
