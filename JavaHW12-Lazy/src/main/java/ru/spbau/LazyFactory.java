package ru.spbau;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Класс, умеющий создавать три разные реализации Lazy:
 * - Lazy1
 * - Lazy2
 * - Lazy3
 */
public class LazyFactory {

    /** Возвращает объект Lazy1 */
    public static <T> @NotNull Lazy<T> createLazy1(@NotNull Supplier<T> supplier) {
        return new Lazy1<>(supplier);
    }

    /** Возвращает объект Lazy2 */
    public static <T> @NotNull Lazy<T> createLazy2(@NotNull Supplier<T> supplier) {
        return new Lazy2<>(supplier);
    }

    /** Возвращает объект Lazy3 */
    public static <T> @NotNull Lazy<T> createLazy3(@NotNull Supplier<T> supplier) {
        return new Lazy3<>(supplier);
    }

}
