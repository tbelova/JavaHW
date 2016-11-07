package ru.spbau;


import java.util.AbstractSet;
import java.util.Iterator;

/** Реализация интерфейса MyTreeSet, в котором элементы хранятся в обратном порядке.*/
public class ReversedBinaryTree<E> extends AbstractSet<E> implements MyTreeSet<E> {
    private BinaryTree<E> binaryTree;

    /** Принимает BinaryTree и конструирует дерево, хранящее элементы в обратном порядке.*/
    public ReversedBinaryTree(BinaryTree<E> binaryTree) {
        this.binaryTree = binaryTree;
    }

    /** Добавляет элемент. Возвращает true, если добавление произошло успешно, и false иначе.*/
    @Override
    public boolean add (E e) {
        return binaryTree.add(e);
    }

    /** Удаляет элемент. Возвращает true, если элемент был в дереве, и false иначе.*/
    @Override
    public boolean remove (Object e) {
        return binaryTree.remove(e);
    }

    /** Возвращает количество элементов в дереве.*/
    @Override
    public int size() {
        return binaryTree.size();
    }

    /**
     * Возвращает итератор.
     * В случае инвалидации итератора, кинет ConcurrentModificationException.
     */
    @Override
    public Iterator<E> iterator() {
        return binaryTree.descendingIterator();
    }

    /**
     * Возвращает итератор, который двигается в противоположном направлении.
     * В случае инвалидации итератора, кинет ConcurrentModificationException.
     */
    @Override
    public Iterator<E> descendingIterator() {
        return binaryTree.iterator();
    }

    /** Возвращает MyTreeSet, в котором элементы лежат в обратном порядке.*/
    @Override
    public MyTreeSet<E> descendingSet() {
        return binaryTree;
    }

    /** Возвращает первый элемент.*/
    @Override
    public E first() {
        return binaryTree.last();
    }

    /** Возвращает последний элемент.*/
    @Override
    public E last() {
        return binaryTree.first();
    }

    /**
     * Принимает элемент e.
     * Возвращает самый большой элемент среди меньших e.
     * Или null, если такого элемента нет.
     */
    @Override
    public E lower(E e) {
        return binaryTree.higher(e);
    }

    /**
     * Принимает элемент e.
     * Возвращает самый большой элемент среди меньших или равных e.
     * Или null, если такого элемента нет.
     */
    @Override
    public E floor(E e) {
        return binaryTree.ceiling(e);
    }

    /**
     * Принимает элемент e.
     * Возвращает самый маленький элемент среди больших или равных e.
     * Или null, если такого элемента нет.
     */
    @Override
    public E ceiling(E e) {
        return binaryTree.floor(e);
    }

    /**
     * Принимает элемент e.
     * Возвращает самый маленький элемент среди больших e.
     * Или null, если такого элемента нет.
     */
    @Override
    public E higher(E e) {
        return binaryTree.lower(e);
    }
}
