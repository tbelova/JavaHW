package ru.spbau;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * Класс, отвечающий за интерфейс.
 */
public class Client extends Application {

    /**
     * Запускает игру.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("Game");
        primaryStage.setResizable(false);

        Game game = new Game();

        Field field = new Field(game);

        GridPane gridPane = field.getGrid();

        int sceneSize = field.getButtonSize() * game.fieldSize();

        Scene scene = new Scene(gridPane, sceneSize, sceneSize);

        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public static void main(String[] args) {
        Application.launch(args);
    }

}
