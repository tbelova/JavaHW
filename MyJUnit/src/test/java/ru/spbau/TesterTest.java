package ru.spbau;

import org.junit.*;
import org.junit.Test;

import java.lang.reflect.*;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TesterTest {

    @org.junit.Test
    public void testSimpleTestClass() throws Exception {

        List<MethodWithResult> results = Tester.test(SimpleTestClass.class);

        for (MethodWithResult methodWithResult: results) {

            Method method = methodWithResult.getMethod();
            Result result = methodWithResult.getResult();

            if (method.equals(SimpleTestClass.class.getMethod("shouldNotBeTested"))) {
                assertEquals(Type.NO_ANNOTATION, result.getType());
            } else if (method.equals(SimpleTestClass.class.getMethod("empty"))) {
                assertEquals(Type.CORRECT, result.getType());
            } else if (method.equals(SimpleTestClass.class.getMethod("throwsNullPointerException"))) {
                assertEquals(Type.CORRECT, result.getType());
            } else if (method.equals(SimpleTestClass.class.getMethod("throwsNullPointerExceptionAndExpectedToThrowIOException"))) {
                assertEquals(Type.FAIL, result.getType());
            } else {
                assertEquals(Type.NO_ANNOTATION, result.getType());
            }

        }

    }

}