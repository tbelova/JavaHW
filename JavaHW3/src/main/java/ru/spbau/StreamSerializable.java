package ru.spbau;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/** Интерфейс, реализующий работу с потоками ввода/вывода. */
public interface StreamSerializable {
    /** Принимает поток вывода и пишет в него объект. */
    void serialize(OutputStream out) throws IOException;

    /** Принимает поток ввода и читает из него объект. */
    void deserialize(InputStream in) throws IOException, ClassNotFoundException;
}
