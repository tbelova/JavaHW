package ru.spbau;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

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
    public void simpleCreateLazyOneThreadTest() throws Exception {
        simpleLazyIntegerTest(LazyFactory.createLazyOneThread(integerSupplier));
    }

    @Test
    public void simpleCreateLazySynchronizedTest() throws Exception {
        simpleLazyIntegerTest(LazyFactory.createLazySynchronized(integerSupplier));
    }

    @Test
    public void simpleCreateLazyLockFreeTest() throws Exception {
        simpleLazyIntegerTest(LazyFactory.createLazyLockFree(integerSupplier));
    }

    private final List<Integer> resultList = new ArrayList<>();

    private class LazyIntegerRunnable implements Runnable {

        private Lazy<Integer> lazy;

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
    public void createLazySynchronizedTest() throws Exception {
        lazyIntegerTest(LazyFactory.createLazySynchronized(integerSupplier));
        assertEquals((Integer)2, integerSupplier.get());
    }

    @Test
    public void createLazyLockFreeTest() throws Exception {
        lazyIntegerTest(LazyFactory.createLazyLockFree(integerSupplier));
    }

    private Supplier<Integer> returningNullSupplier = new Supplier<Integer>() {
        int n = 0;
        @Override
        public Integer get() {
            if (n == 0) {
                n++;
                return null;
            }
            return n++;
        }
    };

    @Test
    public void workWithNullSimpleTest() throws Exception {
        simpleLazyIntegerTest(LazyFactory.createLazyOneThread(returningNullSupplier));
        simpleLazyIntegerTest(LazyFactory.createLazySynchronized(returningNullSupplier));
        simpleLazyIntegerTest(LazyFactory.createLazyLockFree(returningNullSupplier));
    }

    @Test
    public void workWithNullTest() throws Exception {
        lazyIntegerTest(LazyFactory.createLazySynchronized(returningNullSupplier));
        lazyIntegerTest(LazyFactory.createLazyLockFree(returningNullSupplier));
    }

}