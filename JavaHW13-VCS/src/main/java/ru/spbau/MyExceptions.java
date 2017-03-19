package ru.spbau;

public class MyExceptions {

    public static class IsNotDirectoryException extends Exception {}

    public static class IsNotFileException extends Exception {}

    public static class WrongFormatException extends Exception {}

    public static class NotFoundException extends Exception {}

    public static class AlreadyExistsException extends Exception {}


}
