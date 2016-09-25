package com.company;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Matrix -- это класс, который хранит матрицу, предоставляет доступ
 * к ее элементам, умеет сортировать столбцы матрицы по первым элементам
 * и выводить элементы матрицы в порядке обхода по спирали с началом в центре.
 */
public class Matrix {
    private final int n;
    private int[][] matrix;

    /**
     * Pair -- это вспомогательный класс, который хранит пару int-ов и
     * предоставляет доступ к каждому элементу пары.
     * Также умеет сравнивать пары на больше-меньше.
     */
    public static class Pair implements Comparable<Pair> {
        private int first;
        private int second;

        /** конструктор принимает два int-a и строит из них пару. */
        public Pair(int first, int second) {
            this.first = first;
            this.second = second;
        }

        /** возвращает первый элемент пары. */
        public int first() {
            return first;
        }

        /** возвращает второй элемент пары. */
        public  int second() {
            return second;
        }

        /** принимает Pair и поэлементно прибавляет его к this. */
        public void add(Pair pair) {
            first += pair.first;
            second += pair.second;
        }

        /** принимает Pair и сравнивает его с this на больше-меньше. */
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

    /** конструктор от размера квадратной матрицы. */
    public Matrix(int n) {
        this.n = n;
        matrix = new int[n][n];
    }

    /** конструктор от квадратного двумерного массива int-ов. */
    public Matrix(int[][] matrix) {
        n = matrix.length;
        this.matrix = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                this.matrix[i][j] = matrix[i][j];
            }
        }
    }

    /** возвращает размер квадратной матрицы. */
    public int size() {
        return n;
    }

    /** записывает number в элемент матрицы с индексом (i, j). */
    public void setAt(int i, int j, int number) {
        matrix[i][j] = number;
    }

    /** возвращает элемент матрицы с индексом (i, j). */
    public int get(int i, int j) {
        return matrix[i][j];
    }

    /**
     * вспомогательный метод, возвращающий элемент
     * матрицы с индексом (pair.first, pair.second).
     * */
    private int get(Pair pair) {
        return matrix[pair.first][pair.second];
    }

    /**
     * возвращает ArrayList<Integer>, в котором содержатся элементы
     * матрицы в порядке обхода по спирали с началом в центре.
     */
    public ArrayList<Integer> spiralPrint() {
        ArrayList<Integer> vector = new ArrayList<Integer>();
        Pair pos = new Pair(n / 2, n / 2);

        vector.add(get(pos));
        Pair[][] dir = { { new Pair(1, 0), new Pair(0, 1) }, { new Pair(-1, 0), new Pair(0, -1)} };
        for (int cnt = 1; cnt < n; cnt++) {
            for (Pair to : dir[(cnt + 1) % 2]) {
                for (int i = 0; i < cnt; i++) {
                    pos.add(to);
                    vector.add(get(pos));
                }
            }
        }

        for (int i = 0; i < n - 1; i++) {
            pos.first++;
            vector.add(get(pos));
        }

        return vector;
    }

    /** сортирует столбцы матрицы по первому элементу. */
    public void sortColumnsByFirstElement() {
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
