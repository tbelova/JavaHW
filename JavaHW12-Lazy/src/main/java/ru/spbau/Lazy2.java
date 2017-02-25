package ru.spbau;

import java.util.function.Supplier;

public class Lazy2<T> implements Lazy<T> {

    private volatile Object value = Nothing.getValue();

    private Supplier<T> supplier;

    public Lazy2(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public T get() {
        if (value == Nothing.getValue()) {
            synchronized(this) {
                if (value == Nothing.getValue()) {
                    value = supplier.get();
                }
            }
        }
        return (T)value;
    }

}
