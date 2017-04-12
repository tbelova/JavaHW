package ru.spbau;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/** Класс, отвечающий за работу с командной строкой.*/
public class Main {

    private static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        logger.debug("main is called");

        if (args.length == 0) {
            logger.error("no arguments found.");
            System.out.println("Some arguments?");
            return;
        }

        Path currentPath = Paths.get(System.getProperty("user.dir"));

        boolean knownCommand = false;

        knownCommand |= CLI.tryInit(args, currentPath);
        knownCommand |= CLI.tryRemove(args, currentPath);

        if (!knownCommand) {

            Repository repository = CLI.getRepository(currentPath);

            knownCommand |= CLI.tryBranch(args, repository);
            knownCommand |= CLI.tryCheckout(args, repository);
            knownCommand |= CLI.tryDeleteBranch(args, repository);
            knownCommand |= CLI.tryAdd(args, repository);
            knownCommand |= CLI.tryCommit(args, repository);
            knownCommand |= CLI.tryLog(args, repository);
            knownCommand |= CLI.tryMerge(args, repository);
            knownCommand |= CLI.tryStatus(args, repository);
            knownCommand |= CLI.tryReset(args, repository);
            knownCommand |= CLI.tryRm(args, repository);
            knownCommand |= CLI.tryClean(args, repository);

        }

        if (!knownCommand) {
            logger.error("no such command found");
            System.out.println("Unknown command.");
        }

    }

}
