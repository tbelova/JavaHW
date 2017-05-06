package ru.spbau;

/** Класс, содержащий различные исключения, которые могут кидать методы класса Repository.*/
public class MyExceptions {

    /** Исключение, которое бросается при попытке работать с путем, как с директорией, когда он ею не является.*/
    public static class IsNotDirectoryException extends Exception {}

    /** Исключение, которое бросается при попытке работать с путем, как с файлом, когда он им не является.*/
    public static class IsNotFileException extends Exception {}

    /** Исключение, которое бросается в случае неправильной работы программы, не зависящей от пользователя.*/
    public static class UnknownProblem extends Exception {
        static final String defaultMessage = "Unknown problem";
    }

    /** Исключение, которое бросается при попытке работать с несуществубщей директорией.*/
    public static class NotFoundException extends Exception {}

    /** Исключение, которое бросается, если существует директория, которой быть не должно.*/
    public static class AlreadyExistsException extends Exception {}

    /** Исключение, которое бросается при неправильном вводе пользователя.*/
    public static class WrongFormatException extends Exception {}

}
