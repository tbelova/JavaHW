package ru.spbau;


/**
 * Класс, описывающий предикат.
 * @param <T> -- тип аргумента предиката.
 */
public interface Predicate<T> extends Function1<T, Boolean> {

    public static <U> Predicate<U> ALWAYS_TRUE() {
        return new Predicate<U>() {
            @Override
            public Boolean apply(U x) {
                return true;
            }
        };
    }

    public static <U> Predicate<U> ALWAYS_FALSE() {
        return new Predicate<U>() {
            @Override
            public Boolean apply(U x) {
                return false;
            }
        };
    }

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

}
