package ru.spbau;


import java.nio.ByteBuffer;

public class MessageRead extends AttachedMessage {

    private ByteBuffer type;

    private ByteBuffer size;

    private ByteBuffer buffer;

    public MessageRead(ByteBuffer type, ByteBuffer size, ByteBuffer buffer) {

        this.messageType = AttachedMessage.READ;

        this.type = type;
        this.size = size;
        this.buffer = buffer;

    }

    public ByteBuffer getType() {
        return type;
    }

    public ByteBuffer getSize() {
        return size;
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }

}
