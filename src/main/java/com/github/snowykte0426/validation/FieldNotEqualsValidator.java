package com.github.snowykte0426.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @FieldNotEquals 어노테이션의 검증 로직을 구현하는 클래스입니다.
 * 
 * @author Kim Tae Eun
 */
public class FieldNotEqualsValidator implements ConstraintValidator<FieldNotEquals, Object> {
    
    private String[] fieldNames;
    private String message;
    
    @Override
    public void initialize(FieldNotEquals constraintAnnotation) {
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
            
            // 중복되는 값이 있는지 확인
            Set<Object> uniqueValues = new HashSet<>();
            List<Integer> duplicateIndices = new ArrayList<>();
            
            for (int i = 0; i < values.size(); i++) {
                Object value = values.get(i);
                if (!uniqueValues.add(value)) {
                    // 현재 값과 동일한 값을 가진 이전 인덱스도 찾아서 추가
                    for (int j = 0; j < i; j++) {
                        if (Objects.equals(values.get(j), value) && !duplicateIndices.contains(j)) {
                            duplicateIndices.add(j);
                        }
                    }
                    duplicateIndices.add(i);
                }
            }
            
            if (!duplicateIndices.isEmpty()) {
                context.disableDefaultConstraintViolation();
                for (int index : duplicateIndices) {
                    context.buildConstraintViolationWithTemplate(message)
                        .addPropertyNode(accessibleFieldNames.get(index))
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