package ru.spbau;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/** Класс, отвечающий за обработку аргументов, переданных программе на вход.*/
public class CLI {

    private static String TOO_MANY_ARGUMENTS = "Too many arguments.";
    private static String TOO_FEW_ARGUMENTS = "Too few arguments.";

    /**
     * Принимает аргументы программы и путь до директории, откуда она запускается.
     * Если дана команда init, инициализирует репозиторий в этой директории и возвращает true.
     * Иначе возвращает false.
     */
    public static boolean tryInit(String[] args, Path path) {

        if (!args[0].equals("init")) {
            return false;
        }

        if (!checkNumberOfArguments(1, args.length)) {
            return true;
        }

        try {
            Repository.initRepository(path);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyExceptions.AlreadyExistsException e) {
            System.out.println("Repository already exists.");
        } catch (MyExceptions.UnknownProblem unknownProblem) {
            System.out.println(MyExceptions.UnknownProblem.defaultMessage);
        }

        return true;
    }

    /**
     * Принимает аргументы программы и путь до директории, откуда она запускается.
     * Если дана команда remove, удаляет папку с репозиторием и возвращает true.
     * Иначе возвращает false.
     */
    public static boolean tryRemove(String[] args, Path path) {

        if (!args[0].equals("remove")) {
            return false;
        }

        if (!checkNumberOfArguments(1, args.length)) {
            return true;
        }

        try {
            Repository.removeRepository(path);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyExceptions.IsNotDirectoryException e) {
            System.out.println(path + " is not a directory.");
        } catch (MyExceptions.NotFoundException e) {
            System.out.println("Repository not found.");
        }

        return true;
    }

    /**
     * Принимает путь до директории, откуда запускается программа.
     * Возвращает репозиторий, лежащий в этой директории.
     */
    public static Repository getRepository(Path path) {

        Repository repository = null;

        try {
            repository = Repository.getRepository(path);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyExceptions.NotFoundException e) {
            System.out.println("Can't load repository.");
        } catch (MyExceptions.UnknownProblem e) {
            System.out.println(MyExceptions.UnknownProblem.defaultMessage);
        }

        return repository;
    }

    /**
     * Принимает аргументы программы и путь до директории, откуда она запускается.
     * Если дана команда branch без аргументов, выводит название текущей ветки и возвращает true.
     * Если дана команда branch с одним аргументом, создает новую ветку с таким названием и возвращает true.
     * Иначе возвращает false.
     */
    public static boolean tryBranch(String[] args, Repository repository) {

        if (!args[0].equals("branch")) {
            return false;
        }

        if (args.length == 1) {

            try {
                System.out.println(repository.getCurrentBranch());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (MyExceptions.UnknownProblem e) {
                System.out.println(MyExceptions.UnknownProblem.defaultMessage);
            }

            return true;
        }

        if (args.length > 2) {
            System.out.println(TOO_MANY_ARGUMENTS);
            return true;
        }

        try {
            repository.branch(args[1]);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyExceptions.UnknownProblem e) {
            System.out.println(MyExceptions.UnknownProblem.defaultMessage);
        } catch (MyExceptions.AlreadyExistsException e) {
            System.out.println("This branch already exists.");
        } catch (MyExceptions.WrongFormatException e) {
            System.out.println("Wrong format. Maybe you use spaces in the branch name.");
        }

        return true;
    }

    /**
     * Принимает аргументы программы и путь до директории, откуда она запускается.
     * Если дана команда checkout, переключается на переданную ветку или коммит и возвращает true.
     * Иначе возвращает false.
     */
    public static boolean tryCheckout(String[] args, Repository repository) {

        if (!args[0].equals("checkout")) {
            return false;
        }

        if (!checkNumberOfArguments(2, args.length)) {
            return true;
        }

        try {
            repository.checkout(args[1]);
        } catch (MyExceptions.NotFoundException e) {
            System.out.println("No such branch or commit.");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyExceptions.UnknownProblem e) {
            System.out.println(MyExceptions.UnknownProblem.defaultMessage);
        }

        return true;
    }

    /**
     * Принимает аргументы программы и путь до директории, откуда она запускается.
     * Если дана команда branch_rm, удаляет ветку с переданным названием и возвращает true.
     * Иначе возвращает false.
     */
    public static boolean tryDeleteBranch(String[] args, Repository repository) {

        if (!args[0].equals("branch_rm")) {
            return false;
        }

        if (!checkNumberOfArguments(2, args.length)) {
            return true;
        }

        try {
            repository.removeBranch(args[1]);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyExceptions.UnknownProblem e) {
            System.out.println(MyExceptions.UnknownProblem.defaultMessage);
        }

        return true;
    }

    /**
     * Принимает аргументы программы и путь до директории, откуда она запускается.
     * Если дана команда add, добавляет переданный файл в репозиторий и возвращает true.
     * Иначе возвращает false.
     */
    public static boolean tryAdd(String[] args, Repository repository) {

        if (!args[0].equals("add")) {
            return false;
        }

        if (!checkNumberOfArguments(2, args.length)) {
            return true;
        }

        try {
            repository.add(Paths.get(args[1]));
        } catch (MyExceptions.UnknownProblem e) {
            System.out.println(MyExceptions.UnknownProblem.defaultMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }


    /**
     * Принимает аргументы программы и путь до директории, откуда она запускается.
     * Если дана команда commit, делает коммит с заданным сообщением и возвращает true.
     * Иначе возвращает false.
     */
    public static boolean tryCommit(String[] args, Repository repository) {

        if (!args[0].equals("commit")) {
            return false;
        }

        if (!checkNumberOfArguments(2, args.length)) {
            return true;
        }

        try {
            repository.commit(args[1]);
        } catch (MyExceptions.UnknownProblem e) {
            System.out.println(MyExceptions.UnknownProblem.defaultMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * Принимает аргументы программы и путь до директории, откуда она запускается.
     * Если дана команда log, выводит информацию о коммитах-предках текущего коммита и возвращает true.
     * Иначе возвращает false.
     */
    public static boolean tryLog(String[] args, Repository repository) {

        if (!args[0].equals("log")) {
            return false;
        }

        if (!checkNumberOfArguments(1, args.length)) {
            return true;
        }

        try {
            List<LogMessage> log = repository.log();
            for (LogMessage logMessage : log) {
                System.out.println(logMessage.getCommit() + "\n" + logMessage.getMessage() +
                        logMessage.getAuthor() + "\n" + logMessage.getDate() + "\n");
            }
        } catch (MyExceptions.UnknownProblem e) {
            System.out.println(MyExceptions.UnknownProblem.defaultMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * Принимает аргументы программы и путь до директории, откуда она запускается.
     * Если дана команда merge, сливает указанную ветку с текущей и возвращает true.
     * Иначе возвращает false.
     */
    public static boolean tryMerge(String[] args, Repository repository) {

        if (!args[0].equals("merge")) {
            return false;
        }

        if (!checkNumberOfArguments(2, args.length)) {
            return true;
        }

        try {
            repository.merge(args[1]);
        } catch (MyExceptions.NotFoundException e) {
            System.out.println("No such branch.");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyExceptions.UnknownProblem e) {
            System.out.println(MyExceptions.UnknownProblem.defaultMessage);
        }

        return true;
    }


    /**
     * Принимает аргументы программы и путь до директории, откуда она запускается.
     * Если дана команда status, выводит информацию обо всех файлах в репозитории и возвращает true.
     * Иначе возвращает false.
     */
    public static boolean tryStatus(String[] args, Repository repository) {

        if (!args[0].equals("status")) {
            return false;
        }

        if (!checkNumberOfArguments(1, args.length)) {
            return true;
        }

        try {
            List<File> files = repository.status();
            for (File file: files) {
                if (file.getType() == File.CHANGED) {
                    System.out.print("changed: ");
                } else if (file.getType() == File.DELETED) {
                    System.out.print("deleted: ");
                } else if (file.getType() == File.STAGED) {
                    System.out.print("staged: ");
                } else if (file.getType() == File.UNTRACKED) {
                    System.out.print("untracked: ");
                }
                System.out.println(file.getPath());
            }
        } catch (MyExceptions.UnknownProblem e) {
            System.out.println(MyExceptions.UnknownProblem.defaultMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * Принимает аргументы программы и путь до директории, откуда она запускается.
     * Если дана команда reset, сбрасывает состояние указанного файла и возвращает true.
     * Иначе возвращает false.
     */
    public static boolean tryReset(String[] args, Repository repository) {

        if (!args[0].equals("reset")) {
            return false;
        }

        if (!checkNumberOfArguments(2, args.length)) {
            return true;
        }

        try {
            repository.reset(Paths.get(args[1]));
        } catch (MyExceptions.UnknownProblem e) {
            System.out.println(MyExceptions.UnknownProblem.defaultMessage);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyExceptions.IsNotFileException e) {
            System.out.println(args[1] + " is not a file.");
        }

        return true;
    }

    /**
     * Принимает аргументы программы и путь до директории, откуда она запускается.
     * Если дана команда rm, удаляет файл как из репозитория, так и физически, и возвращает true.
     * Иначе возвращает false.
     */
    public static boolean tryRm(String[] args, Repository repository) {

        if (!args[0].equals("rm")) {
            return false;
        }

        if (!checkNumberOfArguments(2, args.length)) {
            return true;
        }

        try {
            repository.rm(Paths.get(args[1]));
        } catch (MyExceptions.UnknownProblem e) {
            System.out.println(MyExceptions.UnknownProblem.defaultMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * Принимает аргументы программы и путь до директории, откуда она запускается.
     * Если дана команда clean, удаляет все файлы, не добавленные в репозиторий, и возвращает true.
     * Иначе возвращает false.
     */
    public static boolean tryClean(String[] args, Repository repository) {

        if (!args[0].equals("clean")) {
            return false;
        }

        if (!checkNumberOfArguments(1, args.length)) {
            return true;
        }

        try {
            repository.clean();
        } catch (MyExceptions.UnknownProblem e) {
            System.out.println(MyExceptions.UnknownProblem.defaultMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    private static boolean checkNumberOfArguments(int expected, int found) {

        if (found < expected) {
            System.out.println(TOO_FEW_ARGUMENTS);
            return false;
        }

        if (found > expected) {
            System.out.println(TOO_MANY_ARGUMENTS);
            return false;
        }

        return true;
    }


}
