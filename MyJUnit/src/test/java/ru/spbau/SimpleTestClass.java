package ru.spbau;

import java.io.IOException;

public class SimpleTestClass {

    public static void shouldNotBeTested() throws Exception {}

    @Test
    public static void empty() throws Exception {}

    @Test(expected = NullPointerException.class)
    public static void throwsNullPointerException() throws Exception {
        throw new NullPointerException();
    }

    @Test(expected = IOException.class)
    public static void throwsNullPointerExceptionAndExpectedToThrowIOException() throws Exception {
        throw new NullPointerException();
    }

}
