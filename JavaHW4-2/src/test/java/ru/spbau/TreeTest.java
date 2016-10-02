package ru.spbau;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TreeTest {
    private Tree<Integer> treeInt;
    private Tree<String> treeString;

    @Before
    public void constructorTest1() throws Exception {
        treeInt = new Tree<Integer>();
    }

    @Before
    public void constructorTest2() throws Exception {
        treeString = new Tree<String>();
    }

    @Test
    public void addTest1() throws Exception {
        treeInt.add(1);
        treeInt.add(2);
        treeInt.add(1);
        treeInt.add(3);
    }

    @Test
    public void addTest2() throws Exception {
        treeString.add("aaaa");
        treeString.add("abacd");
        treeString.add("fdfjklj");
        treeString.add("dkfjm");
    }

    @Test
    public void containsTest1() throws Exception {
        treeInt.add(1);
        treeInt.add(2);
        treeInt.add(1);
        treeInt.add(3);
        assertTrue(treeInt.contains(1));
        assertTrue(treeInt.contains(2));
        assertTrue(treeInt.contains(3));
        assertFalse(treeInt.contains(4));
    }

    @Test
    public void containsTest2() throws Exception {
        treeString.add("aaaa");
        treeString.add("abacd");
        treeString.add("fdfjklj");
        treeString.add("dkfjm");
        assertTrue(treeString.contains("aaaa"));
        assertTrue(treeString.contains("abacd"));
        assertTrue(treeString.contains("fdfjklj"));
        assertTrue(treeString.contains("dkfjm"));
        assertFalse(treeString.contains("aaa"));
        assertFalse(treeString.contains("sfkfj"));
    }

    @Test
    public void sizeTest1() throws Exception {
        treeInt.add(1);
        treeInt.add(2);
        treeInt.add(1);
        treeInt.add(3);
        assertEquals(3, treeInt.size());
        treeInt.add(2);
        assertEquals(3, treeInt.size());
        treeInt.add(100);
        assertEquals(4, treeInt.size());
    }

    @Test
    public void sizeTest2() throws Exception {
        treeString.add("aaaa");
        treeString.add("abacd");
        treeString.add("fdfjklj");
        treeString.add("dkfjm");
        assertEquals(4, treeString.size());
        treeString.add("mama");
        assertEquals(5, treeString.size());
        treeString.add("aaaa");
        assertEquals(5, treeString.size());
    }

}