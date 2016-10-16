package ru.spbau;


import java.util.ArrayList;

import static java.util.Collections.reverse;

/** Класс, с методами, которые применяют функции к итерируемым объектам.*/
public class Collections {

    /** Применяет функцию f ко всем элементам a и возвращает [f(a1), ..., f(an)].*/
    public static <V, T> Iterable<V> map(Function1<T, V> f, Iterable<? extends T> a) {
        ArrayList<V> arrayList = new ArrayList<V>();
        for (T element : a) {
            arrayList.add(f.apply(element));
        }
        return arrayList;
    }

    /** Принимает p и a, возвращает список, содержащий элементы ai, на которых p(ai) == true.*/
    public static <T> Iterable<T> filter(Predicate<T> p, Iterable<? extends T> a) {
        ArrayList<T> arrayList = new ArrayList<T>();
        for (T element : a) {
            if (p.apply(element)) {
                arrayList.add(element);
            }
        }
        return arrayList;
    }

    /** Принимает p и a, возвращает список с началом a до первого элемента ai, для которого p(ai) == false.*/
    public static <T> Iterable<T> takeWhile(Predicate<T> p, Iterable<? extends T> a) {
        ArrayList<T> arrayList = new ArrayList<T>();
        for (T element : a) {
            if (p.apply(element)) {
                arrayList.add(element);
            } else {
                return arrayList;
            }
        }
        return arrayList;
    }

    /**
     * Принимает функцию двух аргументов, начальное значение и коллекцию.
     * Возвращает результат лево ассоциативной свертки.
     * @param f -- функция свертки.
     * @param first -- начальное значение.
     * @param a -- коллекция.
     */
    public static <X, Y> X foldl(Function2<X, Y, X> f, X first, Iterable<? extends Y> a) {
        for (Y element : a) {
            first = f.apply(first, element);
        }
        return first;
    }

    /**
     * Принимает функцию двух аргументов, начальное значение и коллекцию.
     * Возвращает результат право ассоциативной свертки.
     * @param f -- функция свертки.
     * @param first -- начальное значение.
     * @param a -- коллекция.
     */
    public static <X, Y> X foldr(Function2<Y, X, X> f, X first, Iterable<? extends Y> a) {
        ArrayList<Y> arrayList = new ArrayList<Y>();
        for (Y element : a) {
            arrayList.add(element);
        }
        reverse(arrayList);
        for (Y element : arrayList) {
            first = f.apply(element, first);
        }
        return first;
    }

}