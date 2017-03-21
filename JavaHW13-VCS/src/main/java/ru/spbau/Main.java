package ru.spbau;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("Some arguments?");
            return;
        }

        Path currentPath = Paths.get(System.getProperty("user.dir"));

        if (args[0].equals("init")) {
            if (args.length > 1) {
                System.out.println("Too many arguments.");
                return;
            }
            try {
                Repository.initRepository(currentPath);
            } catch (IOException | MyExceptions.IsNotDirectoryException e) {
                e.printStackTrace();
            } catch (MyExceptions.AlreadyExistsException e) {
                System.out.println("Repository already exists.");
            }
            return;
        }

        if (args[0].equals("remove")) {
            if (args.length > 1) {
                System.out.println("Too many arguments.");
                return;
            }
            try {
                Repository.removeRepository(Paths.get(System.getProperty("user.dir")));
            } catch (IOException | MyExceptions.NotFoundException | MyExceptions.IsNotDirectoryException e) {
                e.printStackTrace();
            }
        }

        Repository repository;
        try {
            repository = Repository.getRepository(currentPath);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } catch (MyExceptions.NotFoundException e) {
            System.out.println("Can't load repository.");
            return;
        }

        if (args[0].equals("branch")) {
            if (args.length == 1) {
                try {
                    System.out.println(repository.getCurrentBranch());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (MyExceptions.WrongFormatException e) {
                    System.out.println("Something went wrong.");
                }
                return;
            }
            if (args.length > 2) {
                System.out.println("Too many arguments.");
                return;
            }
            try {
                repository.branch(args[1]);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (MyExceptions.WrongFormatException e) {
                System.out.println("Something went wrong.");
            } catch (MyExceptions.AlreadyExistsException e) {
                System.out.println("This branch already exists.");
            }

            return;
        }

        if (args[0].equals("checkout")) {
            if (args.length == 1) {
                System.out.println("Too few arguments.");
                return;
            }
            if (args.length > 2) {
                System.out.println("Too many arguments.");
                return;
            }

            try {
                repository.checkout(args[1]);
            } catch (MyExceptions.NotFoundException e) {
                System.out.println("No such branch or commit.");
            } catch (IOException e) {
                e.printStackTrace();
            }

            return;
        }

        if (args[0].equals("branch_rm")) {
            if (args.length == 1) {
                System.out.println("Too few arguments.");
                return;
            }
            if (args.length > 2) {
                System.out.println("Too many arguments.");
                return;
            }

            try {
                repository.removeBranch(args[1]);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (MyExceptions.WrongFormatException e) {
                System.out.println("Something went wrong.");
            }

            return;
        }

        if (args[0].equals("add")) {
            if (args.length == 1) {
                System.out.println("Too few arguments.");
                return;
            }
            if (args.length > 2) {
                System.out.println("Too many arguments.");
                return;
            }

            try {
                repository.add(Paths.get(args[1]));
            } catch (MyExceptions.WrongFormatException e) {
                System.out.println("Something went wrong.");
            } catch (IOException e) {
                e.printStackTrace();
            }

            return;
        }

        if (args[0].equals("commit")) {
            if (args.length == 1) {
                System.out.println("Too few arguments.");
                return;
            }
            if (args.length > 2) {
                System.out.println("Too many arguments.");
                return;
            }

            try {
                repository.commit(args[1]);
            } catch (MyExceptions.WrongFormatException e) {
                System.out.println("Something went wrong.");
            } catch (IOException e) {
                e.printStackTrace();
            }

            return;
        }

        if (args[0].equals("log")) {
            if (args.length > 1) {
                System.out.println("Too many arguments.");
                return;
            }

            try {
                repository.log();
            } catch (MyExceptions.WrongFormatException e) {
                System.out.println("Something went wrong.");
            } catch (IOException e) {
                e.printStackTrace();
            }

            return;
        }

        System.out.println("Unknown command.");

    }

}
