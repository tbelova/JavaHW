package ru.spbau;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;


/** Класс, позволяющий общаться по каналу.*/
public class MessageStructure {

    public static int lengthNumberOfBytes = 4;

    public static int listType = 1;

    public static int getType = 2;

    private SocketChannel socketChannel;

    /** Конструктор от канала.*/
    public MessageStructure(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    /** В блокирующем режиме читает из канала int и возвращает его.*/
    public int readInt() throws IOException {

        ByteBuffer length = ByteBuffer.allocate(lengthNumberOfBytes);

        while (length.hasRemaining()) {
            socketChannel.read(length);
        }

        length.flip();

        int n = length.getInt();

        return n;

    }

    /** В неблокирующем режиме читает из канала и возвращает буфер с (возможно не полностью прочитанным) int-ом.*/
    public ByteBuffer readIntToBuffer() throws IOException {

        ByteBuffer length = ByteBuffer.allocate(lengthNumberOfBytes);

        socketChannel.read(length);

        return length;

    }

    /**
     * В неблокирующем режиме читает из канала указанное число байт (возможно меньше)
     * Возвращает буфер с тем, что прочиталось.
     */
    public ByteBuffer readBytesToBuffer(int n) throws IOException {

        ByteBuffer message = ByteBuffer.allocate(n);

        socketChannel.read(message);

        return message;

    }


    /** В блокирующем режиме читает из канала булево значение.*/
    public boolean readBool() throws IOException {
        int n = readInt();
        return n == 1;
    }

    /** В блокирующем режиме читает из канала строку.*/
    public String readString() throws IOException {

        String string = new String(readByteArray());

        return string;

    }

    /** В блокирующем режиме читает из канала последовательность байт.*/
    public byte[] readByteArray() throws IOException {

        int length = readInt();

        ByteBuffer message = ByteBuffer.allocate(length);

        while (message.hasRemaining()) {
            socketChannel.read(message);
        }

        return message.array();

    }


    /** В блокирующем режиме пишет указанное число в канал.*/
    public void writeInt(int n) throws IOException {

        ByteBuffer buf = ByteBuffer.allocate(lengthNumberOfBytes);
        buf.putInt(n);

        buf.flip();
        socketChannel.write(buf);

    }

    /** В блокирующем режиме пишет указанное булево зачение в канал.*/
    public void writeBool(boolean bool) throws IOException {
        if (bool) {
            writeInt(1);
        } else {
            writeInt(0);
        }
    }

    /** В блокирующем режиме пишет указанную последовательность байт в канал.*/
    public void writeByteArray(byte[] bytes) throws IOException {

        writeInt(bytes.length);

        socketChannel.write(ByteBuffer.wrap(bytes));

    }

    /** В блокирующем режиме пишет указанную строку в канал.*/
    public void writeString(String string) throws IOException {

        writeByteArray(string.getBytes());

    }

}
