package ru.spbau;


/**
 * Класс, описывающий предикат.
 * @param <T> -- тип аргумента предиката.
 */
public interface Predicate<T> {

    Predicate ALWAYS_TRUE = x -> true;
    Predicate ALWAYS_FALSE = x -> false;

    /**
     * Принимает один предикат в качестве аргумента, возвращает предикат, который ведет себя,
     * как дизъюнкция текущего предиката и предиката-аргумента.
     * Семантика ленивая, как у ||.
     */
    default Predicate<T> or(Predicate<T> p) {
        return x -> apply(x) || p.apply(x);
    }

    /**
     * Принимает один предикат в качестве аргумента, возвращает предикат, который ведет себя,
     * как конъюнкция текущего предиката и предиката-аргумента.
     * Семантика ленивая, как у &&.
     */
    default Predicate<T> and(Predicate<T> p) {
        return x -> apply(x) && p.apply(x);
    }

    /** Принимает 0 аргументов, возвращает предикат-отрицание текущего предиката.*/
    default Predicate<T> not() {
        return x -> !apply(x);
    }

    /**
     * Применение предиката.
     * Принимает x.
     * Возвращает p(x).
     */
    boolean apply(T x);

}
