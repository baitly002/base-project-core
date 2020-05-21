package org.basecode.common.criterion.annotations;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DictField {
    /**
     * 参数中文含义 标题
     */
    String value();
}
