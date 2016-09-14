package com.company;

import org.junit.Test;

import static org.junit.Assert.*;

public class HashTableTest {
    @Test
    public void put() throws Exception {
        HashTable hashTable = new HashTable();
        assertEquals(null, hashTable.put("key1", "value1"));
        assertEquals("value1", hashTable.put("key1", "value2"));
    }

    @Test
    public void get() throws Exception {
        HashTable hashTable = new HashTable();
        assertEquals(null, hashTable.get("key1"));
        assertEquals(null, hashTable.put("key1", "value1"));
        assertEquals("value1", hashTable.get("key1"));
        assertEquals("value1", hashTable.put("key1", "value2"));
        assertEquals("value2", hashTable.get("key1"));
    }

    @Test
    public void remove() throws Exception {
        HashTable hashTable = new HashTable();
        assertEquals(null, hashTable.put("key1", "value1"));
        assertEquals("value1", hashTable.remove("key1"));
        assertEquals(null, hashTable.remove("key1"));
    }

    @Test
    public void clear() throws Exception {
        HashTable hashTable = new HashTable();
        assertEquals(null, hashTable.put("key1", "value1"));
        hashTable.clear();
        assertEquals(null, hashTable.get("key1"));
    }

    @Test
    public void size() throws Exception {
        HashTable hashTable = new HashTable();
        assertEquals(0, hashTable.size());
        assertEquals(null, hashTable.put("key1", "value1"));
        assertEquals(1, hashTable.size());
        assertEquals(null, hashTable.put("key2", "value2"));
        assertEquals(2, hashTable.size());
        assertEquals("value1", hashTable.put("key1", "value2"));
        assertEquals(2, hashTable.size());
        assertEquals("value2", hashTable.remove("key2"));
        assertEquals(1, hashTable.size());
        hashTable.clear();
        assertEquals(0, hashTable.size());
    }

    @Test
    public void contains() throws Exception {
        HashTable hashTable = new HashTable();
        assertEquals(false, hashTable.contains("key1"));
        assertEquals(null, hashTable.put("key1", "value1"));
        assertEquals(true, hashTable.contains("key1"));
    }

}