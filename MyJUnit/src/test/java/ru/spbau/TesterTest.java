package ru.spbau;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TesterTest {

    @org.junit.Test
    public void testSimpleTestClass() throws Exception {

        Tester tester = new Tester(SimpleTestClass.class);

        List<MethodWithResult> results = tester.test();

        for (MethodWithResult methodWithResult: results) {

            Method method = methodWithResult.getMethod();
            Result result = methodWithResult.getResult();

            if (method.equals(SimpleTestClass.class.getMethod("empty"))) {
                assertEquals(Type.CORRECT, result.getType());
            } else if (method.equals(SimpleTestClass.class.getMethod("throwsNullPointerException"))) {
                assertEquals(Type.CORRECT, result.getType());
            } else if (method.equals(SimpleTestClass.class.getMethod("throwsNullPointerExceptionAndExpectedToThrowIOException"))) {
                assertEquals(Type.FAIL, result.getType());
            } else {
                assertTrue(false);
            }

        }

    }

    @org.junit.Test
    public void testSimpleTestClassWithBeforeAndAfter() throws Exception {

        Tester tester = new Tester(SimpleTestClassWithBeforeAndAfter.class);

        List<MethodWithResult> results = tester.test();

        for (MethodWithResult methodWithResult: results) {

            Method method = methodWithResult.getMethod();
            Result result = methodWithResult.getResult();

            if (method.equals(SimpleTestClassWithBeforeAndAfter.class.getMethod("testOk"))) {
                assertEquals(Type.CORRECT, result.getType());
            } else if (method.equals(SimpleTestClassWithBeforeAndAfter.class.getMethod("testFail"))) {
                assertEquals(Type.FAIL, result.getType());
            } else if (method.equals(SimpleTestClassWithBeforeAndAfter.class.getMethod("testFailInAfter"))) {
                assertEquals(Type.FAIL, result.getType());
            } else {
                assertTrue(false);
            }

        }

    }

}
