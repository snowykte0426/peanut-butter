package com.github.snowykte0426.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;
import java.util.*;

public class FieldNotEqualsValidator implements ConstraintValidator<FieldNotEquals, Object> {
    
    private static final Map<Class<?>, Map<String, List<Field>>> fieldCache = new HashMap<>();
    
    @Override
    public void initialize(FieldNotEquals constraintAnnotation) {
    }
    
    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        if (object == null) {
            return true;
        }
        
        Class<?> clazz = object.getClass();
        Map<String, List<Field>> groupedFields = getGroupedFields(clazz);
        
        boolean isValid = true;
        context.disableDefaultConstraintViolation();
        
        for (Map.Entry<String, List<Field>> entry : groupedFields.entrySet()) {
            String group = entry.getKey();
            List<Field> fields = entry.getValue();
            
            if (fields.size() < 2) {
                continue;
            }
            
            if (!validateGroup(object, fields, group, context)) {
                isValid = false;
            }
        }
        
        return isValid;
    }

    private Map<String, List<Field>> getGroupedFields(Class<?> clazz) {
        return fieldCache.computeIfAbsent(clazz, this::collectGroupedFields);
    }

    private Map<String, List<Field>> collectGroupedFields(Class<?> clazz) {
        Map<String, List<Field>> groupedFields = new HashMap<>();

        Class<?> currentClass = clazz;
        while (currentClass != null && currentClass != Object.class) {
            for (Field field : currentClass.getDeclaredFields()) {
                FieldNotEquals[] annotations = field.getAnnotationsByType(FieldNotEquals.class);
                
                for (FieldNotEquals annotation : annotations) {
                    String group = annotation.group().isEmpty() ? "" : annotation.group();
                    groupedFields.computeIfAbsent(group, k -> new ArrayList<>()).add(field);
                }
            }
            currentClass = currentClass.getSuperclass();
        }
        
        return groupedFields;
    }

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

                if (errorMessage == null) {
                    FieldNotEquals annotation = field.getAnnotation(FieldNotEquals.class);
                    if (annotation != null && (annotation.group().equals(group) || (annotation.group().isEmpty() && group.equals("")))) {
                        errorMessage = annotation.message();
                    }
                }
            }

            Set<Object> uniqueValues = new HashSet<>();
            List<Object> duplicates = new ArrayList<>();
            
            for (Object value : values) {
                if (!uniqueValues.add(value)) {
                    duplicates.add(value);
                }
            }
            
            if (!duplicates.isEmpty()) {
                for (int i = 0; i < values.size(); i++) {
                    if (duplicates.contains(values.get(i))) {
                        context.buildConstraintViolationWithTemplate(
                            errorMessage != null ? errorMessage : "Field values must not be equal"
                        )
                        .addPropertyNode(fieldNames.get(i))
                        .addConstraintViolation();
                    }
                }
                return false;
            }
            
            return true;
            
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to access field values: " + e.getMessage(), e);
        }
    }
}