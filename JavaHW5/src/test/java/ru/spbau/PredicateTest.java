package ru.spbau;

import org.junit.Test;

import static org.junit.Assert.*;


public class PredicateTest {
    private Predicate<Integer> even = x -> (x % 2 == 0);
    private Predicate<Integer> odd = x -> (x % 2 != 0);
    private Predicate<Integer> mult3 = x -> (x % 3 == 0);

    @Test
    public void apply() throws Exception {
        assertTrue(even.apply(0));
        assertFalse(even.apply(111));

        assertTrue(odd.apply(111));
        assertFalse(odd.apply(0));

        assertTrue(mult3.apply(111));
        assertFalse(mult3.apply(5));

        assertTrue(Predicate.ALWAYS_TRUE.apply(100));
        assertFalse(Predicate.ALWAYS_FALSE.apply(100));
    }

    @Test
    public void or() throws Exception {
        assertTrue(even.or(mult3).apply(4));
        assertFalse(even.or(mult3).apply(5));
        assertTrue(odd.or(mult3).apply(5));
        assertFalse(odd.or(mult3).apply(4));

        assertTrue(mult3.or(even).apply(4));
        assertFalse(mult3.or(even).apply(5));
        assertTrue(mult3.or(odd).apply(5));
        assertFalse(mult3.or(odd).apply(4));

        assertTrue(even.or(mult3).apply(6));
        assertTrue(odd.or(mult3).apply(6));

        assertTrue(mult3.or(even).apply(9));
        assertTrue(mult3.or(odd).apply(9));
    }

    @Test
    public void and() throws Exception {
        assertTrue(even.and(mult3).apply(6));
        assertFalse(even.and(mult3).apply(8));
        assertFalse(even.and(mult3).apply(3));
    }

    @Test
    public void not() throws Exception {
        assertTrue(Predicate.ALWAYS_FALSE.not().apply(100));
        assertFalse(Predicate.ALWAYS_TRUE.not().apply(100));

        assertTrue(even.not().apply(1));
    }

}