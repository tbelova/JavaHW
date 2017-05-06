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
import java.util.List;

/** Класс, отвечающий за то, в каком виде используются разные вспомогательные объекты.*/
public class Format {

    private static String dateFormat = "MM/dd/yyyy HH:mm:ss";

    /** Принимает строчку. Возвращает объект типа Date, который ей соответствует.*/
    public static @NotNull Date readDate(@NotNull String date) throws MyExceptions.UnknownProblem {
        DateFormat df = new SimpleDateFormat(dateFormat);
        try {
            return df.parse(date);
        } catch (ParseException e) {
            throw new MyExceptions.UnknownProblem();
        }
    }

    /** Принимает дату. Возвращает строчку, которая ей соответствует.*/
    public static @NotNull String writeDate(@NotNull Date date) {
        DateFormat df = new SimpleDateFormat(dateFormat);
        return df.format(date);
    }

    /** Возвращает хеш от переданного набора байт.*/
    public static @NotNull String getSHAFromByteArray(@NotNull byte[] content) {
        return DigestUtils.sha1Hex(content);
    }

}
