package ru.spbau;


/**
 * Класс, описывающий функцию двух аргументов.
 * @param <X> -- тип первого аргумента.
 * @param <Y> -- тип второго аргумента.
 * @param <V> -- тип результата функции.
 */
public interface Function2<X, Y, V> {

    /**
     * Композиция.
     * Принимает “Function1 g”, возвращает “g(f(x, y))”.
     */
    default <U>Function2<X, Y, U> compose(Function1<V, U> g) {
        return (x, y) -> g.apply(apply(x, y));
    }

    /**
     * bind первого аргумента.
     * Принимает первый аргумент, возвращает “f(_, y)”.
     */
    default Function1<Y, V> bind1(X x) {
        return y -> apply(x, y);
    }

    /**
     * bind второго аргумента.
     * Принимает второй аргумент, возвращает “f(x, _)”.
     */
    default Function1<X, V> bind2(Y y) {
        return x -> apply(x, y);
    }

    /* Каррирование, конвертация в “Function1”.*/
    default Function1<X, Function1<Y, V>> curry() {
        return this::bind1;
    }

    /**
     * Применение функции.
     * Принимает x, y.
     * Возвращает f(x, y).
     */
    V apply(X x, Y y);

}
