package ru.spbau;

import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;


/** Класс, объекты которого прикрепляются к selectionKey. Хранит в себе частично прочитанное сообщение.*/
public class MessageRead extends AttachedMessage {

    private ByteBuffer type;

    private ByteBuffer size;

    private ByteBuffer buffer;

    /** Конструктор, который просто копирует значения переданных аргументов.*/
    public MessageRead(@Nullable ByteBuffer type, @Nullable ByteBuffer size, @Nullable ByteBuffer buffer) {

        this.messageType = AttachedMessage.READ;

        this.type = type;
        this.size = size;
        this.buffer = buffer;

    }

    /** Возвращает тип запроса:
     * 1 -- list
     * 2 -- get
     * */
    public @Nullable ByteBuffer getType() {
        return type;
    }

    /** Возвращает длину передаваемого пути.*/
    public @Nullable ByteBuffer getSize() {
        return size;
    }

    /** Возвращает переданный путь.*/
    public @Nullable ByteBuffer getBuffer() {
        return buffer;
    }

}
