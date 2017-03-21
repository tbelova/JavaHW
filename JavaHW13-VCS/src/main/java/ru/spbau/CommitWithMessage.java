package ru.spbau;

import org.jetbrains.annotations.NotNull;

/** Класс, хранящий пару из хеша коммита и его сообщения.*/
public class CommitWithMessage {

    private String commit;
    private String message;

    public CommitWithMessage(@NotNull String commit, @NotNull String message) {
        this.commit = commit;
        this.message = message;
    }

    public @NotNull String getCommit() {
        return commit;
    }

    public @NotNull String getMessage() {
        return message;
    }
}
