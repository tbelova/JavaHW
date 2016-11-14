package ru.spbau;


/**
 * Класс, описывающий функцию одной переменной.
 * @param <ARG> -- тип аргумента функции.
 * @param <RESULT> -- тип результата функции.
 */
public interface Function1<ARG, RESULT> {

    /**
     * Композиция.
     * Принимает “Function1 g”, возвращает “g(f(x))”.
     */
    default <U> Function1<ARG, U> compose(Function1<? super RESULT, U> g) {
        return x -> g.apply(Function1.this.apply(x));
    }

    /**
     * Применение функции.
     * Принимает x, возвращает f(x)
     */
    RESULT apply(ARG x);

}
