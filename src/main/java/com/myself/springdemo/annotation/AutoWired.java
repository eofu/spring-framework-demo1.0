package com.myself.springdemo.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD})//字段上
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AutoWired {
	String value() default "";
}
