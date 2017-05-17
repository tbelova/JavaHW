package ru.spbau;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс, отвечающий за тестирование классов.
 */
public class Main {

    /**
     * Принимает путь и выполняет запуск тестов, расположенных по этому пути.
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException {

        if (args.length != 1) {
            System.out.println("Wrong number of arguments.");
            return;
        }

        Path path = Paths.get(args[0]);

        List<Path> files = Files.list(path).collect(Collectors.toList());

        for (Path file: files) {

            Class classToTest = Class.forName(file.toString());

            Tester tester = new Tester(classToTest);

            List<MethodWithResult> results = tester.test();

            for (MethodWithResult methodWithResult: results) {
                if (methodWithResult.getResult().getType() != Type.NO_ANNOTATION) {
                    System.out.println(path + ": " + methodWithResult.toString());
                }
            }

        }

    }

}
