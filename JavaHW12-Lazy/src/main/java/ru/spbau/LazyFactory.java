package ru.spbau;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Класс, умеющий создавать три разные реализации Lazy:
 * - LazyOneThread
 * - LazySynchronized
 * - LazyLockFree
 */
public class LazyFactory {

    /** Возвращает объект LazyOneThread */
    public static <T> @NotNull Lazy<T> createLazyOneThread(@NotNull Supplier<T> supplier) {
        return new LazyOneThread<>(supplier);
    }

    /** Возвращает объект LazySynchronized */
    public static <T> @NotNull Lazy<T> createLazySynchronized(@NotNull Supplier<T> supplier) {
        return new LazySynchronized<>(supplier);
    }

    /** Возвращает объект LazyLockFree */
    public static <T> @NotNull Lazy<T> createLazyLockFree(@NotNull Supplier<T> supplier) {
        return new LazyLockFree<>(supplier);
    }

}
