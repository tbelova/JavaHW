package ru.spbau;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/** Класс, отвечающий за работу с консолью.*/
public class Main {

    /**
     * Принимает строку, задающую путь до дириктории/файла.
     * Пишет значение check-суммы.
     * Пишет, сколько времени работает каждый вариант подсчета check-суммы.
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Wrong number of arguments.");
            return;
        }
        try {
            Path path = Paths.get(args[0]);
            System.out.println(Arrays.toString(SingleThreadCheckSum.checkSum(path)));

            long start1 = System.nanoTime();
            SingleThreadCheckSum.checkSum(path);
            long elapsedTime1 = System.nanoTime() - start1;

            long start2 = System.nanoTime();
            SingleThreadCheckSum.checkSum(path);
            long elapsedTime2 = System.nanoTime() - start2;

            System.out.println("Single thread algorithm: " + elapsedTime1 + " nanos" +
                    "\nForkJoin algorithm: " + elapsedTime2 + " nanos");

        } catch (FileNotFoundException e) {
            System.out.println("File is not found.");
        }
    }

}
