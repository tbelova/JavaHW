package ru.spbau;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.ArrayList;
import java.util.List;

public class Rule implements TestRule {
    private List<Thread> threadList = new ArrayList<>();
    private boolean isAnyException = false;

    public void register(Thread thread) {
        thread.setUncaughtExceptionHandler((thread1, throwable) -> isAnyException = true);
        threadList.add(thread);
    }

    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                base.evaluate();
                if (isAnyException) {
                    throw new RuleException();
                }
                for (Thread thread: threadList) {
                    if (thread.isAlive()) {
                        throw new RuleException();
                    }
                }
            }
        };
    }

}
