package ru.spbau;


/**
 * Класс, описывающий функцию одной переменной.
 * @param <T> -- тип аргумента функции.
 * @param <V> -- тип результата функции.
 */
public interface Function1<T, V> {

    /**
     * Композиция.
     * Принимает “Function1 g”, возвращает “g(f(x))”.
     */
    default <U>Function1<T, U> compose(Function1<V, U> g) {
        return x -> g.apply(Function1.this.apply(x));
    }

    /**
     * Применение функции.
     * Принимает x, возвращает f(x)
     */
    V apply(T x);

}
