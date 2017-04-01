package ru.spbau;

import org.jetbrains.annotations.NotNull;

/** Класс, хранящий хеш коммита, его сообщение, автора и дату.*/
public class CommitWithMessage {

    private final String commit;
    private final String message;
    private final String author;
    private final String date;

    public CommitWithMessage(@NotNull String commit, @NotNull String message,
                             @NotNull String author, @NotNull String date) {
        this.commit = commit;
        this.message = message;
        this.author = author;
        this.date = date;
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
