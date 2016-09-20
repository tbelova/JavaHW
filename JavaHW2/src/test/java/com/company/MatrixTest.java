package com.company;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class MatrixTest {

    @Test
    public void sizeTest() throws Exception {
        Matrix matrix = new Matrix(5);
        assertEquals(5, matrix.size());
    }

    @Test
    public void setAtTest() throws Exception {
        Matrix matrix = new Matrix(5);
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                matrix.setAt(i, j, i + j);
            }
        }
    }

    @Test
    public void getTest() throws Exception {
        Matrix matrix = new Matrix(5);
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                matrix.setAt(i, j, i + j);
            }
        }

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                assertEquals(i + j, matrix.get(i, j));
            }
        }
    }

    @Test
    public void constructorTest() throws Exception {
        int[][] m = { {1, 2, 3},
                      {4, 5, 6},
                      {7, 8, 9} };
        Matrix matrix = new Matrix(m);

        for (int i = 1; i <= 9; ++i) {
            assertEquals(matrix.get((i - 1) / 3, (i - 1) % 3), i);
        }
    }

    @Test
    public void spiralPrintTest() throws Exception {
        int[][] m = { {7, 6, 5},
                      {8, 1, 4},
                      {9, 2, 3} };

        Matrix matrix = new Matrix(m);

        ArrayList<Integer> spiral = new ArrayList<Integer>();
        for (int i = 1; i <= 9; i++) {
            spiral.add(i);
        }

        assertEquals(spiral, matrix.spiralPrint());

    }

    @Test
    public void sortColomnsByFirstElementTest() throws Exception {
        int[][] m1 = { {7, 6, 5},
                       {8, 1, 4},
                       {9, 2, 3} };

        Matrix matrix1 = new Matrix(m1);
        matrix1.sortColumnsByFirstElement();

        int[][] m2 = { {5, 6, 7},
                       {4, 1, 8},
                       {3, 2, 9} };

        Matrix matrix2 = new Matrix(m2);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                assertEquals(matrix1.get(i, j), matrix2.get(i, j));
            }
        }
    }

}