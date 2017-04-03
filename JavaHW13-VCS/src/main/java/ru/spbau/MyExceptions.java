package ru.spbau;

/** Класс, содержащий различные исключения, которые могут кидать методы класса Repository.*/
public class MyExceptions {

    public static class IsNotDirectoryException extends Exception {}

    public static class IsNotFileException extends Exception {}

    public static class UnknownProblem extends Exception {}

    public static class NotFoundException extends Exception {}

    public static class AlreadyExistsException extends Exception {}

    public static class WrongFormatException extends Exception {}

}
