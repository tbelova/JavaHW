package ru.spbau;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Result {

    private Type type;
    private String cause;

    public static Result getCorrect() {
        return new Result(Type.CORRECT, null);
    }

    public static Result getFail() {
        return new Result(Type.FAIL, null);
    }

    public static Result getIgnored(@NotNull String s) {
        return new Result(Type.IGNORED, s);
    }

    public static Result getNoAnnotation() {
        return new Result(Type.NO_ANNOTATION, null);
    }

    public @NotNull Type getType() {
        return type;
    }

    @Override
    public @NotNull String toString() {

        if (type == Type.CORRECT) {
            return "OK";
        }

        if (type == Type.FAIL) {
            return "FAIL";
        }

        if (type == Type.IGNORED) {
            return "IGNORED: " + cause;
        }

        return "";

    }

    private Result(@NotNull Type type, @Nullable String cause) {
        this.type = type;
        this.cause = cause;
    }

}
