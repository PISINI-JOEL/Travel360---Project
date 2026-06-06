package com.cts.annotation;

import com.cts.enums.AuditEntity;
import com.cts.enums.LogType;
import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Audit {
    String action();
    AuditEntity entity();
    LogType type() default LogType.INFO;
}
