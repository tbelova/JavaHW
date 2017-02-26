package ru.spbau;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.function.Supplier;

/**
 * Lock-free реализация интерфейса Lazy с гарантией корректной работы в многопоточном режиме
 * Вычисление может производиться более одного раза
 * Lazy.get всегда возвращает один и тот же объект
 */
public class LazyLockFree<T> implements Lazy<T> {

    //private AtomicReference<Object> value = new AtomicReference<>(Nothing.getValue());

    private volatile Object value;

    private static final AtomicReferenceFieldUpdater<LazyLockFree, Object> fieldUpdater =
            AtomicReferenceFieldUpdater.newUpdater(LazyLockFree.class, Object.class, "value");

    private Supplier<T> supplier;

    public LazyLockFree(@NotNull Supplier<T> supplier) {
        this.supplier = supplier;
    }

    /**
     * - Первый вызов get() вызывает вычисление и возвращает результат
     * - Повторные вызовы get() возвращают тот же объект, что и первый вызов
     * - Вычисление может производиться более одного раза
     */
    @SuppressWarnings("unchecked")
    public @Nullable T get() {
        Supplier<T> tmpSupplier = supplier;
        if (tmpSupplier != null) {
            fieldUpdater.compareAndSet(this, Nothing.getValue(), tmpSupplier.get());
            supplier = null;
        }
        return (T)value;
    }

}

