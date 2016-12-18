package ru.spbau;

import org.junit.Test;

import java.util.ConcurrentModificationException;
import java.util.Iterator;

import static org.junit.Assert.*;


public class BinaryTreeTest {
    @Test
    public void addTest() throws Exception {
        MyTreeSet<Integer> set = new BinaryTree<>();
        assertTrue(set.add(5));
        assertTrue(set.add(6));
        assertTrue(set.add(1));
    }

    @Test
    public void removeTest() throws Exception {
        MyTreeSet<Integer> set = new BinaryTree<>();
        assertTrue(set.add(5));
        assertTrue(set.add(6));
        assertTrue(set.add(1));
        assertTrue(set.remove(5));
        assertFalse(set.remove(5));
        assertFalse(set.remove(7));
        assertTrue(set.remove(6));
    }

    @Test
    public void sizeTest() throws Exception {
        MyTreeSet<Integer> set = new BinaryTree<>();
        set.add(5);
        assertEquals(1, set.size());
        set.add(6);
        assertEquals(2, set.size());
        set.add(1);
        assertEquals(3, set.size());
    }

    @Test
    public void iteratorTest() throws Exception {
        MyTreeSet<Integer> set = new BinaryTree<>();
        for (int i = 0; i < 100; ++i) {
            assertTrue(set.add(i));
        }
        int i = 0;
        for (Iterator<Integer> iterator = set.iterator(); iterator.hasNext(); ) {
            assertEquals(i, (int)iterator.next());
            i++;
        }
        assertEquals(100, i);
    }

    @Test(expected = ConcurrentModificationException.class)
    public void iteratorTest2() throws Exception {
        MyTreeSet<Integer> set = new BinaryTree<>();
        for (int i = 0; i < 100; ++i) {
            assertTrue(set.add(i));
        }
        int i = 0;
        for (Iterator<Integer> iterator = set.iterator(); iterator.hasNext(); ) {
            assertEquals(i, (int)iterator.next());
            i++;
            if (i == 40) {
                assertTrue(set.add(15));
            }
        }
        assertEquals(100, i);
    }

    @Test
    public void descendingIteratorTest() throws Exception {
        MyTreeSet<Integer> set = new BinaryTree<>();
        for (int i = 0; i < 100; ++i) {
            assertTrue(set.add(i));
        }
        int i = 100;
        for (Iterator<Integer> iterator = set.descendingIterator(); iterator.hasNext(); ) {
            i--;
            assertEquals(i, (int)iterator.next());
        }
        assertEquals(0, i);
    }

    @Test
    public void descendingSetTest() throws Exception {
        MyTreeSet<Integer> set = new BinaryTree<>();
        for (int i = 0; i < 100; ++i) {
            assertTrue(set.add(i));
        }
        MyTreeSet<Integer> descendingTree = set.descendingSet();
        int i = 100;
        for (Iterator<Integer> iterator = descendingTree.iterator(); iterator.hasNext(); ) {
            i--;
            assertEquals(i, (int)iterator.next());
        }
        assertEquals(0, i);
    }

    @Test
    public void firstTest() throws Exception {
        MyTreeSet<Integer> set = new BinaryTree<>();
        assertTrue(set.add(5));
        assertTrue(set.add(6));
        assertTrue(set.add(1));
        assertEquals(1, (int)set.first());
        MyTreeSet<Integer> setRev = new BinaryTree<>((x, y) -> y.compareTo(x));
        assertTrue(setRev.add(5));
        assertTrue(setRev.add(6));
        assertTrue(setRev.add(1));
        assertEquals(6, (int)setRev.first());
    }

    @Test
    public void lastTest() throws Exception {
        MyTreeSet<Integer> set = new BinaryTree<>();
        assertTrue(set.add(5));
        assertTrue(set.add(6));
        assertTrue(set.add(1));
        assertEquals(6, (int)set.last());
        MyTreeSet<Integer> setRev = new BinaryTree<>((x, y) -> y.compareTo(x));
        assertTrue(setRev.add(5));
        assertTrue(setRev.add(6));
        assertTrue(setRev.add(1));
        assertEquals(1, (int)setRev.last());
    }

    @Test
    public void lowerTest() throws Exception {
        MyTreeSet<Integer> set = new BinaryTree<>();
        for (int i = 0; i < 50; ++i) {
            assertTrue(set.add(i * 2));
        }
        assertEquals(40, (int)set.lower(42));
        assertEquals(40, (int)set.lower(41));
        assertEquals(98, (int)set.lower(200));
        assertNull(set.lower(0));
    }

    @Test
    public void floorTest() throws Exception {
        MyTreeSet<Integer> set = new BinaryTree<>();
        for (int i = 0; i < 50; ++i) {
            assertTrue(set.add(i * 2));
        }
        assertEquals(42, (int)set.floor(42));
        assertEquals(40, (int)set.floor(41));
        assertEquals(98, (int)set.floor(200));
        assertEquals(0, (int)set.floor(0));
        assertNull(set.floor(-1));
    }

    @Test
    public void ceilingTest() throws Exception {
        MyTreeSet<Integer> set = new BinaryTree<>();
        for (int i = 0; i < 50; ++i) {
            assertTrue(set.add(i * 2));
        }
        assertEquals(42, (int)set.ceiling(42));
        assertEquals(42, (int)set.ceiling(41));
        assertNull(set.ceiling(200));
        assertEquals(0, (int)set.ceiling(0));
        assertEquals(0, (int)set.ceiling(-1));
        assertEquals(98, (int)set.ceiling(98));
    }

    @Test
    public void higherTest() throws Exception {
        MyTreeSet<Integer> set = new BinaryTree<>();
        for (int i = 0; i < 50; ++i) {
            assertTrue(set.add(i * 2));
        }
        assertEquals(44, (int)set.higher(42));
        assertEquals(42, (int)set.higher(41));
        assertNull(set.higher(200));
        assertEquals(2, (int)set.higher(0));
        assertEquals(0, (int)set.higher(-1));
        assertNull(set.higher(98));
    }
}
