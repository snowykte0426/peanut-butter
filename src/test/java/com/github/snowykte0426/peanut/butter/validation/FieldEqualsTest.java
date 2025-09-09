package com.github.snowykte0426.peanut.butter.validation;

import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.junit.jupiter.api.Assertions.*;

class FieldEqualsTest {

    @Test
    void shouldHaveRuntimeRetention() {
        Retention retention = FieldEquals.class.getAnnotation(Retention.class);
        assertEquals(RetentionPolicy.RUNTIME, retention.value());
    }

    @Test
    void shouldTargetType() {
        Target target = FieldEquals.class.getAnnotation(Target.class);
        assertEquals(1, target.value().length);
        assertEquals(ElementType.TYPE, target.value()[0]);
    }

    @Test
    void shouldHaveFieldsMethod() throws NoSuchMethodException {
        assertNotNull(FieldEquals.class.getMethod("fields"));
        assertEquals(String[].class, FieldEquals.class.getMethod("fields").getReturnType());
    }

    @Test
    void shouldHaveMessageMethod() throws NoSuchMethodException {
        assertNotNull(FieldEquals.class.getMethod("message"));
        assertEquals(String.class, FieldEquals.class.getMethod("message").getReturnType());
    }

    @Test
    void shouldHaveGroupsMethod() throws NoSuchMethodException {
        assertNotNull(FieldEquals.class.getMethod("groups"));
        assertEquals(Class[].class, FieldEquals.class.getMethod("groups").getReturnType());
    }

    @Test
    void shouldHavePayloadMethod() throws NoSuchMethodException {
        assertNotNull(FieldEquals.class.getMethod("payload"));
        assertEquals(Class[].class, FieldEquals.class.getMethod("payload").getReturnType());
    }

    @Test
    void shouldHaveDefaultMessage() throws NoSuchMethodException {
        String defaultMessage = (String) FieldEquals.class.getMethod("message").getDefaultValue();
        assertEquals("Fields must have equal values", defaultMessage);
    }

    @Test
    void shouldHaveEmptyDefaultGroups() throws NoSuchMethodException {
        Class<?>[] defaultGroups = (Class<?>[]) FieldEquals.class.getMethod("groups").getDefaultValue();
        assertEquals(0, defaultGroups.length);
    }

    @Test
    void shouldHaveEmptyDefaultPayload() throws NoSuchMethodException {
        Class<?>[] defaultPayload = (Class<?>[]) FieldEquals.class.getMethod("payload").getDefaultValue();
        assertEquals(0, defaultPayload.length);
    }

    @Test
    void shouldHaveListAnnotation() {
        assertNotNull(FieldEquals.List.class);

        Target listTarget = FieldEquals.List.class.getAnnotation(Target.class);
        assertEquals(1, listTarget.value().length);
        assertEquals(ElementType.TYPE, listTarget.value()[0]);

        Retention listRetention = FieldEquals.List.class.getAnnotation(Retention.class);
        assertEquals(RetentionPolicy.RUNTIME, listRetention.value());
    }

    @FieldEquals(fields = {"field1", "field2"})
    static class TestClass {
        String field1;
        String field2;
    }

    @Test
    void shouldWorkOnClass() {
        FieldEquals annotation = TestClass.class.getAnnotation(FieldEquals.class);
        assertNotNull(annotation);
        assertArrayEquals(new String[]{"field1", "field2"}, annotation.fields());
    }
}
