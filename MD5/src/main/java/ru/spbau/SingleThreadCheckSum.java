package ru.spbau;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/** Однопоточный вариант вычисления check-суммы.*/
public class SingleThreadCheckSum {

    /** Принимает путь до дириктории/файла, возвращает check-сумму.*/
    public static byte[] checkSum(Path path) throws FileNotFoundException {
        if (!Files.exists(path)) {
            throw new FileNotFoundException();
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            if (Files.isDirectory(path)) {
                ByteOutputStream bytes = new ByteOutputStream();
                bytes.write(path.getFileName().toString().getBytes());
                for (File file: path.toFile().listFiles()) {
                    bytes.write(checkSum(file.toPath()));
                }
                return md.digest(bytes.getBytes());
            } else {
                FileInputStream fileInputStream = new FileInputStream(path.toFile());
                DigestInputStream inputStream = new DigestInputStream(fileInputStream, md);
                while (inputStream.read() != -1);
                inputStream.close();
                fileInputStream.close();
                return md.digest();
            }
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException();
        }
    }


}
