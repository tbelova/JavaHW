package ru.spbau;

import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;


/** Класс, объекты которого прикрепляются к selectionKey. Хранит в себе частично прочитанное сообщение.*/
public class MessageRead extends AttachedMessage {

    private ByteBuffer type;

    private ByteBuffer size;

    private ByteBuffer buffer;

    public MessageRead(@Nullable ByteBuffer type, @Nullable ByteBuffer size, @Nullable ByteBuffer buffer) {

        this.messageType = AttachedMessage.READ;

        this.type = type;
        this.size = size;
        this.buffer = buffer;

    }

    public @Nullable ByteBuffer getType() {
        return type;
    }

    public @Nullable ByteBuffer getSize() {
        return size;
    }

    public @Nullable ByteBuffer getBuffer() {
        return buffer;
    }

}
