package com.company;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class HashTableTest {
    private HashTable hashTable;

    @Before
    public void constructorTest() throws Exception {
        hashTable = new HashTable();
    }

    @Test
    public void putTest() throws Exception {
        assertNull(hashTable.put("key1", "value1"));
        assertEquals("value1", hashTable.put("key1", "value2"));
    }

    @Test
    public void getTest() throws Exception {
        assertNull(hashTable.get("key1"));
        assertNull(hashTable.put("key1", "value1"));
        assertEquals("value1", hashTable.get("key1"));
        assertEquals("value1", hashTable.put("key1", "value2"));
        assertEquals("value2", hashTable.get("key1"));
    }

    @Test
    public void removeTest() throws Exception {
        assertNull(hashTable.put("key1", "value1"));
        assertEquals("value1", hashTable.remove("key1"));
        assertNull(hashTable.remove("key1"));
    }

    @Test
    public void clearTest() throws Exception {
        assertNull(hashTable.put("key1", "value1"));
        hashTable.clear();
        assertNull(hashTable.get("key1"));
    }

    @Test
    public void sizeTest() throws Exception {
        assertEquals(0, hashTable.size());
        assertNull(hashTable.put("key1", "value1"));
        assertEquals(1, hashTable.size());
        assertNull(hashTable.put("key2", "value2"));
        assertEquals(2, hashTable.size());
        assertEquals("value1", hashTable.put("key1", "value2"));
        assertEquals(2, hashTable.size());
        assertEquals("value2", hashTable.remove("key2"));
        assertEquals(1, hashTable.size());
        hashTable.clear();
        assertEquals(0, hashTable.size());
    }

    @Test
    public void containsTest() throws Exception {
        assertEquals(false, hashTable.contains("key1"));
        assertNull(hashTable.put("key1", "value1"));
        assertEquals(true, hashTable.contains("key1"));
    }

}