package ru.spbau;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Этот класс отвечает за логику игры.
 * Тут хранится поле и результат игры.
 */
public class Game {

    private final int N = 6;

    private int[][] field;

    private int rest = N * N / 2;

    /**
     * Конструктор. Заполняет поле NxN случайными числами в пределах [0..N]
     */
    public Game() {

        field = new int[N][N];

        List<Integer> rands = new ArrayList<>();

        for (int i = 0; i < N * N / 2; ++i) {
            Integer number = (int)(Math.random() * N);
            rands.add(number);
            rands.add(number);
        }

        Collections.shuffle(rands);

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                field[i][j] = rands.get(i * N + j);
            }
        }

    }

    /**
     * Принимает координаты двух клеток, возвращает, одинаковые ли числа там стоят.
     */
    public boolean push(int x1, int y1, int x2, int y2) {
        return field[x1][y1] == field[x2][y2];
    }

    /**
     * Возвращает true, если игра закончилась.
     */
    public boolean win() {
        return rest == 0;
    }

    /**
     * Возвращает содержимое клетки.
     */
    public @NotNull Integer get(int i, int j) {
        return field[i][j];
    }

    /**
     * Возвращает размер поля.
     */
    public int fieldSize() {
        return N;
    }

}
