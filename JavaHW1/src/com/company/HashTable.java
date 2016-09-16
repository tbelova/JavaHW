package com.company;

/*

HashTable -- класс, представляющий из себя хеш-таблицу с ключами и значениями типа String

 */

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


    /*
     метод size() возвращает количество ключей в хеш-таблице
     */
    public int size() {
        return sz;
    }


    /*
    метод contains() возвращает true, если данный ключ содержится в хеш-таблице, и false иначе
     */
    public boolean contains(String key) {
        return !(arrayOfLists[Hash(key)].empty());
    }


    /*
     метод get() возвращает значение по ключу, или null, если такого значения нет
     */
    public String get(String key) {
        return arrayOfLists[Hash(key)].get(key);
    }


    /*
    метод put(key, value) кладет в хеш-таблицу значение value по ключу key и возвращает то,
    что было по этому ключу раньше, либо null, ели ничего не было
     */
    public String put(String key, String value) {
        String prevValue = arrayOfLists[Hash(key)].insert(key, value);
        if (prevValue == null) {
            sz++;
        }
        return prevValue;
    }


    /*
    метод remove(key) удаляет значение по заданному ключу из хеш-таблицы и возвращает удалённое значение,
    либо null, если такого значения не было
     */
    public String remove(String key) {
        String prevValue = arrayOfLists[Hash(key)].erase(key);
        if (prevValue != null) {
            sz--;
        }
        return prevValue;
    }


    /*
    метод clear() очищает хеш-таблицу
     */
    public void clear() {
        sz = 0;
        for (List ml: arrayOfLists) {
            ml.clear();
        }
    }

}
