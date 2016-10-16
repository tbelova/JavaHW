package ru.spbau;

import org.junit.Test;

import static org.junit.Assert.*;


public class Function2Test {
    private Function2<Integer, Integer, Integer> sum = (x, y) -> x + y;
    private Function2<Integer, Integer, Integer> mult = (x, y) -> x * y;
    private Function2<Integer, Integer, Integer> div = (x, y) -> x / y;

    private Function1<Integer, Integer> x2 = x -> x * 2;
    private Function1<Integer, Integer> x3 = x -> x * 3;
    private Function1<Integer, Integer> x6 = x -> x * 6;
    private Function1<Integer, Integer> plus2 = x -> x + 2;

    @Test
    public void apply() throws Exception {
        for (int i = 0; i < 100; i++) {
            for (int j = 1; j < 100; j++) {
                assertEquals(i + j, (int)sum.apply(i, j));
                assertEquals(i * j, (int)mult.apply(i, j));
                assertEquals(i / j, (int)div.apply(i, j));
            }
        }
    }

    @Test
    public void composeTest() throws Exception {
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                assertEquals((i + j) * 2, (int)sum.compose(x2).apply(i, j));
                assertEquals((i + j) * 3, (int)sum.compose(x3).apply(i, j));
                assertEquals((i + j) * 6, (int)sum.compose(x6).apply(i, j));
                assertEquals((i + j) * 6, (int)sum.compose(x2).compose(x3).apply(i, j));

                assertEquals(i + j + 2, (int)sum.compose(plus2).apply(i, j));

                assertEquals(i * j * 2, (int)mult.compose(x2).apply(i, j));
                assertEquals(i * j * 3, (int)mult.compose(x3).apply(i, j));
                assertEquals(i * j * 6, (int)mult.compose(x6).apply(i, j));
                assertEquals(i * j * 6, (int)mult.compose(x2).compose(x3).apply(i, j));

                assertEquals(i * j + 2, (int)mult.compose(plus2).apply(i, j));
            }
        }
    }

    @Test
    public void bind1() throws Exception {
        Function1<Integer, Integer> f = sum.bind1(5);
        assertEquals(6, (int)f.apply(1));
        assertEquals(15, (int)f.apply(10));

        Function1<Integer, Integer> f2 = sum.compose(x2).compose(plus2).bind1(1);
        assertEquals((1 + 10) * 2 + 2, (int)f2.apply(10));
        assertEquals((1 + 4) * 2 + 2, (int)f2.apply(4));

        Function2<Integer, Integer, Integer> f3 = (x, y) -> x + 2 * y;
        Function1<Integer, Integer> f4 = f3.bind1(5);
        assertEquals(7, (int)f4.apply(1));
        assertEquals(9, (int)f4.apply(2));
        assertEquals(11, (int)f4.apply(3));
    }

    @Test
    public void bind2() throws Exception {
        Function1<Integer, Integer> f = sum.bind1(5);
        assertEquals(6, (int)f.apply(1));
        assertEquals(15, (int)f.apply(10));

        Function1<Integer, Integer> f2 = sum.compose(x2).compose(plus2).bind2(1);
        assertEquals((1 + 10) * 2 + 2, (int)f2.apply(10));
        assertEquals((1 + 4) * 2 + 2, (int)f2.apply(4));

        Function2<Integer, Integer, Integer> f3 = (x, y) -> x + 2 * y;
        Function1<Integer, Integer> f4 = f3.bind2(5);
        assertEquals(11, (int)f4.apply(1));
        assertEquals(12, (int)f4.apply(2));
        assertEquals(13, (int)f4.apply(3));
    }

    @Test
    public void curry() throws Exception {
        Function1<Integer, Function1<Integer, Integer>> f = sum.curry();
        Function1 f0  = f.apply(0);
        Function1 f1  = f.apply(1);
        Function1 f2  = f.apply(2);
        Function1 f3  = f.apply(3);

        assertEquals(4, f0.apply(4));
        assertEquals(5, f1.apply(4));
        assertEquals(6, f2.apply(4));
        assertEquals(7, f3.apply(4));
    }

}


