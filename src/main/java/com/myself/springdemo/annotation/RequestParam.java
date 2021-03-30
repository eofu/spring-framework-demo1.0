package com.myself.springdemo.annotation;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})//字段上
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestParam {
	String value() default "";
}
