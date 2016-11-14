package ru.spbau;

import org.junit.Test;

import static org.junit.Assert.*;

public class Function1Test {
    private Function1<Integer, Integer> x2 = x -> x * 2;
    private Function1<Integer, Integer> x3 = x -> x * 3;
    private Function1<Integer, Integer> x6 = x -> x * 6;
    private Function1<Integer, Integer> plus2 = x -> x + 2;

    @Test
    public void applyTest() throws Exception {
        for (int i = 0; i < 100; i++) {
            assertEquals(i * 2, (int)x2.apply(i));
            assertEquals(i * 3, (int)x3.apply(i));
            assertEquals(i * 6, (int)x6.apply(i));
        }
    }

    @Test
    public void composeSimpleTest1() throws Exception {
        for (int i = 0; i < 100; i++) {
            assertEquals(x6.apply(i), x2.compose(x3).apply(i));
        }
    }

    @Test
    public void composeSimpleTest2() throws Exception {
        for (int i = 0; i < 100; i++) {
            assertEquals(i * 2 + 2, (int)x2.compose(plus2).apply(i));
            assertEquals(i * 3 + 2, (int)x3.compose(plus2).apply(i));
            assertEquals(i * 6 + 2, (int)x6.compose(plus2).apply(i));

            assertEquals((i + 2) * 2, (int)plus2.compose(x2).apply(i));
            assertEquals((i + 2) * 3, (int)plus2.compose(x3).apply(i));
            assertEquals((i + 2) * 6, (int)plus2.compose(x6).apply(i));
        }
    }

    @Test
    public void composeTest() throws Exception {
        for (int i = 0; i < 100; i++) {
            assertEquals((i + 2) * 2 * 3 + 2, (int)plus2.compose(x2).compose(x3).compose(plus2).apply(i));
            assertEquals(((i * 6 + 2) * 2 * 2 + 2) * 6,
                    (int)x6.compose(plus2).compose(x2).compose(x2).compose(plus2).compose(x6).apply(i));
        }
    }

}