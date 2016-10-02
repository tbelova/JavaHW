package ru.spbau;

/** Tree -- generic-множество, хранящее элементы в дереве поиска. */
public class Tree<T extends Comparable<T> > {

    private Node<T> root = null;
    private int size = 0;

    /** Метод add добавляет новый элемент в множество. */
    public void add(T element) {
        if (root == null) {
            root = new Node<T>(element);
            size = 1;
            return;
        }
        Node<T> node = find(root, element);
        if (element.compareTo(node.getValue()) == 0) {
            return;
        }
        if (element.compareTo(node.getValue()) < 0) {
            node.setLeft(new Node(element));
        } else {
            node.setRight(new Node(element));
        }
        size++;
    }

    /** Метод contains возвращает true, если элемент есть в множестве, и false, иначе. */
    public boolean contains(T element) {
        if (root == null) {
            return false;
        }
        Node<T> node = find(root, element);
        return element.compareTo(node.getValue()) == 0;
    }

    /** Метод size возвращает количество элементов в множестве. */
    public int size() {
        return size;
    }

    private Node find(Node<T> node, T element) {
        if (element.compareTo(node.getValue()) == 0) {
            return node;
        }
        if (element.compareTo(node.getValue()) < 0) {
            return (node.left == null) ? node : find(node.left, element);
        } else {
            return (node.right == null) ? node : find(node.right, element);
        }
    }

    private static class Node<T extends Comparable<T> > {
        private Node left = null;
        private Node right = null;
        private final T value;

        private Node(T element) {
            value = element;
        }

        public T getValue() {
            return value;
        }

        public void setLeft(Node node) {
            left = node;
        }

        public void setRight(Node node) {
            right = node;
        }
    }

}
