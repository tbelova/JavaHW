package ru.spbau;

import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import org.jetbrains.annotations.NotNull;

/**
 * Класс, отвечающий за поле для конкретной игры.
 */
public class Field {

    private final int BUTTON_SIZE = 30;

    private Button buttonPressed = null;

    private Button buttonPressedSecond = null;

    private Game game;

    /**
     * Конструктор от игры.
     */
    public Field(@NotNull Game game) {
        this.game = game;
    }

    /**
     * Возвращает поле для конкретной игры.
     */
    public @NotNull GridPane getGrid() {

        GridPane gridPane = new GridPane();

        for (int i = 0; i < game.fieldSize(); i++) {
            for (int j = 0; j < game.fieldSize(); j++) {
                Button button = new Button();
                button.setId(new Cell(i, j).getId());
                button.setPrefWidth(BUTTON_SIZE);
                button.setPrefHeight(BUTTON_SIZE);
                button.setOnMouseClicked(event -> {

                    if (buttonPressed != null && buttonPressedSecond != null) {

                        buttonPressed.setText("");
                        buttonPressedSecond.setText("");

                        buttonPressed = null;
                        buttonPressedSecond = null;

                        return;

                    }

                    Cell cell = getCell(button.getId());
                    button.setText(game.get(cell.getX(), cell.getY()).toString());

                    if (buttonPressed == button) {
                        return;
                    }

                    if (buttonPressed == null) {
                        buttonPressed = button;
                    } else {

                        Cell cell1 = getCell(buttonPressed.getId());

                        if (game.push(cell.getX(), cell.getY(), cell1.getX(), cell1.getY())) {

                            button.setDisable(true);
                            buttonPressed.setDisable(true);
                            buttonPressed = null;

                        } else {
                            buttonPressedSecond = button;
                        }

                    }

                });

                gridPane.add(button, i, j, 1, 1);

            }
        }

        return gridPane;
    }

    /**
     * Возвращает размер кнопки на поле.
     */
    public int getButtonSize() {
        return BUTTON_SIZE;
    }

    private @NotNull Cell getCell(@NotNull String s) {
        String[] coordinates = s.split(",");
        return new Cell(new Integer(coordinates[0]), new Integer(coordinates[1]));
    }

    private class Cell {

        private int x;
        private int y;

        public Cell(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public @NotNull String getId() {
            return x + "," + y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

    }


}
