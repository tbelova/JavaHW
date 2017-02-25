package ru.spbau;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * Lock-free реализация интерфейса Lazy с гарантией корректной работы в многопоточном режиме
 * Вычисление может производиться более одного раза
 * Lazy.get всегда возвращает один и тот же объект
 */
public class Lazy3<T> implements Lazy<T> {

    private AtomicReference<Object> value = new AtomicReference<>(Nothing.getValue());

    private Supplier<T> supplier;

    public Lazy3(@NotNull Supplier<T> supplier) {
        this.supplier = supplier;
    }

    /**
     * - Первый вызов get() вызывает вычисление и возвращает результат
     * - Повторные вызовы get() возвращают тот же объект, что и первый вызов
     * - Вычисление может производиться более одного раза
     */
    @SuppressWarnings("unchecked")
    public @Nullable T get() {
        value.compareAndSet(Nothing.getValue(), supplier.get());
        return (T)value.get();
    }

}

