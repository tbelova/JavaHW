package ru.spbau;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.ArrayList;
import java.util.List;

/**
 *  Rule для JUnit, позволяющий регистрировать потоки и проверяющий,
 *  что к концу теста они все завершились без исключений.
 */
public class Rule implements TestRule {
    private List<Thread> threadList = new ArrayList<>();
    private boolean isAnyException = false;

    /** Регистрирует поток.*/
    public void register(Thread thread) {
        thread.setUncaughtExceptionHandler((thread1, throwable) -> isAnyException = true);
        threadList.add(thread);
    }

    /**
     * Создает новый Statement из того, который передан, как параметр.
     * В нем добавлена проверка зарегистрированных потоков.
     */
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
