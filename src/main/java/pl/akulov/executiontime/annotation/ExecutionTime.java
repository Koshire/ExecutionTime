package pl.akulov.executiontime.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ExecutionTime {

    String message() default "";

    String level() default "INFO";

    boolean dynamicLevel() default false;

    int infoLimit() default 100;

    int warnLimit() default 500;
}
