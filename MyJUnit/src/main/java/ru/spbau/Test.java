package ru.spbau;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Test {

    Class expected() default DefaultException.class;
    String ignore() default shouldNotIgnore;

    class DefaultException {}
    String shouldNotIgnore = "";

}
