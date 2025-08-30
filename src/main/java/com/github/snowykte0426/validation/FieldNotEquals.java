package com.github.snowykte0426.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates that specified fields have different values.
 * 
 * @author Kim Tae Eun
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FieldNotEqualsValidator.class)
@Documented
@Repeatable(FieldNotEquals.List.class)
public @interface FieldNotEquals {
    
    /**
     * Field names to validate for inequality.
     * 
     * @return array of field names
     */
    String[] fields();
    
    /**
     * Error message when validation fails.
     * 
     * @return error message
     */
    String message() default "Fields must have different values";
    
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
     * Container for multiple @FieldNotEquals annotations.
     */
    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        FieldNotEquals[] value();
    }
}