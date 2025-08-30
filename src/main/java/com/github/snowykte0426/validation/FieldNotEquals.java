package com.github.snowykte0426.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FieldNotEqualsValidator.class)
@Documented
@Repeatable(FieldNotEquals.List.class)
public @interface FieldNotEquals {

    String group() default "";

    String message() default "필드 값들이 서로 달라야 합니다";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        FieldNotEquals[] value();
    }
}
