package ru.spbau;

import java.io.IOException;

public class SimpleTestClass {

    public void shouldNotBeTested() throws Exception {}

    @Test
    public void empty() throws Exception {}

    @Test(expected = NullPointerException.class)
    public void throwsNullPointerException() throws Exception {
        throw new NullPointerException();
    }

    @Test(expected = IOException.class)
    public void throwsNullPointerExceptionAndExpectedToThrowIOException() throws Exception {
        throw new NullPointerException();
    }

}
