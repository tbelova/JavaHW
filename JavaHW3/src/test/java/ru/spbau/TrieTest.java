package ru.spbau;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.Assert.*;

public class TrieTest {
    private Trie trie;

    @Before
    public void init() throws Exception {
        trie = new Trie();
    }

    @Test
    public void addTest() throws Exception {
        assertTrue(trie.add("abcde"));
        assertTrue(trie.add("abcd"));
        assertTrue(trie.add("abcdef"));
        assertFalse(trie.add("abcde"));
        assertTrue(trie.add("abgde"));
    }

    @Test
    public void containsTest() throws Exception {
        addTest();
        assertTrue(trie.contains("abcde"));
        assertTrue(trie.contains("abcd"));
        assertTrue(trie.contains("abcdef"));
        assertTrue(trie.contains("abgde"));
        assertFalse(trie.contains("abcdee"));
        assertFalse(trie.contains("aacde"));
    }

    @Test
    public void removeTest() throws Exception {
        for (int i = 0; i < 10; ++i) {
            assertTrue(trie.add("abcde"));
            assertTrue(trie.add("abcd"));
            assertTrue(trie.add("abcdef"));
            assertFalse(trie.add("abcde"));
            assertTrue(trie.add("abgde"));
            assertTrue(trie.remove("abcde"));
            assertTrue(trie.remove("abcd"));
            assertTrue(trie.remove("abcdef"));
            assertTrue(trie.remove("abgde"));
            assertFalse(trie.remove("abcdee"));
            assertFalse(trie.remove("aacde"));
            assertTrue(trie.remove("abcde"));
        }
    }

    @Test
    public void sizeTest() throws Exception {
        assertEquals(0, trie.size());
        assertFalse(trie.remove("abcde"));
        assertEquals(0, trie.size());
        assertTrue(trie.add("abcde"));
        assertEquals(1, trie.size());
        assertTrue(trie.add("abcd"));
        assertEquals(2, trie.size());
        assertTrue(trie.add("abcdef"));
        assertEquals(3, trie.size());
        assertFalse(trie.add("abcde"));
        assertEquals(4, trie.size());
        assertTrue(trie.add("abgde"));
        assertEquals(5, trie.size());
    }

    @Test
    public void howManyStartsWithPrefixTest() throws Exception {
        assertTrue(trie.add("abcde"));
        assertTrue(trie.add("abcd"));
        assertTrue(trie.add("abcdef"));
        assertFalse(trie.add("abcde"));
        assertTrue(trie.add("abgde"));
        assertEquals(5, trie.howManyStartsWithPrefix(""));
        assertEquals(5, trie.howManyStartsWithPrefix("a"));
        assertEquals(5, trie.howManyStartsWithPrefix("ab"));
        assertEquals(4, trie.howManyStartsWithPrefix("abc"));
        assertEquals(4, trie.howManyStartsWithPrefix("abcd"));
        assertEquals(3, trie.howManyStartsWithPrefix("abcde"));
        assertEquals(1, trie.howManyStartsWithPrefix("abcdef"));
        assertEquals(0, trie.howManyStartsWithPrefix("b"));
    }

    @Test
    public void serializeTest() throws Exception {
        assertTrue(trie.add("abcde"));
        assertTrue(trie.add("abcd"));
        assertTrue(trie.add("abcdef"));
        assertFalse(trie.add("abcde"));
        assertTrue(trie.add("abgde"));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        trie.serialize(out);
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        Trie trie2 = new Trie();
        trie2.deserialize(in);

        assertEquals(trie.size(), trie2.size());
        assertEquals(trie.contains("ababababa"), trie2.contains("ababababa"));
        assertEquals(trie.contains("abcde"), trie2.contains("abcde"));
        assertEquals(trie.contains("abcdef"), trie2.contains("abcdef"));
        assertEquals(trie.contains("a"), trie2.contains("a"));
        assertEquals(trie.contains("abgde"), trie2.contains("abgde"));
    }

}