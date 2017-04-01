package ru.spbau;

import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Format {

    private static String dateFormat = "MM/dd/yyyy HH:mm:ss";

    public static @NotNull Date readDate(@NotNull String date) throws ParseException {
        DateFormat df = new SimpleDateFormat(dateFormat);
        return df.parse(date);
    }

    public static @NotNull String writeDate(@NotNull Date date) {
        DateFormat df = new SimpleDateFormat(dateFormat);
        return df.format(date);
    }

    public static @NotNull String getSHAFromByteArray(@NotNull byte[] content) {
        return DigestUtils.sha1Hex(content);
    }

    public static void writeTo(@NotNull Path path, @NotNull byte[] content) throws IOException {
        OutputStream outputStream = Files.newOutputStream(path);
        outputStream.write(content);
        outputStream.close();
    }

    public static void writeTo(@NotNull Path path, @NotNull String content) throws IOException {
        writeTo(path, content.getBytes());
    }


}
