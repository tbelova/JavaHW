package ru.spbau;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.Assert.*;

public class LazyFactoryTest {

    private Supplier<Integer> integerSupplier = new Supplier<Integer>() {
        Integer num = 0;
        @Override
        public Integer get() {
            num++;
            return num;
        }
    };

    private void simpleLazyIntegerTest(@NotNull Lazy<Integer> lazy) throws Exception {
        Integer n = lazy.get();
        for (int i = 0; i < 100; ++i) {
            assertSame(n, lazy.get());
        }
    }

    @Test
    public void simpleCreateLazy1Test() throws Exception {
        simpleLazyIntegerTest(LazyFactory.createLazy1(integerSupplier));
    }

    @Test
    public void simpleCreateLazy2Test() throws Exception {
        simpleLazyIntegerTest(LazyFactory.createLazy2(integerSupplier));
    }

    @Test
    public void simpleCreateLazy3Test() throws Exception {
        simpleLazyIntegerTest(LazyFactory.createLazy3(integerSupplier));
    }

    private final List<Integer> resultList = new ArrayList<>();

    private class LazyIntegerRunnable implements Runnable {

        Lazy<Integer> lazy;

        public LazyIntegerRunnable(@NotNull Lazy<Integer> lazy) {
            this.lazy = lazy;
        }

        @Override
        public void run() {
           for (int i = 0; i < 50; ++i) {
               Integer n = lazy.get();
               synchronized (resultList) {
                   resultList.add(n);
               }
           }
        }
    }

    private void lazyIntegerTest(@NotNull Lazy<Integer> lazy) throws Exception {
        int numberOfThreads = 10;
        List<Thread> threadList = new ArrayList<>();
        for (int i = 0; i < numberOfThreads; ++i) {
            Thread thread = new Thread(new LazyIntegerRunnable(lazy));
            threadList.add(thread);
        }
        for (Thread thread: threadList) {
            thread.start();
        }
        for (Thread thread: threadList) {
            thread.join();
        }
        for (Integer element: resultList) {
            assertSame(resultList.get(0), element);
        }
    }

    @Test
    public void createLazy2Test() throws Exception {
        lazyIntegerTest(LazyFactory.createLazy2(integerSupplier));
    }

    @Test
    public void createLazy3Test() throws Exception {
        lazyIntegerTest(LazyFactory.createLazy3(integerSupplier));
    }

}