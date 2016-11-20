package ru.spbau;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.util.Scanner;

/** Класс с методами serialize и deserialize.*/
public class Serialization {
    /**  Записывает состояние полей переданного объекта в поток.*/
    public static void serialize(@NotNull Object object, @NotNull OutputStream outputStream) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(outputStream);

        for (Field field: object.getClass().getDeclaredFields()) {
            if (field.isSynthetic()) {
                continue;
            }
            field.setAccessible(true);
            try {
                if (field.getType() == String.class) {
                    String s = (String)field.get(object);
                    writer.write(s.length() + "\n");
                    for (int i = 0; i < s.length(); i++) {
                        writer.write((int)s.charAt(i) + "\n");
                    }
                } else {
                    writer.write(field.get(object) + "\n");
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        writer.flush();
    }

    /** Создаёт экземпляр класса и инициализирует его поля данными из потока.*/
    public static <T> T deserialize(@NotNull InputStream inputStream, @NotNull Class<T> clazz) {
        Scanner scanner = new Scanner(inputStream);
        T object;
        try {
            object = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        for (Field field: clazz.getDeclaredFields()) {
            if (field.isSynthetic()) {
                continue;
            }
            field.setAccessible(true);
            try {
                if (field.getType() == Character.TYPE) {
                    field.set(object, scanner.findInLine(".").charAt(0));
                }
                if (field.getType() == Boolean.TYPE) {
                    field.set(object, scanner.nextBoolean());
                }
                if (field.getType() == Byte.TYPE) {
                    field.set(object, scanner.nextByte());
                }
                if (field.getType() == Short.TYPE) {
                    field.set(object, scanner.nextShort());
                }
                if (field.getType() == Integer.TYPE) {
                    field.set(object, scanner.nextInt());
                }
                if (field.getType() == Long.TYPE) {
                    field.set(object, scanner.nextLong());
                }
                if (field.getType() == Float.TYPE) {
                    field.set(object, scanner.nextFloat());
                }
                if (field.getType() == Double.TYPE) {
                    field.set(object, scanner.nextDouble());
                }
                if (field.getType() == String.class) {
                    int numberOfCharacters = scanner.nextInt();
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < numberOfCharacters; i++) {
                        stringBuilder.append((char)scanner.nextInt());
                    }
                    field.set(object, stringBuilder.toString());
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return object;
    }

}
