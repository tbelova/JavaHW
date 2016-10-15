package ru.spbau;


import java.util.ArrayList;

import static java.util.Collections.reverse;

public class Collections {

    public static <V,T>Iterable<V> map(Function1<T, V> f, Iterable<? extends T> a) {
        ArrayList<V> arrayList = new ArrayList<V>();
        for (T element: a) {
            arrayList.add(f.apply(element));
        }
        return  arrayList;
    }

    public static <T>Iterable<T> filter(Predicate<T> p, Iterable<? extends T> a) {
        ArrayList<T> arrayList = new ArrayList<T>();
        for (T element: a) {
            if (p.apply(element)) {
                arrayList.add(element);
            }
        }
        return arrayList;
    }

    public static <T>Iterable<T> takeWhile(Predicate<T> p, Iterable<? extends T> a) {
        ArrayList<T> arrayList = new ArrayList<T>();
        for (T element: a) {
            if (p.apply(element)) {
                arrayList.add(element);
            } else {
                return arrayList;
            }
        }
        return arrayList;
    }

    public static <X, Y> X foldl(Function2<X, Y, X> f, X first, Iterable<? extends Y> a) {
        for (Y element: a) {
            first = f.apply(first, element);
        }
        return first;
    }

    public static <X, Y> X foldr(Function2<X, Y, X> f, X first, Iterable<? extends Y> a) {
        ArrayList<Y> arrayList = new ArrayList<Y>();
        for (Y element: a) {
            arrayList.add(element);
        }
        reverse(arrayList);
        return foldl(f, first, arrayList);
    }

}
