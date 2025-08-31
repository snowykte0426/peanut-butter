package com.github.snowykte0426.peanut.buttor.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Validator implementation for @FieldEquals annotation.
 * 
 * @author Kim Tae Eun
 */
public class FieldEqualsValidator implements ConstraintValidator<FieldEquals, Object> {
    
    private static final int MINIMUM_FIELDS_FOR_VALIDATION = 2;
    
    private String[] fieldNames;
    private String message;
    
    @Override
    public void initialize(FieldEquals constraintAnnotation) {
        this.fieldNames = constraintAnnotation.fields();
        this.message = constraintAnnotation.message();
    }
    
    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        if (object == null || fieldNames.length < MINIMUM_FIELDS_FOR_VALIDATION) {
            return true;
        }
        
        try {
            List<Object> values = new ArrayList<>();
            List<String> accessibleFieldNames = new ArrayList<>();
            
            for (String fieldName : fieldNames) {
                Field field = getField(object.getClass(), fieldName);
                if (field != null) {
                    field.setAccessible(true);
                    Object value = field.get(object);
                    values.add(value);
                    accessibleFieldNames.add(fieldName);
                }
            }
            
            if (values.size() < MINIMUM_FIELDS_FOR_VALIDATION) {
                return true;
            }

            Object firstValue = values.get(0);
            boolean allEqual = values.stream()
                .allMatch(value -> Objects.equals(firstValue, value));
            
            if (!allEqual) {
                context.disableDefaultConstraintViolation();
                for (String fieldName : accessibleFieldNames) {
                    context.buildConstraintViolationWithTemplate(message)
                        .addPropertyNode(fieldName)
                        .addConstraintViolation();
                }
                return false;
            }
            
            return true;
            
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error accessing field: " + e.getMessage(), e);
        }
    }
    
    /**
     * Finds a field in the class hierarchy.
     */
    private Field getField(Class<?> clazz, String fieldName) {
        Class<?> currentClass = clazz;
        while (currentClass != null && currentClass != Object.class) {
            try {
                return currentClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                currentClass = currentClass.getSuperclass();
            }
        }
        return null;
    }
}