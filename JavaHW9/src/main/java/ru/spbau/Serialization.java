package ru.spbau;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.util.Scanner;

public class Serialization {
    public static void serialize(@NotNull Object object, @NotNull OutputStream outputStream) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(outputStream);
        for (Field field: object.getClass().getFields()) {
            field.setAccessible(true);
            try {
                Object fieldValue = null;
                fieldValue = field.get(object);
                writer.write(fieldValue.toString());
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static <T> T deserialize(@NotNull InputStream inputStream, @NotNull Class<T> clazz) {
        Scanner scanner = new Scanner(inputStream);
        T object;
        try {
            object = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        for (Field field: clazz.getFields()) {
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
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return object;
    }

}
