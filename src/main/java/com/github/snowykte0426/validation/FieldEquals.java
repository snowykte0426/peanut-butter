package com.github.snowykte0426.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 같은 그룹 내의 필드들이 동일한 값을 가져야 함을 검증하는 어노테이션입니다.
 * 
 * <p>사용 예시:</p>
 * <pre>
 * {@code
 * @FieldEquals(group = "phone", message = "전화번호가 일치하지 않습니다")
 * private String phoneNumber1;
 * 
 * @FieldEquals(group = "phone", message = "전화번호가 일치하지 않습니다")
 * private String phoneNumber2;
 * }
 * </pre>
 * 
 * @author Kim Tae Eun
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FieldEqualsValidator.class)
@Documented
@Repeatable(FieldEquals.List.class)
public @interface FieldEquals {
    
    /**
     * 검증 그룹을 지정합니다. 같은 그룹 내의 필드들끼리 값이 동일한지 검증합니다.
     * 기본값은 빈 문자열이며, 이 경우 클래스 내 모든 @FieldEquals 어노테이션이 적용된 필드가 하나의 그룹으로 묶입니다.
     * 
     * @return 검증 그룹 이름
     */
    String group() default "";
    
    /**
     * 검증 실패 시 표시할 메시지입니다.
     * 
     * @return 오류 메시지
     */
    String message() default "필드 값들이 일치하지 않습니다";
    
    /**
     * 검증 그룹을 지정합니다.
     * 
     * @return 검증 그룹
     */
    Class<?>[] groups() default {};
    
    /**
     * 페이로드를 지정합니다.
     * 
     * @return 페이로드
     */
    Class<? extends Payload>[] payload() default {};
    
    /**
     * 같은 필드에 여러 개의 @FieldEquals 어노테이션을 적용할 때 사용됩니다.
     */
    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        FieldEquals[] value();
    }
}
