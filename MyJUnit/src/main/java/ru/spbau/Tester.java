package ru.spbau;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Tester {

    public static @NotNull List<MethodWithResult> test(@NotNull Class forTest) throws ClassNotFoundException {

        List<MethodWithResult> resultList = new ArrayList<>();

        for (Method method: forTest.getMethods()) {
            resultList.add(new MethodWithResult(method, test(method)));
        }

        return resultList;

    }

    private static @NotNull Result test(@NotNull Method method) {

        Test test = method.getAnnotation(Test.class);

        if (test == null) {
            return Result.getNoAnnotation();
        }

        if (!test.ignore().equals(Test.shouldNotIgnore)) {
            return Result.getIgnored(test.ignore());
        }

        try {
            method.invoke(method.getDeclaringClass().newInstance());
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
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

