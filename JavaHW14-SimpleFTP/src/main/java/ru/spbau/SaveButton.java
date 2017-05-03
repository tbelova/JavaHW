package ru.spbau;

import javafx.scene.control.Button;
import javafx.stage.FileChooser;

/**  Класс, отвечающий за кнопку сохранения файла. */
public class SaveButton {

    /** Настраивает и возвращает кнопку сохранения файла.*/
    public static Button get() {
        Button button = new Button("SAVE FILE");

        return button;
    }

}
