package ru.spbau;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

/**
 * Класс, хранящий метод и результат его запуска.
 */
public class MethodWithResult {

    private Method method;
    private Result result;

    /**
     * Просто конструктор.
     */
    public MethodWithResult(@NotNull Method method, @NotNull Result result) {
        this. method = method;
        this.result = result;
    }

    /**
     * Возвращает результат запуска метода.
     */
    public @NotNull Result getResult() {
        return result;
    }

    /**
     * Возвращает метод.
     */
    public @NotNull Method getMethod() {
        return method;
    }

    /**
     * Конвертирует содержимое объекта в строку.
     */
    @Override
    public @NotNull String toString() {
        return method.getName() + ": " + result.toString();
    }

}
