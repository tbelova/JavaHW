package ru.spbau;

import java.nio.file.Path;

public class MessageWithRequest extends AttachedMessage {

    private int type;

    private Path path;

    public MessageWithRequest(int type, Path path) {

        this.messageType = AttachedMessage.REQUEST;

        this.type = type;
        this.path = path;

    }

    public int getType() {
        return type;
    }

    public Path getPath() {
        return path;
    }

}
