package com.myself.springdemo.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})//类上,方法上
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {
	String value() default "";
}
