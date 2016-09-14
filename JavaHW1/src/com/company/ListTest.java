package com.company;

import static org.junit.Assert.*;

public class ListTest {
    @org.junit.Test
    public void insertTest() throws Exception {
        List list = new List();
        assertEquals(null, list.insert("key", "value1"));
        assertEquals("value1", list.insert("key", "value2"));
    }

    @org.junit.Test
    public void eraseTest() throws Exception {
        List list = new List();
        assertEquals(null, list.insert("key", "value1"));
        assertEquals("value1", list.erase("key"));
        assertEquals(null, list.erase("key"));
    }

    @org.junit.Test
    public void emptyTest() throws Exception {
        List list = new List();
        assertEquals(true, list.empty());
        assertEquals(null, list.insert("key", "value1"));
        assertEquals(false, list.empty());
        assertEquals("value1", list.erase("key"));
        assertEquals(true, list.empty());
    }

    @org.junit.Test
    public void getTest() throws Exception {
        List list = new List();
        assertEquals(null, list.get("key1"));
        assertEquals(null, list.insert("key1", "value1"));
        assertEquals("value1", list.get("key1"));
        assertEquals("value1", list.insert("key1", "value2"));
        assertEquals("value2", list.get("key1"));
        assertEquals(null, list.get("key2"));
        assertEquals(null, list.insert("key2", "value1"));
        assertEquals("value1", list.get("key2"));
        assertEquals("value1", list.insert("key2", "value2"));
        assertEquals("value2", list.get("key2"));

        assertEquals("value2", list.erase("key1"));
        assertEquals("value2", list.erase("key2"));

        assertEquals(null, list.erase("key1"));
        assertEquals(null, list.erase("key2"));
    }

    @org.junit.Test
    public void clearTest() throws Exception {
        List list = new List();
        assertEquals(null, list.insert("key1", "value1"));
        assertEquals(null, list.insert("key2", "value2"));
        assertEquals(null, list.insert("key3", "value3"));

        assertEquals("value1", list.get("key1"));
        assertEquals("value2", list.get("key2"));
        assertEquals("value3", list.get("key3"));

        list.clear();

        assertEquals(null, list.get("key1"));
        assertEquals(null, list.get("key2"));
        assertEquals(null, list.get("key3"));
    }

}