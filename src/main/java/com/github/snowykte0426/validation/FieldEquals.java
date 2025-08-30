package com.github.snowykte0426.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 지정된 필드들이 동일한 값을 가져야 함을 검증하는 어노테이션입니다.
 * 
 * <p>사용 예시:</p>
 * <pre>
 * {@code
 * @FieldEquals(fields = {"password", "passwordConfirm"}, message = "비밀번호가 일치하지 않습니다")
 * public class UserForm {
 *     private String password;
 *     private String passwordConfirm;
 * }
 * }
 * </pre>
 * 
 * @author Kim Tae Eun
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FieldEqualsValidator.class)
@Documented
@Repeatable(FieldEquals.List.class)
public @interface FieldEquals {
    
    /**
     * 검증할 필드명들을 지정합니다.
     * 
     * @return 검증할 필드명 배열
     */
    String[] fields();
    
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
     * 같은 클래스에 여러 개의 @FieldEquals 어노테이션을 적용할 때 사용됩니다.
     */
    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        FieldEquals[] value();
    }
}