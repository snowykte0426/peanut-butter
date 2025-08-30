package com.github.snowykte0426.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FieldEqualsValidator.class)
@Documented
public @interface FieldEquals {

    String group() default "";

    String message() default "필드 값들이 일치하지 않습니다";

    Class<?>[] groups() default {};
    

    Class<? extends Payload>[] payload() default {};

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        FieldEquals[] value();
    }
}
