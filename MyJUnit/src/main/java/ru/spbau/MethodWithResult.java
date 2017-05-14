package ru.spbau;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

public class MethodWithResult {

    private Method method;
    private Result result;

    public MethodWithResult(@NotNull Method method, @NotNull Result result) {
        this. method = method;
        this.result = result;
    }

    public @NotNull Result getResult() {
        return result;
    }

    public @NotNull Method getMethod() {
        return method;
    }

    @Override
    public @NotNull String toString() {
        return method.getName() + ": " + result.toString();
    }

}
