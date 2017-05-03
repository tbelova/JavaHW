package ru.spbau;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;


/** Класс, позволяющий писать в буфер.*/
public class BufferMessageStructure {

    private final static int MAX_SIZE = 4096 * 4096;

    private ByteBuffer buffer;

    /** Конструирует BufferMessageStructure с большим буфером.*/
    public BufferMessageStructure() {
        this.buffer = ByteBuffer.allocate(MAX_SIZE);
    }

    /** Конструирует BufferMessageStructure с указанным буфером.*/
    public BufferMessageStructure(@NotNull ByteBuffer buffer) {
        this.buffer = buffer;
    }

    /** Возвращает буфер.*/
    public @NotNull ByteBuffer getBuffer() {
        return buffer;
    }

    /** Дописывает с буфер указанное число.*/
    public void writeInt(int n) {
        buffer.putInt(n);
    }

    /** Дописывает в буфер указанную строчку.*/
    public void writeString(@NotNull String s) {
        writeByteArray(s.getBytes());
    }

    /** Дописывает в буфер указанное булево значение.*/
    public void writeBool(boolean bool) {
        if (bool) {
            writeInt(1);
        } else {
            writeInt(0);
        }
    }

    /** Дописывает в буфер последовательность байт.*/
    public void writeByteArray(@NotNull byte[] bytes) {
        buffer.putInt(bytes.length);
        buffer.put(bytes);
    }


}
