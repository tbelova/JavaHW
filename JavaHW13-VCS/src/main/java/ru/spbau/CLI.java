package ru.spbau;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class CLI {

    public static boolean tryInit(String[] args, Path path) {

        if (!args[0].equals("init")) {
            return false;
        }

        if (args.length > 1) {
            System.out.println("Too many arguments.");
            return true;
        }

        try {
            Repository.initRepository(path);
        } catch (IOException | MyExceptions.IsNotDirectoryException e) {
            e.printStackTrace();
        } catch (MyExceptions.AlreadyExistsException e) {
            System.out.println("Repository already exists.");
        } catch (MyExceptions.UnknownProblem unknownProblem) {
            unknownProblem.printStackTrace();
        } catch (MyExceptions.IsNotFileException e) {
            e.printStackTrace();
        }

        return true;
    }

    public static boolean tryRemove(String[] args, Path path) {

        if (!args[0].equals("remove")) {
            return false;
        }

        if (args.length > 1) {
            System.out.println("Too many arguments.");
            return true;
        }
        try {
            Repository.removeRepository(path);
        } catch (IOException | MyExceptions.NotFoundException | MyExceptions.IsNotDirectoryException e) {
            e.printStackTrace();
        }

        return true;
    }

    public static Repository getRepository(Path path) {

        Repository repository;

        try {
            repository = Repository.getRepository(path);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (MyExceptions.NotFoundException e) {
            System.out.println("Can't load repository.");
            return null;
        } catch (MyExceptions.UnknownProblem unknownProblem) {
            unknownProblem.printStackTrace();
            return null;
        }

        return repository;
    }

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
                System.out.println("Something went wrong.");
            }
            return true;
        }

        if (args.length > 2) {
            System.out.println("Too many arguments.");
            return true;
        }

        try {
            repository.branch(args[1]);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyExceptions.UnknownProblem e) {
            System.out.println("Something went wrong.");
        } catch (MyExceptions.AlreadyExistsException e) {
            System.out.println("This branch already exists.");
        } catch (MyExceptions.WrongFormatException e) {
            System.out.println("Wrong format. Maybe you use spaces in the branch name.");
        } catch (MyExceptions.IsNotFileException e) {
            e.printStackTrace();
        }

        return true;
    }

    public static boolean tryCheckout(String[] args, Repository repository) {

        if (!args[0].equals("checkout")) {
            return false;
        }

        if (args.length == 1) {
            System.out.println("Too few arguments.");
            return true;
        }

        if (args.length > 2) {
            System.out.println("Too many arguments.");
            return true;
        }

        try {
            repository.checkout(args[1]);
        } catch (MyExceptions.NotFoundException e) {
            System.out.println("No such branch or commit.");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyExceptions.IsNotFileException e) {
            e.printStackTrace();
        } catch (MyExceptions.UnknownProblem unknownProblem) {
            unknownProblem.printStackTrace();
        }

        return true;
    }

    public static boolean tryDeleteBranch(String[] args, Repository repository) {

        if (!args[0].equals("branch_rm")) {
            return false;
        }

        if (args.length == 1) {
            System.out.println("Too few arguments.");
            return true;
        }

        if (args.length > 2) {
            System.out.println("Too many arguments.");
            return true;
        }

        try {
            repository.removeBranch(args[1]);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyExceptions.UnknownProblem e) {
            System.out.println("Something went wrong.");
        }

        return true;
    }

    public static boolean tryAdd(String[] args, Repository repository) {

        if (!args[0].equals("add")) {
            return false;
        }

        if (args.length == 1) {
            System.out.println("Too few arguments.");
            return true;
        }

        if (args.length > 2) {
            System.out.println("Too many arguments.");
            return true;
        }

        try {
            repository.add(Paths.get(args[1]));
        } catch (MyExceptions.UnknownProblem e) {
            System.out.println("Something went wrong.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }


    public static boolean tryCommit(String[] args, Repository repository) {

        if (!args[0].equals("commit")) {
            return false;
        }

        if (args.length == 1) {
            System.out.println("Too few arguments.");
            return true;
        }

        if (args.length > 2) {
            System.out.println("Too many arguments.");
            return true;
        }

        try {
            repository.commit(args[1]);
        } catch (MyExceptions.UnknownProblem e) {
            System.out.println("Something went wrong.");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyExceptions.IsNotFileException e) {
            e.printStackTrace();
        }

        return true;
    }

    public static boolean tryLog(String[] args, Repository repository) {

        if (!args[0].equals("log")) {
            return false;
        }

        if (args.length > 1) {
            System.out.println("Too many arguments.");
            return true;
        }

        try {
            List<LogMessage> log = repository.log();
            for (LogMessage logMessage : log) {
                System.out.println(logMessage.getCommit() + "\n" + logMessage.getMessage() +
                        logMessage.getAuthor() + "\n" + logMessage.getDate() + "\n");
            }
        } catch (MyExceptions.UnknownProblem e) {
            System.out.println("Something went wrong.");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyExceptions.IsNotFileException e) {
            e.printStackTrace();
        }

        return true;
    }

    public static boolean tryMerge(String[] args, Repository repository) {

        if (!args[0].equals("merge")) {
            return false;
        }

        if (args.length != 2) {
            System.out.println("Wrong number of arguments.");
            return true;
        }

        try {
            repository.merge(args[1]);
        } catch (MyExceptions.NotFoundException e) {
            System.out.println("No such branch.");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyExceptions.UnknownProblem e) {
            System.out.println("Something went wrong.");
        } catch (MyExceptions.IsNotFileException e) {
            e.printStackTrace();
        }

        return true;
    }


    public static boolean tryStatus(String[] args, Repository repository) {

        if (!args[0].equals("status")) {
            return false;
        }

        if (args.length != 1) {
            System.out.println("Wrong number of arguments.");
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
        } catch (MyExceptions.UnknownProblem unknownProblem) {
            unknownProblem.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    public static boolean tryReset(String[] args, Repository repository) {

        if (!args[0].equals("reset")) {
            return false;
        }

        if (args.length != 2) {
            System.out.println("Wrong number of arguments.");
            return true;
        }

        try {
            repository.reset(Paths.get(args[1]));
        } catch (MyExceptions.UnknownProblem unknownProblem) {
            unknownProblem.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyExceptions.IsNotFileException e) {
            e.printStackTrace();
        }

        return true;
    }

    public static boolean tryRm(String[] args, Repository repository) {

        if (!args[0].equals("rm")) {
            return false;
        }

        if (args.length != 2) {
            System.out.println("Wrong number of arguments.");
            return true;
        }

        try {
            repository.rm(Paths.get(args[1]));
        } catch (MyExceptions.UnknownProblem unknownProblem) {
            unknownProblem.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    public static boolean tryClean(String[] args, Repository repository) {

        if (!args[0].equals("clean")) {
            return false;
        }

        if (args.length != 1) {
            System.out.println("Wrong number of arguments.");
            return true;
        }

        try {
            repository.clean();
        } catch (MyExceptions.UnknownProblem unknownProblem) {
            unknownProblem.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }





}
