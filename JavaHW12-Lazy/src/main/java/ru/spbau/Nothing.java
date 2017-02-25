package ru.spbau;

public class Nothing {
    private static Nothing value = new Nothing();

    public static Nothing getValue() {
        return value;
    }
}
