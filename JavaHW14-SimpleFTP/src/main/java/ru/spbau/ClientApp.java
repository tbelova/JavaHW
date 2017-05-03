package ru.spbau;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ClientApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        Manager manager = new Manager();

        primaryStage.setTitle("FTP CLIENT");

        Button saveButton = SaveButton.get();

        FileSystemTableView table = new FileSystemTableView(manager);

        saveButton.setOnAction(value -> {
            TableViewItem item = table.get().getSelectionModel().getSelectedItem();

            if (!item.isDir()) {

                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save to");

                File selectedFile = fileChooser.showSaveDialog(primaryStage);

                try {
                    if (selectedFile != null) {
                        System.out.println(selectedFile.getPath());
                        manager.saveFile(item.getName(), selectedFile.toPath());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException();
                }

            }

        });


        VBox vBox = new VBox(table.get(), saveButton);

        Scene scene = new Scene(vBox, 400, 300);
        primaryStage.setScene(scene);

        primaryStage.show();

    }

    public static void main(String[] args) {
        Application.launch(args);
    }

}