package ru.spbau;

import java.util.function.Supplier;

/**
 * Класс, умеющий создавать три разные реализации Lazy:
 * - Lazy1
 * - Lazy2
 * - Lazy3
 */
public class LazyFactory {

    /** Возвращает объект Lazy1 */
    public static <T> Lazy<T> createLazy1(Supplier<T> supplier) {
        return new Lazy1<>(supplier);
    }

    /** Возвращает объект Lazy2 */
    public static <T> Lazy<T> createLazy2(Supplier<T> supplier) {
        return new Lazy2<>(supplier);
    }

    /** Возвращает объект Lazy3 */
    public static <T> Lazy<T> createLazy3(Supplier<T> supplier) {
        return new Lazy3<>(supplier);
    }

}
