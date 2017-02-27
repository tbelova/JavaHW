package ru.spbau;

/**
 * Класс, умеющий возвращать объект, символизирующий отсутствие значения
 */
public class Nothing {
    private static Nothing value = new Nothing();

    public static Nothing getValue() {
        return value;
    }
}
