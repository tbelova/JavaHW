package ru.spbau;

public class Trie {
    private class Node {
        private Node[] next = new Node[256];
        private Node parent = null;
        private int isTerminate = 0;
        private int amoutOfTerminate = 0;

        public void addTerminate(int isTerminate) {
            this.isTerminate += isTerminate;
        }

        public int getTerminate() {
            return isTerminate;
        }

        public int getAmoutOfTerminate() {
            return amoutOfTerminate;
        }

        private void recalc() {
            amoutOfTerminate = isTerminate;
            for (int i = 0; i < next.length; i++) {
                if (next[i] == null) continue;
                amoutOfTerminate += next[i].amoutOfTerminate;
            }
        }
    }

    Node root = new Node();

    public boolean add(String s) {
        Node last = root;
        for (int i = 0; i < s.length(); i++) {
            if (last.next[s.charAt(i)] != null) {
                last = last.next[s.charAt(i)];
            } else {
                last = new Node();
            }
        }
        last.addTerminate(1);
        recalc(last);

        return last.getTerminate() > 1;
    }

    public boolean contains(String s) {
        Node node = find(s);
        if (node != null && node.getTerminate() != 0) return true;
        return false;
    }

    public boolean remove(String s) {
        Node node = find(s);
        if (node == null || node.getTerminate() == 0) return false;
        node.addTerminate(-1);
        recalc(node);
        return true;
    }

    public int size() {
        return root.getTerminate();
    }

    public int howManyStartsWithPrefix(String prefix) {
        Node node = find(prefix);
        if (node == null) return 0;
        return node.getAmoutOfTerminate();
    }

    private void recalc(Node node) {
        while(node != null) {
            node.recalc();
            node = node.parent;
        }
    }

    private Node find(String s) {
        Node node = root;
        for (int i = 0; i < s.length(); i++) {
            if (node.next[s.charAt(i)] != null) {
                node = node.next[s.charAt(i)];
            } else {
                return null;
            }
        }
        return node;
    }

}
