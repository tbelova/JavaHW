package ru.spbau;

import java.nio.ByteBuffer;

public class MessageWrite extends AttachedMessage {

    private ByteBuffer buffer;

    public MessageWrite(ByteBuffer buffer) {

        this.messageType = AttachedMessage.WRITE;

        this.buffer = buffer;

    }

    public ByteBuffer getBuffer() {
        return buffer;
    }

}
