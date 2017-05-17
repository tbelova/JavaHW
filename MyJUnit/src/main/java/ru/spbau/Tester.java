package ru.spbau;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс, позволяющий тестировать классы.
 */
public class Tester {

    private Class testClass;
    private List<Method> before = new ArrayList<>();
    private List<Method> after = new ArrayList<>();
    private List<Method> beforeClass = new ArrayList<>();
    private List<Method> afterClass = new ArrayList<>();
    private List<Method> tests = new ArrayList<>();

    /**
     * Конструктор от класса, который нужно протестировать.
     */
    public Tester(@NotNull Class testClass) {

        this.testClass = testClass;

        for (Method method: testClass.getMethods()) {

            if (method.getAnnotation(Test.class) != null) {
                tests.add(method);
            }

            if (method.getAnnotation(Before.class) != null) {
                before.add(method);
            }

            if (method.getAnnotation(After.class) != null) {
                after.add(method);
            }

            if (method.getAnnotation(BeforeClass.class) != null) {
                beforeClass.add(method);
            }

            if (method.getAnnotation(AfterClass.class) != null) {
                afterClass.add(method);
            }

        }

    }

    /**
     * Принимает класс. Запускает в нем все методы, помеченные аннотацией @Test.
     * Перед и после каждого метода запускаются все методы, помеченные аннотацией @Before и @After.
     * Перед и после запуска тестов в классе запускаются все методы, помеченные аннотациями BeforeClass и AfterClass.
     */
    public @NotNull List<MethodWithResult> test() throws ClassNotFoundException {

        List<MethodWithResult> resultList = new ArrayList<>();

        for (Method method: beforeClass) {
            Result result = invoke(method, null);
            if (result.getType().equals(Type.FAIL)) {
                resultList = ignoreAll("because of BeforeClass method");
                resultList.add(new MethodWithResult(method, result));
                return resultList;
            }
        }

        for (Method method: tests) {

            Object object;

            try {
                object = testClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            resultList.add(new MethodWithResult(method, invoke(method, object)));

        }

        for (Method method: afterClass) {
            Result result = invoke(method, null);
            if (result.getType().equals(Type.FAIL)) {
                resultList = ignoreAll("because of AfterClass method");
                resultList.add(new MethodWithResult(method, result));
                return resultList;
            }
        }

        return resultList;

    }

    private @NotNull Result invoke(@NotNull Method test, @Nullable Object object) {

        Test testAnnotation = test.getAnnotation(Test.class);

        if (!testAnnotation.ignore().equals(Test.shouldNotIgnore)) {
            return Result.getIgnored(testAnnotation.ignore());
        }

        if (object != null) {

            long startTime = System.currentTimeMillis();
            long endTime = 0;

            try {

                for (Method beforeTest : before) {
                    beforeTest.invoke(object);
                }

                test.invoke(object);

                for (Method afterTest: after) {
                    afterTest.invoke(object);
                }

                endTime = System.currentTimeMillis();

            } catch (InvocationTargetException e) {

                if (!e.getTargetException().getClass().equals(testAnnotation.expected())) {
                    return Result.getFail(System.currentTimeMillis() - startTime);
                } else {
                    return Result.getCorrect(System.currentTimeMillis() - startTime);
                }

            } catch (IllegalAccessException e) {

                throw new RuntimeException(e);

            }

            if (!testAnnotation.expected().equals(Test.DefaultException.class)) {
                return Result.getFail(endTime - startTime);
            }

            return Result.getCorrect(endTime - startTime);

        } else {

            long startTime = System.currentTimeMillis();
            long endTime = 0;

            try {
                test.invoke(null);
                endTime = System.currentTimeMillis();
            } catch (InvocationTargetException e) {
                return Result.getFail(System.currentTimeMillis() - startTime);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            return Result.getCorrect(endTime - startTime);

        }

    }

    private @NotNull List<MethodWithResult> ignoreAll(@NotNull String cause) {

        List<MethodWithResult> resultList = new ArrayList<>();
        for (Method test: tests) {
            resultList.add(new MethodWithResult(test, Result.getIgnored(cause)));
        }

        return resultList;

    }

}

