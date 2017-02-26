package ru.spbau;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Реализация интерфейса Lazy с гарантией корректной работы в многопоточном режиме
 * Вычисление запускается не более одного раза
 */
public class LazySynchronized<T> implements Lazy<T> {

    private volatile Object value = Nothing.getValue();

    private Supplier<T> supplier;

    public LazySynchronized(@NotNull Supplier<T> supplier) {
        this.supplier = supplier;
    }

    /**
     * - Первый вызов get() вызывает вычисление и возвращает результат
     * - Повторные вызовы get() возвращают тот же объект, что и первый вызов
     * - Вычисление запускается не более одного раза
     */
    @SuppressWarnings("unchecked")
    public @Nullable T get() {
        if (value == Nothing.getValue()) {
            synchronized(this) {
                if (value == Nothing.getValue()) {
                    value = supplier.get();
                    supplier = null;
                }
            }
        }
        return (T)value;
    }

}
