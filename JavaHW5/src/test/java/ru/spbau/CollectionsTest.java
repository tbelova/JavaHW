package ru.spbau;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;

import static org.junit.Assert.*;


public class CollectionsTest {

    private ArrayList<Integer> a = new ArrayList<>();

    @Before
    public void initialize() throws Exception {
        for (int i = 0; i < 100; i++) {
            a.add(i);
        }
    }

    @Test
    public void mapTest() throws Exception {
        Iterable b = Collections.map(x -> x * 2, a);

        Iterator<Integer> ita = a.iterator();
        Iterator<Integer> itb = b.iterator();
        while (ita.hasNext() && itb.hasNext()) {
            assertEquals(2 * ita.next(), (int)itb.next());
        }
        assertFalse(ita.hasNext());
        assertFalse(itb.hasNext());
    }

    @Test
    public void filterTest() throws Exception {
        Iterable b = Collections.filter(x -> x % 2 == 0, a);

        Iterator<Integer> itb = b.iterator();
        for (int i = 0; i < 50; i++) {
            assertTrue(itb.hasNext());
            assertEquals(i * 2, (int)itb.next());
        }
        assertFalse(itb.hasNext());
    }

    @Test
    public void takeWhileTest() throws Exception {
        Iterable b = Collections.takeWhile(x -> x < 30, a);
        Iterator<Integer> itb = b.iterator();
        for (int i = 0; i < 30; i++) {
            assertTrue(itb.hasNext());
            assertEquals(i, (int)itb.next());
        }
        assertFalse(itb.hasNext());
    }

    @Test
    public void takeUnlessTest() throws Exception {
        Iterable b = Collections.takeUnless(x -> x >= 30, a);
        Iterator<Integer> itb = b.iterator();
        for (int i = 0; i < 30; i++) {
            assertTrue(itb.hasNext());
            assertEquals(i, (int)itb.next());
        }
        assertFalse(itb.hasNext());
    }


    @Test
    public void foldlTest() throws Exception {
        Function2<Integer, Integer, Integer> sum = (x, y) -> x + y;
        Function2<Integer, Integer, Integer> fst = (x, y) -> x;
        Function2<Integer, Integer, Integer> lst = (x, y) -> y;
        assertEquals(4950, (int)Collections.foldl(sum, 0, a));
        assertEquals(0, (int)Collections.foldl(fst, 0, a));
        assertEquals(99, (int)Collections.foldl(lst, 0, a));
    }

    @Test
    public void foldrTest   () throws Exception {
        Function2<Integer, Integer, Integer> sum = (x, y) -> x + y;
        Function2<Integer, Integer, Integer> fst = (x, y) -> x;
        Function2<Integer, Integer, Integer> lst = (x, y) -> y;
        assertEquals(4950, (int)Collections.foldr(sum, 0, a));
        assertEquals(0, (int)Collections.foldr(fst, 0, a));
        assertEquals(100, (int)Collections.foldr(lst, 100, a));
    }

}