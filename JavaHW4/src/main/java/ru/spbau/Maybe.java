package ru.spbau;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/** Maybe -- generic-класс, контейнер из одного элемента, в котором может быть, а может и не быть значение. */
public class Maybe<T> {

    private T value = null;

    /** Метод just(t) создаёт новый объект типа Maybe, хранящий в себе значение t. */
    public static <T> Maybe<T> just(@NotNull T t) {
        return new Maybe<T>(t);
    }

    /** Метод nothing() создаёт новый объект типа Maybe без хранимого значения. */
    public static <T> Maybe<T> nothing() {
        return new Maybe<T>();
    }

    /** Метод get() возвращает хранимое значение, если оно есть, бросает исключение, если значения нет. */
    public T get() throws UnsupportedOperationException {
        if (value == null) {
            throw new UnsupportedOperationException();
        }
        return value;
    }

    /** Метод isPresent() возвращает true, если значение есть, и false, если нет. */
    public boolean isPresent() {
        return (value != null);
    }

    /**
     * Метод map() принимает функцию и возвращает новый объект Maybe со значением, полученным
     * применением функции к хранимому значению, или пустой Maybe, если текущий Maybe пустой.
     */
    public <U> Maybe<U> map(Function<? super T, ? extends U> mapper) {
        if (value == null) {
            return new Maybe<>();
        }
        return new Maybe<>(mapper.apply(value));
    }

    private Maybe() {}

    private Maybe(T t) {
        value = t;
    }
}