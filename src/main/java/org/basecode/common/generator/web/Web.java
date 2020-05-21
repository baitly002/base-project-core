package org.basecode.common.generator.web;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
@Documented
public @interface Web {
    String[] value() default {};
}