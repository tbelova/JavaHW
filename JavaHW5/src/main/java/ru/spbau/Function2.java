package ru.spbau;


public interface Function2<X, Y, V> {

    default <U>Function2<X, Y, U> compose(Function1<V, U> g) {
        return (x, y) -> g.apply(apply(x, y));
    }

    default Function1<Y, V> bind1(X x) {
        return y -> apply(x, y);
    }

    default Function1<X, V> bind2(Y y) {
        return x -> apply(x, y);
    }

    default Function1<X, Function1<Y, V>> curry() {
        return this::bind1;
    }

    V apply(X x, Y y);


}
