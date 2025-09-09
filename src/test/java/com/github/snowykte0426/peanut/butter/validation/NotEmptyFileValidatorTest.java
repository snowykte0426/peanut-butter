package com.github.snowykte0426.peanut.butter.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotEmptyFileValidatorTest {

    private NotEmptyFile notEmptyFile;
    private ConstraintValidatorContext context;
    private MultipartFile multipartFile;
    private NotEmptyFileValidator validator;

    @BeforeEach
    void setUp() {
        notEmptyFile = mock(NotEmptyFile.class);
        context = mock(ConstraintValidatorContext.class);
        multipartFile = mock(MultipartFile.class);

        validator = new NotEmptyFileValidator();
        validator.initialize(notEmptyFile);
    }

    @Test
    void shouldImplementConstraintValidator() {
        assertTrue(validator instanceof ConstraintValidator);
    }

    @Test
    void shouldReturnFalseForNullFile() {
        boolean result = validator.isValid(null, context);

        assertFalse(result);
    }

    @Test
    void shouldReturnFalseForEmptyFile() {
        when(multipartFile.isEmpty()).thenReturn(true);

        boolean result = validator.isValid(multipartFile, context);

        assertFalse(result);
    }

    @Test
    void shouldReturnTrueForNonEmptyFile() {
        when(multipartFile.isEmpty()).thenReturn(false);

        boolean result = validator.isValid(multipartFile, context);

        assertTrue(result);
    }

    @Test
    void shouldHandleFileWithZeroSize() {
        when(multipartFile.isEmpty()).thenReturn(true);

        boolean result = validator.isValid(multipartFile, context);

        assertFalse(result);
    }

    @Test
    void shouldHandleFileWithContent() {
        when(multipartFile.isEmpty()).thenReturn(false);

        boolean result = validator.isValid(multipartFile, context);

        assertTrue(result);
    }
}
