package ru.spbau;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Методы, помеченные данной аннотацией, считаются тестами и будут запущены при передаче класса в Tester.test()
 * Аргументы:
 * - expected -- исключение, которое ожидается при завершении метода
 * - ignore -- непустая строка с указанием причины отмена запуска
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Test {

    Class expected() default DefaultException.class;
    String ignore() default shouldNotIgnore;

    class DefaultException {}
    String shouldNotIgnore = "";

}
