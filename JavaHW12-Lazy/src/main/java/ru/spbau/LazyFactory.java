package ru.spbau;

import java.util.function.Supplier;

public class LazyFactory {

    public static <T> Lazy<T> createLazy1(Supplier<T> supplier) {
        return new Lazy1<>(supplier);
    }

    public static <T> Lazy<T> createLazy2(Supplier<T> supplier) {
        return new Lazy2<>(supplier);
    }

    public static <T> Lazy<T> createLazy3(Supplier<T> supplier) {
        return new Lazy3<>(supplier);
    }

}
