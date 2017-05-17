package ru.spbau;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Класс, хранящий результат работы некоторого теста.
 */
public class Result {

    private Type type;
    private String cause;
    private long time;

    /**
     * Возвращает объект, соответствующий корректному завершению теста.
     */
    public static @NotNull Result getCorrect(long time) {
        return new Result(Type.CORRECT, null, time);
    }

    /**
     * Возвращает объект, соответствующий некорректному завершению теста.
     */
    public static @NotNull Result getFail(long time) {
        return new Result(Type.FAIL, null, time);
    }

    /**
     * Принимает строку с причиной отмены запуска теста.
     * Возвращает объект, соответствующий тому, что тест был проигнорирован с указанной причиной.
     */
    public static @NotNull Result getIgnored(@NotNull String s) {
        return new Result(Type.IGNORED, s, 0);
    }

    /**
     * Возвращает тип результата теста.
     */
    public @NotNull Type getType() {
        return type;
    }

    /**
     * Конвертирует результат выполнения теста в строку.
     */
    @Override
    public @NotNull String toString() {

        if (type == Type.CORRECT) {
            return "OK(" + time + "ms)";
        }

        if (type == Type.FAIL) {
            return "FAIL(" + time + "ms)";
        }

        if (type == Type.IGNORED) {
            return "IGNORED: " + cause;
        }

        return "";

    }

    private Result(@NotNull Type type, @Nullable String cause, long time) {
        this.type = type;
        this.cause = cause;
        this.time = time;
    }

}
