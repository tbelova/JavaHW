package ru.spbau;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

/** Класс, объекты которого прикрепляются к selectionKey. Хранит в себе частично отправленное сообщение.*/
public class MessageWrite extends AttachedMessage {

    private ByteBuffer buffer;

    /** Конструктор от буфера, содержимое которого хотим отправить.*/
    public MessageWrite(@NotNull ByteBuffer buffer) {

        this.messageType = AttachedMessage.WRITE;

        this.buffer = buffer;

    }

    /** Возвращает буфер.*/
    public @NotNull ByteBuffer getBuffer() {
        return buffer;
    }

}
