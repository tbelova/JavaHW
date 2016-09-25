package ru.spbau;

import java.io.*;

public class Trie implements StreamSerializable {

    private Node root = new Node();

    public boolean add(String s) {
        Node last = root;
        for (int i = 0; i < s.length(); i++) {
            if (last.next[s.charAt(i)] == null) {
                last.next[s.charAt(i)] = new Node();
                last.next[s.charAt(i)].parent = last;
            }
            last = last.next[s.charAt(i)];
        }
        last.addTerminate(1);
        recalc(last);

        return last.getTerminate() == 1;
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
        return root.getAmountOfTerminate();
    }

    public int howManyStartsWithPrefix(String prefix) {
        Node node = find(prefix);
        if (node == null) return 0;
        return node.getAmountOfTerminate();
    }

    public void serialize(OutputStream out) throws IOException {
        (new ObjectOutputStream(out)).writeObject(root);
    }

    public void deserialize(InputStream in) throws IOException, ClassNotFoundException {
        root = (Node)(new ObjectInputStream(in)).readObject();
        root.fixParents(null);
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

    private static class Node implements Serializable {
        private Node[] next = new Node[256];
        private Node parent = null;
        private int isTerminate = 0;
        private int amountOfTerminate = 0;

        public void addTerminate(int isTerminate) {
            this.isTerminate += isTerminate;
        }

        public int getTerminate() {
            return   isTerminate;
        }

        public int getAmountOfTerminate() {
            return amountOfTerminate;
        }

        private void recalc() {
            amountOfTerminate = isTerminate;
            for (Node node: next) {
                if (node == null) continue;
                amountOfTerminate += node.amountOfTerminate;
            }
        }

        private void writeObject(ObjectOutputStream out) throws IOException {
            out.writeInt(isTerminate);
            out.writeInt(amountOfTerminate);
            out.writeObject(next);
        }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            isTerminate = in.readInt();
            amountOfTerminate = in.readInt();
            next = (Node[])in.readObject();
        }

        private void fixParents(Node parent) {
            this.parent  = parent;
            for (Node node: next) {
                if (node == null) continue;
                node.fixParents(this);
            }
        }

    }

}
