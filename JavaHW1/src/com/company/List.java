package com.company;

public class List {

    List() {}

    private class Node {

        private Node next = null;
        private String key, value;

        Node() {}
        Node(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public Node getNext() {
            return next;
        }

        public void setNext(Node next) {
            this.next = next;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

    }

    private Node head = new Node();

    private Node findPrev(String key) {
        Node cur = head;
        while (cur.getNext() != null) {
            if (cur.getNext().getKey().equals(key)) {
                return cur;
            }
            cur = cur.getNext();
        }
        return null;
    }

    public String insert(String key, String value) {
        Node prev = findPrev(key);
        if (prev == null) {
            Node node = new Node(key, value);
            node.setNext(head.getNext());
            head.setNext(node);
            return null;
        } else {
            String prevValue = prev.getNext().getValue();
            prev.getNext().setValue(value);
            return prevValue;
        }
    }

    public String erase(String key) {
        Node prev = findPrev(key);
        if (prev == null) {
            return null;
        }

        String str = prev.getNext().getValue();
        prev.setNext(prev.getNext().getNext());
        return str;
    }

    public boolean empty() {
        return head.getNext() == null;
    }

    public String get(String key) {
        Node prev = findPrev(key);
        if (prev == null) {
            return null;
        }

        return prev.getNext().getValue();
    }

    public void clear() {
        head = new Node();
    }

}
