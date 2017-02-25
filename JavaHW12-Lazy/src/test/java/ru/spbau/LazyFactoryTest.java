package ru.spbau;

import org.junit.Test;

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

    public void simpleIntegerTest(Lazy<Integer> lazy) throws Exception {
        Integer n = lazy.get();
        for (int i = 0; i < 100; ++i) {
            assertSame(n, lazy.get());
        }
    }

    @Test
    public void simpleCreateLazy1Test() throws Exception {
        simpleIntegerTest(LazyFactory.createLazy1(integerSupplier));
    }

    @Test
    public void simpleCreateLazy2Test() throws Exception {
        simpleIntegerTest(LazyFactory.createLazy2(integerSupplier));
    }

    @Test
    public void simpleCreateLazy3Test() throws Exception {
        simpleIntegerTest(LazyFactory.createLazy3(integerSupplier));
    }

}