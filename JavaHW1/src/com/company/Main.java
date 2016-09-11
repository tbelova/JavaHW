package com.company;

public class Main {

    public static void main(String[] args) {
        HashTable ht = new HashTable();

        assert(ht.put("MAMA", "mama") == null);
        assert(ht.size() == 1);
        assert(ht.get("MAMA").equals("mama"));
        assert(ht.put("PAPA", "papa") == null);
        assert(ht.size() == 2);
        assert(ht.get("PAPA").equals("papa"));
        assert(ht.put("MAMA", "papa").equals("mama"));
        assert(ht.size() == 2);
        assert(ht.get("MAMA").equals("papa"));
        ht.clear();
        assert(ht.get("MAMA") == null);
        assert(ht.size() == 0);

        System.out.println("OK");
    }

}
