package com.github.snowykte0426.peanut.butter.validation;

import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.junit.jupiter.api.Assertions.*;

class FieldNotEqualsTest {

    @Test
    void shouldHaveRuntimeRetention() {
        Retention retention = FieldNotEquals.class.getAnnotation(Retention.class);
        assertEquals(RetentionPolicy.RUNTIME, retention.value());
    }

    @Test
    void shouldTargetType() {
        Target target = FieldNotEquals.class.getAnnotation(Target.class);
        assertEquals(1, target.value().length);
        assertEquals(ElementType.TYPE, target.value()[0]);
    }

    @Test
    void shouldHaveFieldsMethod() throws NoSuchMethodException {
        assertNotNull(FieldNotEquals.class.getMethod("fields"));
        assertEquals(String[].class, FieldNotEquals.class.getMethod("fields").getReturnType());
    }

    @Test
    void shouldHaveMessageMethod() throws NoSuchMethodException {
        assertNotNull(FieldNotEquals.class.getMethod("message"));
        assertEquals(String.class, FieldNotEquals.class.getMethod("message").getReturnType());
    }

    @Test
    void shouldHaveDefaultMessage() throws NoSuchMethodException {
        String defaultMessage = (String) FieldNotEquals.class.getMethod("message").getDefaultValue();
        assertEquals("Fields must have different values", defaultMessage);
    }

    @Test
    void shouldHaveListAnnotation() {
        assertNotNull(FieldNotEquals.List.class);

        Target listTarget = FieldNotEquals.List.class.getAnnotation(Target.class);
        assertEquals(1, listTarget.value().length);
        assertEquals(ElementType.TYPE, listTarget.value()[0]);

        Retention listRetention = FieldNotEquals.List.class.getAnnotation(Retention.class);
        assertEquals(RetentionPolicy.RUNTIME, listRetention.value());
    }

    @FieldNotEquals(fields = {"username", "password"})
    static class TestClass {
        String username;
        String password;
    }

    @Test
    void shouldWorkOnClass() {
        FieldNotEquals annotation = TestClass.class.getAnnotation(FieldNotEquals.class);
        assertNotNull(annotation);
        assertArrayEquals(new String[]{"username", "password"}, annotation.fields());
    }
}
