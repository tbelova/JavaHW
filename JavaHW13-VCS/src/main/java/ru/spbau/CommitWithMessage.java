package ru.spbau;

import java.util.ArrayList;
import java.util.List;

public class CommitWithMessage {
    private String commit;
    private String message;

    public static List<CommitWithMessage> commitsWithMessagesFromCommits(List<Repository.Commit> commits) {
        List<CommitWithMessage> commitsWithMessages = new ArrayList<>();
        for (Repository.Commit commit: commits) {
            commitsWithMessages.add(new CommitWithMessage(commit));
        }
        return commitsWithMessages;
    }

    public CommitWithMessage(Repository.Commit commit) {
        this.commit = commit.getSHA();
        this.message = commit.getMessage();
    }

    public String getCommit() {
        return commit;
    }

    public String getMessage() {
        return message;
    }
}
