package ru.spbau;

import java.nio.ByteBuffer;


public class BufferMessageStructure {

    private ByteBuffer buffer;

    public BufferMessageStructure(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    public void writeInt(int n) {
        buffer.putInt(n);
    }

    public void writeString(String s) {
        writeByteArray(s.getBytes());
    }

    public void writeBool(boolean bool) {
        if (bool) {
            writeInt(1);
        } else {
            writeInt(0);
        }
    }

    public void writeByteArray(byte[] bytes) {
        buffer.putInt(bytes.length);
        buffer.put(bytes);
    }


}
