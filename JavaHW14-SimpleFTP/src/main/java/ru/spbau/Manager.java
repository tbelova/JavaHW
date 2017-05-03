package ru.spbau;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Manager {

    private final Client client;
    private  static final Path ROOT_PATH = Paths.get("FAKE_FOLDER/root");

    private static Logger logger = LoggerFactory.getLogger(Manager.class);

    private Path currentPath;

    public Manager() throws IOException {

        logger.debug("in constructor");

        currentPath = ROOT_PATH;
        client = new Client();
    }

    public List<FileWithType> getFileList() throws IOException {

        logger.debug("in getFileList");

        client.connect();
        List<FileWithType> files = client.list(ROOT_PATH.relativize(currentPath).toString());
        client.disconnect();

        return files;

    }

    public void goTo(@NotNull TableViewItem item) throws IOException {

        logger.debug("in goTo {}", item.getName());
        logger.debug("path before = {}", currentPath);

        if (item.getName().equals("..")) {
            currentPath = currentPath.getParent();
        } else if (item.isDir()) {
            currentPath = currentPath.resolve(item.getName());
        }

        logger.debug("path after = {}", currentPath);

    }

    public void saveFile(@NotNull String fileName, @NotNull Path homePath) throws IOException {

        logger.debug("in saveFile {} to {}", fileName, homePath);

        client.connect();
        client.get(ROOT_PATH.relativize(currentPath.resolve(fileName)).toString(), homePath.toString());
        client.disconnect();

    }

    public @NotNull Path getCurrentPath() {
        return currentPath;
    }

}
