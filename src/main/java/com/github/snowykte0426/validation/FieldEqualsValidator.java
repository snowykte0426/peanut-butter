package com.github.snowykte0426.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @FieldEquals 어노테이션의 검증 로직을 구현하는 클래스입니다.
 * 
 * @author Kim Tae Eun
 */
public class FieldEqualsValidator implements ConstraintValidator<FieldEquals, Object> {
    
    private static final Map<Class<?>, Map<String, List<Field>>> fieldCache = new HashMap<>();
    
    @Override
    public void initialize(FieldEquals constraintAnnotation) {
        // 초기화 로직이 필요한 경우 여기에 구현
    }
    
    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        if (object == null) {
            return true; // null 객체는 검증하지 않음
        }
        
        Class<?> clazz = object.getClass();
        Map<String, List<Field>> groupedFields = getGroupedFields(clazz);
        
        boolean isValid = true;
        context.disableDefaultConstraintViolation();
        
        for (Map.Entry<String, List<Field>> entry : groupedFields.entrySet()) {
            String group = entry.getKey();
            List<Field> fields = entry.getValue();
            
            if (fields.size() < 2) {
                continue; // 그룹에 필드가 1개 이하면 검증하지 않음
            }
            
            if (!validateGroup(object, fields, group, context)) {
                isValid = false;
            }
        }
        
        return isValid;
    }
    
    /**
     * 클래스의 @FieldEquals 어노테이션이 적용된 필드들을 그룹별로 분류합니다.
     */
    private Map<String, List<Field>> getGroupedFields(Class<?> clazz) {
        return fieldCache.computeIfAbsent(clazz, this::collectGroupedFields);
    }
    
    /**
     * 클래스에서 @FieldEquals 어노테이션이 적용된 필드들을 수집하고 그룹별로 분류합니다.
     */
    private Map<String, List<Field>> collectGroupedFields(Class<?> clazz) {
        Map<String, List<Field>> groupedFields = new HashMap<>();
        
        // 상속된 필드까지 포함하여 모든 필드를 검사
        Class<?> currentClass = clazz;
        while (currentClass != null && currentClass != Object.class) {
            for (Field field : currentClass.getDeclaredFields()) {
                FieldEquals[] annotations = field.getAnnotationsByType(FieldEquals.class);
                
                for (FieldEquals annotation : annotations) {
                    String group = annotation.group().isEmpty() ? "" : annotation.group();
                    groupedFields.computeIfAbsent(group, k -> new ArrayList<>()).add(field);
                }
            }
            currentClass = currentClass.getSuperclass();
        }
        
        return groupedFields;
    }
    
    /**
     * 특정 그룹의 필드들이 모두 동일한 값을 가지는지 검증합니다.
     */
    private boolean validateGroup(Object object, List<Field> fields, String group, ConstraintValidatorContext context) {
        try {
            List<Object> values = new ArrayList<>();
            List<String> fieldNames = new ArrayList<>();
            String errorMessage = null;
            
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(object);
                values.add(value);
                fieldNames.add(field.getName());
                
                // 첫 번째 필드의 어노테이션에서 메시지를 가져옴
                if (errorMessage == null) {
                    FieldEquals annotation = field.getAnnotation(FieldEquals.class);
                    if (annotation != null && (annotation.group().equals(group) || (annotation.group().isEmpty() && group.equals("")))) {
                        errorMessage = annotation.message();
                    }
                }
            }
            
            // 모든 값이 동일한지 확인
            boolean allEqual = values.stream()
                .allMatch(value -> Objects.equals(values.get(0), value));
            
            if (!allEqual) {
                // 각 필드에 대해 오류 메시지 추가
                for (String fieldName : fieldNames) {
                    context.buildConstraintViolationWithTemplate(
                        errorMessage != null ? errorMessage : \"필드 값들이 일치하지 않습니다\"
                    )
                    .addPropertyNode(fieldName)
                    .addConstraintViolation();
                }
                return false;
            }
            
            return true;
            
        } catch (IllegalAccessException e) {
            throw new RuntimeException(\"필드 접근 중 오류가 발생했습니다: \" + e.getMessage(), e);
        }
    }
}
