package ru.spbau;


public interface Predicate<T> {

    Predicate ALWAYS_TRUE = x -> true;
    Predicate ALWAYS_FALSE = x -> false;

    default Predicate<T> or(Predicate<T> p) {
        return x -> apply(x) || p.apply(x);
    }

    default Predicate<T> and(Predicate<T> p) {
        return x -> apply(x) && p.apply(x);
    }

    default Predicate<T> not() {
        return x -> !apply(x);
    }

    boolean apply(T x);

}
