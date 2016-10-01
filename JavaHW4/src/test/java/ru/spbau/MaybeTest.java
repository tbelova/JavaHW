package ru.spbau;

import org.junit.Before;
import org.junit.Test;

import java.util.function.Function;

import static org.junit.Assert.*;

public class MaybeTest {
    Maybe<Integer> maybe10;
    Maybe<Integer> nothing;

    @Before
    public void justTest() throws Exception {
        maybe10 = Maybe.just(10);
    }

    @Before
    public void nothingTest() throws Exception {
        nothing = Maybe.nothing();
    }

    @Test
    public void getTest() throws Exception {
        assertEquals((Integer)10, maybe10.get());
    }

    @Test
    public void isPresentTest1() throws Exception {
        assertTrue(maybe10.isPresent());
    }

    @Test
    public void isPresentTest2() throws Exception {
        assertFalse(nothing.isPresent());
    }

    @Test
    public void mapTest1() throws Exception {
        Function<Integer, Integer> plus1 = x -> x + 1;
        assertTrue(maybe10.map(plus1).isPresent());
        assertEquals((Integer)11, maybe10.map(plus1).get());
    }

    @Test
    public void mapTest2() throws Exception {
        Function<Integer, Integer> plus1 = x -> x + 1;
        assertFalse(nothing.map(plus1).isPresent());
    }

}