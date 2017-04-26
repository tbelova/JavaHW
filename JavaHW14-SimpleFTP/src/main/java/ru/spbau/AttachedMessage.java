package ru.spbau;

public class AttachedMessage {

    public static int REQUEST = 0;

    public static int READ = 1;

    public static int WRITE = 2;

    protected int messageType;

    public int getMessageType() {
        return messageType;
    }

}
