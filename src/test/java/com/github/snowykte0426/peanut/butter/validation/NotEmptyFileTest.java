package com.github.snowykte0426.peanut.butter.validation;

import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.junit.jupiter.api.Assertions.*;

class NotEmptyFileTest {

    @Test
    void shouldHaveRuntimeRetention() {
        Retention retention = NotEmptyFile.class.getAnnotation(Retention.class);
        assertEquals(RetentionPolicy.RUNTIME, retention.value());
    }

    @Test
    void shouldTargetField() {
        Target target = NotEmptyFile.class.getAnnotation(Target.class);
        assertEquals(1, target.value().length);
        assertEquals(ElementType.FIELD, target.value()[0]);
    }

    @Test
    void shouldHaveMessageMethod() throws NoSuchMethodException {
        assertNotNull(NotEmptyFile.class.getMethod("message"));
        assertEquals(String.class, NotEmptyFile.class.getMethod("message").getReturnType());
    }

    @Test
    void shouldHaveDefaultMessage() throws NoSuchMethodException {
        String defaultMessage = (String) NotEmptyFile.class.getMethod("message").getDefaultValue();
        assertEquals("File must not be empty", defaultMessage);
    }

    @Test
    void shouldHaveGroupsMethod() throws NoSuchMethodException {
        assertNotNull(NotEmptyFile.class.getMethod("groups"));
        assertEquals(Class[].class, NotEmptyFile.class.getMethod("groups").getReturnType());
    }

    @Test
    void shouldHavePayloadMethod() throws NoSuchMethodException {
        assertNotNull(NotEmptyFile.class.getMethod("payload"));
        assertEquals(Class[].class, NotEmptyFile.class.getMethod("payload").getReturnType());
    }

    static class TestClass {
        @NotEmptyFile
        private String file;
    }

    @Test
    void shouldWorkOnField() throws NoSuchFieldException {
        NotEmptyFile annotation = TestClass.class.getDeclaredField("file").getAnnotation(NotEmptyFile.class);
        assertNotNull(annotation);
        assertEquals("File must not be empty", annotation.message());
    }
}
