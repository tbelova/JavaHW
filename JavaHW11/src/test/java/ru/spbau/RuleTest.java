package ru.spbau;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class RuleTest {

    @Test
    public void applyTest() throws Throwable {
        Rule rule = new Rule();

        Statement statement = new Statement() {
            @Override
            public void evaluate() throws Throwable {
                Thread thread1 = new Thread(() -> System.out.println("first!"));
                Thread thread2 = new Thread(() -> System.out.println("second!"));
                thread1.start();
                thread2.start();
                thread1.join();
                thread2.join();
            }
        };

        Statement newStatement = rule.apply(statement, null);
        newStatement.evaluate();
    }

    @Test(expected = RuleException.class)
    public void applyTestThrowsAnException() throws Throwable {
        Rule rule = new Rule();

        Statement statement = new Statement() {
            @Override
            public void evaluate() throws Throwable {
                Thread thread1 = new Thread(() -> System.out.println("first!"));
                Thread thread2 = new Thread(() -> {System.out.println("second!"); throw new RuntimeException(); });
                rule.register(thread1);
                rule.register(thread2);
                thread1.start();
                thread2.start();
                thread1.join();
                thread2.join();
            }
        };

        Statement newStatement = rule.apply(statement, null);
        newStatement.evaluate();
    }

    @Test(expected = RuleException.class)
    public void applyTestDoesNotFinish() throws Throwable {
        Rule rule = new Rule();

        Statement statement = new Statement() {
            @Override
            public void evaluate() throws Throwable {
                Thread thread1 = new Thread(() -> System.out.println("first!"));
                Thread thread2 = new Thread(() -> {System.out.println("second!"); while (true) {} });
                rule.register(thread1);
                rule.register(thread2);
                thread1.start();
                thread2.start();
                thread1.join();
            }
        };

        Statement newStatement = rule.apply(statement, null);
        newStatement.evaluate();
    }

}