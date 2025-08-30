package com.github.snowykte0426.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @FieldEquals 어노테이션의 검증 로직을 구현하는 클래스입니다.
 * 
 * @author Kim Tae Eun
 */
public class FieldEqualsValidator implements ConstraintValidator<FieldEquals, Object> {
    
    private String[] fieldNames;
    private String message;
    
    @Override
    public void initialize(FieldEquals constraintAnnotation) {
        this.fieldNames = constraintAnnotation.fields();
        this.message = constraintAnnotation.message();
    }
    
    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        if (object == null || fieldNames.length < 2) {
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
            
            if (values.size() < 2) {
                return true;
            }
            
            // 모든 값이 동일한지 확인
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
            throw new RuntimeException("필드 접근 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
    
    /**
     * 클래스 계층구조에서 필드를 찾습니다.
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