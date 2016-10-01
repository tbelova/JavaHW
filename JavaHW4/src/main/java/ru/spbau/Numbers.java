package ru.spbau;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.function.Function;

/** Класс со статическими методами readNumber и toSqrt. */
public class Numbers {

    /**
     * Если на строке число, возвращается “Maybe” с числом.
     * Если на строке записано не число, возвращается “Maybe” с null.
     */
    public static Maybe readNumber(InputStream in) {
        try (Scanner scanner = new Scanner(new BufferedInputStream(in))) {
            if (scanner.hasNextInt()) {
                return Maybe.just(scanner.nextInt());
            } else {
                return Maybe.nothing();
            }
        }
    }

    /**
     * Метод toSqrt() возводит в квадрат прочитанные числа и сохраняет их в новый файл.
     * Если на строке было не число, сохраняет их в новый файл.
     */
    public static void toSqrt(InputStream in, OutputStream out) {
        Function<Integer, Integer> sqrt = a -> a * a;
        try (Scanner scanner = new Scanner(new BufferedInputStream(in)); PrintWriter writer = new PrintWriter(out)) {
            while (scanner.hasNext()) {
                if (!scanner.hasNextInt()) {
                    writer.println("null");
                    scanner.next();
                } else {
                    writer.println(Maybe.just(scanner.nextInt()).map(sqrt).get());
                }
            }
        }
    }

}
