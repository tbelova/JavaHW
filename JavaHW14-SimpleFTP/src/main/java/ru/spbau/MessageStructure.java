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

        System.out.println("get = " + n);

        return n;

    }

    public boolean readBool() throws IOException {
        int n = readInt();
        return n == 1;
    }

    public String readString() throws IOException {

        String string = new String(readByteArray());

        System.out.println("get string " + string);

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

        System.out.println("send = " + n);

        ByteBuffer buf = ByteBuffer.allocate(lengthNumberOfBytes);
        buf.putInt(n);

        buf.flip();
        socketChannel.write(buf);

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

        System.out.println("send string: " + string);

        writeByteArray(string.getBytes());

    }

}
