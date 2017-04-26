package ru.spbau;

/** Класс, объекты которого прикрепляются к selectionKey.*/
public class AttachedMessage {

    public static int REQUEST = 0;

    public static int READ = 1;

    public static int WRITE = 2;

    protected int messageType;

    /**
     * Возвращает тип прикрепленного объекта.
     * REQUEST --объект класса MessageWithRequest
     * READ -- объект класса MessageRead
     * WRITE -- объект класса MessageWrite
     */
    public int getMessageType() {
        return messageType;
    }

}
