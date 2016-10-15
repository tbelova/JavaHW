package ru.spbau;


public interface Function1<T, V> {

    default <U>Function1<T, U> compose(Function1<V, U> g) {
        return x -> g.apply(Function1.this.apply(x));
    }

    V apply(T x);

}
