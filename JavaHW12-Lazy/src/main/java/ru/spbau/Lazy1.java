package ru.spbau;

import java.util.function.Supplier;

public class Lazy1<T> implements Lazy<T> {

    private Object value = Nothing.getValue();

    private Supplier<T> supplier;

    public Lazy1(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public T get() {
        if (value == Nothing.getValue()) {
            value = supplier.get();
        }
        return (T)value;
    }

}
