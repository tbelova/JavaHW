package ru.spbau;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/** Класс, отвечающий за работу с таблицей, хранящей информацию о файлах и папках.*/
public class FileSystemTableView {

    private TableView<TableViewItem> table;
    private Manager manager;
    private ObservableList<TableViewItem> data;
    private Path rootPath;

    private static Logger logger = LoggerFactory.getLogger(FileSystemTableView.class);

    /**
     * Конструктор от объекта типа Manager.
     * Создает таблицу, в которой есть всего одна папка.
     */
    public FileSystemTableView(@NotNull Manager manager) throws IOException {

        logger.debug("in constructor");

        rootPath = manager.getCurrentPath();

        table = new TableView<>();

        TableColumn<TableViewItem, String> fileNameColumn = new TableColumn<>("File name");
        fileNameColumn.setCellValueFactory(param -> new StringBinding() {
            @Override
            protected String computeValue() {
                return param.getValue().getName();
            }
        });

        TableColumn<TableViewItem, Boolean> isDirColumn = new TableColumn<>("Is dir");
        isDirColumn.setCellValueFactory(param -> new BooleanBinding() {
            @Override
            protected boolean computeValue() {
                return param.getValue().isDir();
            }
        });

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getColumns().add(fileNameColumn);
        table.getColumns().add(isDirColumn);

        data = FXCollections.observableArrayList();
        data.add(new TableViewItem(manager.getCurrentPath().getFileName().toString(), true));
        table.setItems(data);

        table.setRowFactory(param -> {
            TableRow<TableViewItem> row = new TableRow<>();

            row.setOnMouseClicked(event -> {

                if (event.getClickCount() < 2) {
                    return;
                }

                TableViewItem item = row.getItem();
                try {
                    if (item.isDir()) {
                        manager.goTo(item);
                        update();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException();
                }
            });

            return row;
        });

        manager.goTo(TableViewItem.BACK_ITEM);
        this.manager = manager;

    }

    /** Возвращает таблицу.*/
    public @NotNull TableView<TableViewItem> get() {
        return table;
    }

    /** Обновляет состояние таблицы в зависимости от текущей папки.*/
    public void update() throws IOException {

        logger.debug("in update");

        data.clear();
        if (!manager.getCurrentPath().equals(rootPath.getParent())) {
            List<FileWithType> files = manager.getFileList();
            List<TableViewItem> allFiles = files.stream().map(TableViewItem::new).collect(Collectors.toList());

            data.add(TableViewItem.BACK_ITEM);
            data.addAll(allFiles);
        } else {
            data.add(new TableViewItem(rootPath.getFileName().toString(), true));
        }

    }

}
