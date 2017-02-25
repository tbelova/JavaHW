package ru.spbau;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class Lazy3<T> implements Lazy<T> {

    private AtomicReference<Object> value = new AtomicReference<>(Nothing.getValue());

    private Supplier<T> supplier;

    public Lazy3(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public T get() {
        value.compareAndSet(Nothing.getValue(), supplier.get());
        return (T)value;
    }

}

