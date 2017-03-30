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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/** Вычисление check-суммы с использованием ForkJoinPool. */
public class ForkJoinCheckSum {

    /** Принимает путь до дириктории/файла, возвращает check-сумму.*/
    public static byte[] checkSum(Path path) throws FileNotFoundException {
        if (!Files.exists(path)) {
            throw new FileNotFoundException();
        }
        return new ForkJoinPool().invoke(new CheckSumTask(path));
    }

    private static class CheckSumTask extends RecursiveTask<byte[]> {

        private Path path;

        public CheckSumTask(Path path) {
            this.path = path;
        }

        @Override
        protected byte[] compute() {
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                if (Files.isDirectory(path)) {

                    ByteOutputStream bytes = new ByteOutputStream();
                    bytes.write(path.getFileName().toString().getBytes());

                    List<CheckSumTask> subTasks = new ArrayList<>();
                    for (File file: path.toFile().listFiles()) {
                        CheckSumTask task = new CheckSumTask(file.toPath());
                        task.fork();
                        subTasks.add(task);
                    }

                    for (CheckSumTask task: subTasks) {
                        bytes.write(task.join());
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

}
