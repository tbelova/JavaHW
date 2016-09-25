package com.company;

/**
 * List -- класс, представляющий из себя список пар (key, value),
 * где key и value типа String
 */
public class List {

    /**
     * Node --  класс, представляющий из себя элемент списка,
     * содержащий пару (key, value) и ссылку на следующий элемент
     */
    private static class Node {

        private Node next = null;
        private String key;
        private String value;

        Node() {}

        Node(String key, String value) {
            this.key = key;
            this.value = value;
        }


        /** метод getNext() возвращает следующий в списке элемент.*/
        public Node getNext() {
            return next;
        }


        /** метод setNext(next) делает элемент next следующим
        в списке для данного элемента.*/
        public void setNext(Node next) {
            this.next = next;
        }


        /** метод getKey() возвращает значение key в данном
        элементе списка.*/
        public String getKey() {
            return key;
        }

        /*
        метод getValue() возвращает значение value в данном
        элементе списка
         */
        public String getValue() {
            return value;
        }


        /*
        метод setValue(value) обновляет value для данного
        элемента списка.
         */
        public void setValue(String value) {
            this.value = value;
        }

    }

    /** head -- первый фиктивный элемент списка.*/
    private Node head = new Node();


    /**
     * метод findPrev(key) возвращает ссылку на элемент, предшествующий
     * в списке элементу с ключом key, или null, если такого элемента нет
     */
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


    /**
     * метод insert(key, value) кладет в список пару (key, value) и,
     * если пара с ключом key уже была, то возвращает значение,
     * которое было ключу key раньше, и удаляет эту пару,
     * а если такой пары раньше не было, то возвращает null.
     */
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


    /**
     * метод erase(key) удаляет пару с ключом key и возвращает значение
     * value из этой пары или null, если такой пары не было.
     */
    public String erase(String key) {
        Node prev = findPrev(key);
        if (prev == null) {
            return null;
        }

        String str = prev.getNext().getValue();
        prev.setNext(prev.getNext().getNext());
        return str;
    }


    /** метод empty() возвращает true, если список пуст и false иначе.*/
    public boolean empty() {
        return head.getNext() == null;
    }


    /**
     * метод get(key) возвращает значение value, соответствующее key,
     * или null, если пары (key, value) нет в списке.
     */
    public String get(String key) {
        Node prev = findPrev(key);
        if (prev == null) {
            return null;
        }

        return prev.getNext().getValue();
    }


    /** метод clear() очищает список.*/
    public void clear() {
        head = new Node();
    }

}
