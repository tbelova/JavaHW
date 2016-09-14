package com.company;

public class HashTable {
    private static final int cntOfLists = 239;

    private int sz = 0;
    private List[] arrayOfLists = new List[cntOfLists];

    public HashTable() {
        for (int i = 0; i < cntOfLists; i++) {
            arrayOfLists[i] = new List();
        }
    }

    private int Hash(String str) {
        int hash = 0;
        int p = 31;
        for (int i = 0; i < str.length(); i++) {
            hash *= p;
            hash += str.charAt(i);
            hash %= cntOfLists;
        }
        return hash;
    }

    public int size() {
        return sz;
    }

    public boolean contains(String key) {
        return !(arrayOfLists[Hash(key)].empty());
    }

    public String get(String key) {
        return arrayOfLists[Hash(key)].get(key);
    }

    public String put(String key, String value) {
        String prevValue = arrayOfLists[Hash(key)].insert(key, value);
        if (prevValue == null) {
            sz++;
        }
        return prevValue;
    }

    public String remove(String key) {
        String prevValue = arrayOfLists[Hash(key)].erase(key);
        if (prevValue != null) {
            sz--;
        }
        return prevValue;
    }

    public void clear() {
        sz = 0;
        for (List ml: arrayOfLists) {
            ml.clear();
        }
    }

}
