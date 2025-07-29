package dgu.umc_app.global.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentUser {
    boolean required() default true;
    String errorMessage() default "로그인이 필요합니다.";
} 