package com.company;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Matrix -- это класс, который хранит матрицу, предоставляет доступ
 * к ее элементам, умеет сортировать столбцы матрицы по первым элементам
 * и выводить элементы матрицы в порядке обхода по спирали с началом в центре.
 */
public class Matrix {
    private int n;
    private int[][] matrix;

    /**
     * Pair -- это вспомогательный класс, который хранит пару int-ов и
     * предоставляет доступ к каждому элементу пары.
     * Также умеет сравнивать пары на больше-меньше.
     */
    public class Pair implements Comparable<Pair> {
        public int first, second;

        /** конструктор Pair(int first, int second) принимает два int-a и строит из них пару. */
        public Pair(int first, int second) {
            this.first = first;
            this.second = second;
        }

        /** метод add(Pair pair) принимает Pair и поэлементно прибавляет его к this. */
        public void add(Pair pair) {
            first += pair.first;
            second += pair.second;
        }

        /** метод compareTo(Pair pair) принимает Pair и сравнивает его с this на больше-меньше. */
        public int compareTo(Pair pair) {
            if (first == pair.first) {
                if (second < pair.second) {
                    return -1;
                } else if (second > pair.second) {
                    return 1;
                } else {
                    return 0;
                }
            } else {
                if (first < pair.first) {
                    return -1;
                } else {
                    return 1;
                }
            }
        }
    }

    /** конструктор Matrix(int n) от размера матрицы. */
    public Matrix(int n) {
        this.n = n;
        matrix = new int[n][n];
    }

    /** конструктор Matrix(int[][] matrix) от квадратного двумерного массива int-ов. */
    public Matrix(int[][] matrix) {
        n = matrix.length;
        this.matrix = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                this.matrix[i][j] = matrix[i][j];
            }
        }
    }

    /** метод size() возвращает размер матрицы. */
    public int size() {
        return n;
    }

    /** метод setAt(i, j, number) заисывает number в элемент матрицы с индексом (i, j). */
    public void setAt(int i, int j, int number) {
        matrix[i][j] = number;
    }

    /** метод get(i, j) возвращает элемент матрицы с индексом (i, j). */
    public int get(int i, int j) {
        return matrix[i][j];
    }

    /**
     * метод get(pair) -- это вспомогательный метод, возвращающий элемент
     * матрицы с индексом (pair.first, pair.second).
     * */
    private int get(Pair pair) {
        return matrix[pair.first][pair.second];
    }

    /**
     * метод spiralPrint() возвращает ArrayList<Integer>, в котором содержаться элементы
     * матрицы в порядке обхода по спирали с началом в центре.
     */
    public ArrayList<Integer> spiralPrint() {
        ArrayList<Integer> vector = new ArrayList<Integer>();
        Pair st = new Pair(n / 2, n / 2);

        vector.add(get(st));
        Pair[][] dir = { { new Pair(1, 0), new Pair(0, 1) }, { new Pair(-1, 0), new Pair(0, -1)} };
        for (int cnt = 1; cnt < n; cnt++) {
            for (Pair to : dir[(cnt + 1) % 2]) {
                for (int i = 0; i < cnt; i++) {
                    st.add(to);
                    vector.add(get(st));
                }
            }
        }

        for (int i = 0; i < n - 1; i++) {
            st.first++;
            vector.add(get(st));
        }

        return vector;

    }

    /** метод sortColomnsByFirstElement() сортирует столбцы матрицы по первому элементу. */
    public void sortColomnsByFirstElement() {
        int[][] newMatrix = new int[n][n];

        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                newMatrix[i][j] = matrix[i][j];
            }
        }

        ArrayList<Pair> vector = new ArrayList<Pair>();
        for (int i = 0; i < n; ++i) {
            vector.add(new Pair(newMatrix[0][i], i));
        }

        Collections.sort(vector);

        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                matrix[i][j] = newMatrix[i][vector.get(j).second];
            }
        }
    }
}
