package ru.spbau;


import java.util.AbstractSet;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

/** Реализация интерфейса MyTreeSet с использованием бинарного дерева.*/
public class BinaryTree<E> extends AbstractSet<E> implements MyTreeSet<E> {

    private Node root = null;
    private Node first = null;
    private Node last = null;
    private int size = 0;
    private Comparator<E> comparator;
    private int timer = 0;

    /** Конструктор без параметров создает дерево, хранящее элементы в естественном порядке.*/
    public BinaryTree() {
        comparator = (x, y) -> ((Comparable<E>)x).compareTo(y);
    }

    /**
     * Принимает компаратор.
     * Создает дерево, хранящее элементы в порядке, определенном этим компаратором.
     */
    public BinaryTree(Comparator<E> comparator) {
        this.comparator = comparator;
    }

    /** Добавляет элемент. Возвращает true, если добавление произошло успешно, и false иначе.*/
    @Override
    public boolean add(E e) {
        timer++;

        Node elem = new Node(e);
        size++;
        if (root == null) {
            root = elem;
            first = root;
            last = root;
        } else {
            Node b = findToInsert(e);

            if (comparator.compare(e, b.value) < 0) {
                elem.prev = b.prev;
                if (b.prev != null) {
                    b.prev.next = elem;
                }
                b.prev = elem;
                elem.next = b;

                b.left = elem;
                elem.parent = b;

                if (b == first) {
                    first = elem;
                }
            } else {
                elem.next = b.next;
                if (b.next != null) {
                    b.next.prev = elem;
                }
                b.next = elem;
                elem.prev = b;

                b.right = elem;
                elem.parent = b;

                if (b == last) {
                    last = elem;
                }
            }

        }

        return true;
    }

    /** Удаляет элемент. Возвращает true, если элемент был в дереве, и false иначе.*/
    @Override
    public boolean remove(Object e) {
        timer++;

        if (root == null) {
            return false;
        }
        E elem = (E)e;
        Node floor = findNode(root, elem, comparator);
        if (comparator.compare(floor.value, elem) > 0) {
            floor = floor.prev;
        }
        if (floor == null || comparator.compare(floor.value, elem) != 0) {
            return false;
        }
        size--;
        if (floor.right == null) {
            if (floor == root) {
                root = floor.left;
                return true;
            }
            if (floor.parent.right == floor) {
                floor.parent.right = floor.left;
            } else {
                floor.parent.left = floor.left;
            }
            return true;
        }
        E tmp = floor.next.value;
        floor.next.value = floor.value;
        floor.value = tmp;

        floor = floor.next;
        if (floor == root) {
            root = floor.right;
        }
        if (floor.parent.right == floor) {
            floor.parent.right = floor.right;
        } else {
            floor.parent.left = floor.right;
        }
        return true;

    }

    /** Возвращает количество элементов в дереве.*/
    @Override
    public int size() {
        return size;
    }

    /**
     * Возвращает итератор.
     * В случае инвалидации итератора, кинет ConcurrentModificationException.
     */
    @Override
    public Iterator<E> iterator() {
        return new TreeIterator();
    }

    /**
     * Возвращает итератор, который двигается в противоположном направлении.
     * В случае инвалидации итератора, кинет ConcurrentModificationException.
     */
    @Override
    public Iterator<E> descendingIterator() {
        return new TreeDescendingIterator();
    }

    /** Возвращает MyTreeSet, в котором элементы лежат в обратном порядке.*/
    @Override
    public MyTreeSet<E> descendingSet() {
        return new ReversedBinaryTree<E>(this);
    }

    /** Возвращает первый элемент.*/
    @Override
    public E first() {
        return first.value;
    }

    /** Возвращает последний элемент.*/
    @Override
    public E last() {
        return last.value;
    }

    /**
     * Принимает элемент e.
     * Возвращает самый большой элемент среди меньших e.
     * Или null, если такого элемента нет.
     */
    @Override
    public E lower(E e) {
        if (root == null) {
            return null;
        }
        Node ceiling = findNode(root, e, (x, y) -> {
            if (comparator.compare(x, y) == 0) {
                return -1;
            } else {
                return comparator.compare(x, y);
            }
        });
        if (comparator.compare(ceiling.value, e) < 0) {
            return ceiling.value;
        } else {
            if (ceiling.prev == null) {
                return null;
            } else {
                return ceiling.prev.value;
            }
        }
    }


    /**
     * Принимает элемент e.
     * Возвращает самый большой элемент среди меньших или равных e.
     * Или null, если такого элемента нет.
     */
    @Override
    public E floor(E e) {
        if (root == null) {
            return null;
        }
        Node floor = findNode(root, e, comparator);
        if (comparator.compare(floor.value, e) <= 0) {
            return floor.value;
        } else {
            if (floor.prev == null) {
                return null;
            } else {
                return floor.prev.value;
            }
        }
    }

    /**
     * Принимает элемент e.
     * Возвращает самый маленький элемент среди больших или равных e.
     * Или null, если такого элемента нет.
     */
    @Override
    public E ceiling(E e) {
        if (root == null) {
            return null;
        }
        Node ceiling = findNode(root, e, (x, y) -> {
            if (comparator.compare(x, y) == 0) {
                return -1;
            } else {
                return comparator.compare(x, y);
            }
        });
        if (comparator.compare(ceiling.value, e) >= 0) {
            return ceiling.value;
        } else {
            if (ceiling.next == null) {
                return null;
            } else {
                return ceiling.next.value;
            }
        }
    }

    /**
     * Принимает элемент e.
     * Возвращает самый маленький элемент среди больших e.
     * Или null, если такого элемента нет.
     */
    @Override
    public E higher(E e) {
        if (root == null) {
            return null;
        }
        Node floor = findNode(root, e, comparator);
        if (comparator.compare(floor.value, e) > 0) {
            return floor.value;
        } else {
            if (floor.next == null) {
                return null;
            } else {
                return floor.next.value;
            }
        }
    }

    private Node findNode(Node node, E e, Comparator<E> comparator) {
        if (comparator.compare(e, node.value) < 0) {
            if (node.left == null) {
                return node;
            } else {
                return findNode(node.left, e, comparator);
            }
        } else {
            if (node.right == null) {
                return node;
            } else {
                return findNode(node.right, e, comparator);
            }
        }
    }

    private Node findToInsert(E e) {
        if (root == null) {
            return null;
        }
        return findNode(root, e, comparator);
    }

    private class Node {
        E value;

        Node next = null;
        Node prev = null;

        Node left = null;
        Node right = null;
        Node parent = null;

        public Node(E e) {
            value = e;
        }
    }

    private class TreeIterator implements Iterator<E> {
        private int startTime = timer;
        private Node pos = first;

        public boolean hasNext() throws ConcurrentModificationException {
            if (timer > startTime) {
                throw new ConcurrentModificationException();
            }
            return (pos != null);
        }

        public E next() throws ConcurrentModificationException {
            if (timer > startTime) {
                throw new ConcurrentModificationException();
            }
            E res = pos.value;
            pos = pos.next;
            return res;
        }
    }

    private class TreeDescendingIterator implements Iterator<E> {
        private int startTime = timer;
        private Node pos = last;

        public boolean hasNext() throws ConcurrentModificationException {
            if (timer > startTime) {
                throw new ConcurrentModificationException();
            }
            return (pos != null);
        }

        public E next() throws  ConcurrentModificationException {
            if (timer > startTime) {
                throw new ConcurrentModificationException();
            }
            E res = pos.value;
            pos = pos.prev;
            return res;
        }
    }

}
