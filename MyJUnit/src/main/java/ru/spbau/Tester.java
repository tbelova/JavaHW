package ru.spbau;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Tester {

    public static @NotNull List<MethodWithResult> test(@NotNull Class forTest) throws ClassNotFoundException {

        List<MethodWithResult> resultList = new ArrayList<>();

        List<Method> shouldBeTested = new ArrayList<>();
        List<Method> beforeTests = new ArrayList<>();
        List<Method> afterTests = new ArrayList<>();

        for (Method method: forTest.getMethods()) {

            if (method.getAnnotation(Test.class) != null) {
                shouldBeTested.add(method);
            }

            if (method.getAnnotation(Before.class) != null) {
                beforeTests.add(method);
            }

            if (method.getAnnotation(After.class) != null) {
                afterTests.add(method);
            }

        }

        for (Method method: forTest.getMethods()) {

            if (method.getAnnotation(BeforeClass.class) != null) {
                if (!invoke(method, null)) {
                    resultList = failAll(shouldBeTested);
                    resultList.add(new MethodWithResult(method, Result.getFail()));
                }
            }

        }

        for (Method method: shouldBeTested) {

            Object object;
            try {
                object = forTest.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            for (Method before: beforeTests) {
                if (!invoke(before, object)) {
                    return failAll(shouldBeTested);
                }
            }

            resultList.add(new MethodWithResult(method, test(method, object)));

            for (Method after: afterTests) {
                if (!invoke(after, object)) {
                    return failAll(shouldBeTested);
                }
            }
        }

        return resultList;

    }

    private static boolean invoke(@NotNull Method method, @Nullable Object object) {

        try {
            method.invoke(object);
        } catch (InvocationTargetException | IllegalAccessException e) {
            return false;
        }

        return true;

    }

    private static @NotNull List<MethodWithResult> failAll(List<Method> tests) {

        List<MethodWithResult> resultList = new ArrayList<>();
        for (Method test: tests) {
            resultList.add(new MethodWithResult(test, Result.getIgnored(".")));
        }

        return resultList;

    }

    private static @NotNull Result test(@NotNull Method method, @NotNull Object object) {

        Test test = method.getAnnotation(Test.class);

        if (test == null) {
            return Result.getNoAnnotation();
        }

        if (!test.ignore().equals(Test.shouldNotIgnore)) {
            return Result.getIgnored(test.ignore());
        }

        try {
            method.invoke(object);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            if (e.getTargetException().getClass().equals(test.expected())) {
                return Result.getCorrect();
            }
        }

        if (!test.expected().equals(Test.DefaultException.class)) {
            return Result.getFail();
        }

        return Result.getCorrect();

    }

}

