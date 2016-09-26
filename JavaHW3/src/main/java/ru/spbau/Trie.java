package ru.spbau;

import java.io.*;

/**
 * Trie -- это класс, представляющий из себя бор.
 */
public class Trie implements StreamSerializable {

    private Node root = new Node();

    /**
     * Метод add добавляет строку в бор. Если строки еще не было в боре, возвращает true, иначе false.
     * Работает за O(|s|).
     * */
    public boolean add(String s) {
        Node last = root;
        for (int i = 0; i < s.length(); i++) {
            if (last.next[s.charAt(i)] == null) {
                last.next[s.charAt(i)] = new Node();
                last.next[s.charAt(i)].parent = last;
            }
            last = last.next[s.charAt(i)];
        }
        last.addTerminated(1);
        recalc(last);

        return last.getAmountOfTerminatedInNode() == 1;
    }

    /**
     * Метод contains возвращает true, если строка есть в боре и false, иначе.
     * Работает за O(|s|).
     * */
    public boolean contains(String s) {
        Node node = find(s);
        if (node != null && node.getAmountOfTerminatedInNode() != 0) return true;
        return false;
    }

    /**
     * Метод remove удаляет строку из бора и возвращает true, если строка была в боре, и false, иначе.
     * Работает за O(|s|).
     */
    public boolean remove(String s) {
        Node node = find(s);
        if (node == null || node.getAmountOfTerminatedInNode() == 0) return false;
        node.addTerminated(-1);
        recalc(node);
        return true;
    }

    /**
     * Метод size возвращает количество строк к боре.
     * Работает за O(1).
     */
    public int size() {
        return root.getAmountOfTerminatedInSubtree();
    }

    /**
     * Метод howManyStartsWithPrefix возвращает, сколько строк в боре начинаются с заданной строки.
     * Работает за O(|prefix|).
     */
    public int howManyStartsWithPrefix(String prefix) {
        Node node = find(prefix);
        if (node == null) return 0;
        return node.getAmountOfTerminatedInSubtree();
    }

    /** Метод serialize выводит бор в стрим. */
    public void serialize(OutputStream out) throws IOException {
        (new ObjectOutputStream(out)).writeObject(root);
    }

    /** Метод deserialize заменяет старое дерево данными из стрима. */
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

    /** Node -- класс, представляющий из себя вершину в боре. */
    private static class Node implements Serializable {
        private Node[] next = new Node[256];
        private Node parent = null;
        private int amountOfTerminatedInNode = 0;
        private int amountOfTerminatedInSubtree = 0;

        public void addTerminated(int cntTerminated) {
            this.amountOfTerminatedInNode += cntTerminated;
        }

        public int getAmountOfTerminatedInNode() {
            return amountOfTerminatedInNode;
        }

        public int getAmountOfTerminatedInSubtree() {
            return amountOfTerminatedInSubtree;
        }

        private void recalc() {
            amountOfTerminatedInSubtree = amountOfTerminatedInNode;
            for (Node node: next) {
                if (node == null) continue;
                amountOfTerminatedInSubtree += node.amountOfTerminatedInSubtree;
            }
        }

        private void writeObject(ObjectOutputStream out) throws IOException {
            out.writeInt(amountOfTerminatedInNode);
            out.writeInt(amountOfTerminatedInSubtree);
            out.writeObject(next);
        }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            amountOfTerminatedInNode = in.readInt();
            amountOfTerminatedInSubtree = in.readInt();
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
