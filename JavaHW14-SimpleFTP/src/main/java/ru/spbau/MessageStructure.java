package ru.spbau;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class MessageStructure {

    public static int lengthNumberOfBytes = 4;

    public static int listType = 1;

    public static int getType = 2;

    private SocketChannel socketChannel;

    public MessageStructure(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public int readInt() throws IOException {

        ByteBuffer length = ByteBuffer.allocate(lengthNumberOfBytes);

        while (length.hasRemaining()) {
            socketChannel.read(length);
        }

        length.flip();

        int n = length.getInt();

        return n;

    }

    public ByteBuffer readIntToBuffer() throws IOException {

        ByteBuffer length = ByteBuffer.allocate(lengthNumberOfBytes);

        socketChannel.read(length);

        return length;

    }

    public ByteBuffer readBytesToBuffer(int n) throws IOException {

        ByteBuffer message = ByteBuffer.allocate(n);

        socketChannel.read(message);

        return message;

    }


    public boolean readBool() throws IOException {
        int n = readInt();
        return n == 1;
    }

    public String readString() throws IOException {

        String string = new String(readByteArray());

        return string;

    }

    public byte[] readByteArray() throws IOException {

        int length = readInt();

        ByteBuffer message = ByteBuffer.allocate(length);

        while (message.hasRemaining()) {
            socketChannel.read(message);
        }

        return message.array();

    }


    public void writeInt(int n) throws IOException {

        ByteBuffer buf = ByteBuffer.allocate(lengthNumberOfBytes);
        buf.putInt(n);

        buf.flip();
        socketChannel.write(buf);

    }

    public void writeIntToBuffer(int n, ByteBuffer buffer) {
        buffer.putInt(n);
    }

    public void writeStringToBuffer(String s, ByteBuffer buffer) {
        writeByteArrayToBuffer(s.getBytes(), buffer);
    }

    public void writeBoolToBuffer(boolean bool, ByteBuffer buffer) {
        if (bool) {
            writeIntToBuffer(1, buffer);
        } else {
            writeIntToBuffer(0, buffer);
        }
    }

    public void writeByteArrayToBuffer(byte[] bytes, ByteBuffer buffer) {
        buffer.putInt(bytes.length);
        buffer.put(bytes);
    }

    public void writeBool(boolean bool) throws IOException {
        if (bool) {
            writeInt(1);
        } else {
            writeInt(0);
        }
    }

    public void writeByteArray(byte[] bytes) throws IOException {

        writeInt(bytes.length);

        socketChannel.write(ByteBuffer.wrap(bytes));

    }

    public void writeString(String string) throws IOException {

        writeByteArray(string.getBytes());

    }

}
