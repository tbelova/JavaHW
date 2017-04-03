package ru.spbau;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/** Класс, хранящий хеш коммита, его сообщение, автора и дату.*/
public class LogMessage {

    private final String commit;
    private final String message;
    private final String author;
    private final String date;

    public LogMessage(@NotNull String commit, @NotNull String message,
                      @NotNull String author, @NotNull String date) {
        this.commit = commit;
        this.message = message;
        this.author = author;
        this.date = date;
    }

    public static List<LogMessage> logMessagesFromCommits(List<Commit> commits) {
        List<LogMessage> logMessages = new ArrayList<>();
        for (Commit commit: commits) {
            logMessages.add(new LogMessage(commit.getSHA(), commit.getMessage(),
                    commit.getAuthor(), Format.writeDate(commit.getDate())));
        }
        return logMessages;
    }

    /** Возвращает хеш коммита.*/
    public @NotNull String getCommit() {
        return commit;
    }

    /** Возвращает сообщение коммита.*/
    public @NotNull String getMessage() {
        return message;
    }

    /** Возвращает автора коммита.*/
    public @NotNull String getAuthor() {
        return author;
    }

    /** Возвращает дату коммита.*/
    public @NotNull String getDate() {
        return date;
    }

}
