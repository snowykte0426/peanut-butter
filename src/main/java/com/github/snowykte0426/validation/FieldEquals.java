package com.github.snowykte0426.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates that specified fields have equal values.
 * 
 * @author Kim Tae Eun
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FieldEqualsValidator.class)
@Documented
@Repeatable(FieldEquals.List.class)
public @interface FieldEquals {
    
    /**
     * Field names to validate for equality.
     * 
     * @return array of field names
     */
    String[] fields();
    
    /**
     * Error message when validation fails.
     * 
     * @return error message
     */
    String message() default "Fields must have equal values";
    
    /**
     * Validation groups.
     * 
     * @return validation groups
     */
    Class<?>[] groups() default {};
    
    /**
     * Payload for validation.
     * 
     * @return payload
     */
    Class<? extends Payload>[] payload() default {};
    
    /**
     * Container for multiple @FieldEquals annotations.
     */
    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        FieldEquals[] value();
    }
}