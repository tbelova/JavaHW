package ru.spbau;


/**
 * Класс, описывающий функцию двух аргументов.
 * @param <ARGX> -- тип первого аргумента.
 * @param <ARGY> -- тип второго аргумента.
 * @param <RESULT> -- тип результата функции.
 */
public interface Function2<ARGX, ARGY, RESULT> {

    /**
     * Композиция.
     * Принимает “Function1 g”, возвращает “g(f(x, y))”.
     */
    default <U>Function2<ARGX, ARGY, U> compose(Function1<RESULT, U> g) {
        return (x, y) -> g.apply(apply(x, y));
    }

    /**
     * bind первого аргумента.
     * Принимает первый аргумент, возвращает “f(_, y)”.
     */
    default Function1<ARGY, RESULT> bind1(ARGX x) {
        return y -> apply(x, y);
    }

    /**
     * bind второго аргумента.
     * Принимает второй аргумент, возвращает “f(x, _)”.
     */
    default Function1<ARGX, RESULT> bind2(ARGY y) {
        return x -> apply(x, y);
    }

    /* Каррирование, конвертация в “Function1”.*/
    default Function1<ARGX, Function1<ARGY, RESULT>> curry() {
        return this::bind1;
    }

    /**
     * Применение функции.
     * Принимает x, y.
     * Возвращает f(x, y).
     */
    RESULT apply(ARGX x, ARGY y);

}
