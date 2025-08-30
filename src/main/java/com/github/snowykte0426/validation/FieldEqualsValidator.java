package com.github.snowykte0426.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class FieldEqualsValidator implements ConstraintValidator<FieldEquals, Object> {
    
    private static final Map<Class<?>, Map<String, List<Field>>> fieldCache = new HashMap<>();
    
    @Override
    public void initialize(FieldEquals constraintAnnotation) {
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
                    FieldEquals annotation = field.getAnnotation(FieldEquals.class);
                    if (annotation != null && (annotation.group().equals(group) || (annotation.group().isEmpty() && group.equals("")))) {
                        errorMessage = annotation.message();
                    }
                }
            }

            boolean allEqual = values.stream()
                .allMatch(value -> Objects.equals(values.get(0), value));
            
            if (!allEqual) {
                for (String fieldName : fieldNames) {
                    context.buildConstraintViolationWithTemplate(
                        errorMessage != null ? errorMessage : "Field values do not match"
                    )
                    .addPropertyNode(fieldName)
                    .addConstraintViolation();
                }
                return false;
            }
            
            return true;
            
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to access field values: " + e.getMessage(), e);
        }
    }
}
