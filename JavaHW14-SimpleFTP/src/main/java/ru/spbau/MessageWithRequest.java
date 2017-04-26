package ru.spbau;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/** Класс, объекты которого прикрепляются к selectionKey. Хранит в себе запрос к серверу.*/
public class MessageWithRequest extends AttachedMessage {

    private int type;

    private Path path;

    /** Конструктор от типа запроса и пути до файла/папки.*/
    public MessageWithRequest(int type, @NotNull Path path) {

        this.messageType = AttachedMessage.REQUEST;

        this.type = type;
        this.path = path;

    }

    /** Возвращает тип запроса.*/
    public int getType() {
        return type;
    }

    /** Возвращает путь до файла/папки.*/
    public @NotNull Path getPath() {
        return path;
    }

}
