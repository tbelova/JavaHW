package ru.spbau;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/** Класс, который отвечает за работу с репозиторием.*/
public class Repository {

    VCSFolders folders;

    private List<Branch> branches = new ArrayList<>();

    private Head head;
    private Index index;

    private static Logger logger = LoggerFactory.getLogger(Repository.class);

    /**
     * Принимает путь до папки и создает в ней новый репозиторий.
     * Бросает исключение, если такой папки не существует или в ней уже создан репозиторий.
     */
    static public @NotNull Repository initRepository(@NotNull Path path) throws IOException,
            MyExceptions.AlreadyExistsException, MyExceptions.UnknownProblem {

        logger.debug("initRepository is called with path {}", path);

        try {
            FileSystemWorker.createRepository(new VCSFolders(path));
        } catch (MyExceptions.IsNotDirectoryException e) {
            logger.error("can't find such directory");
            throw new MyExceptions.UnknownProblem();
        }

        Repository repository = new Repository(path);
        logger.debug("build repository");
        try {
            repository.initialCommit();
            logger.debug("create initial commit");
        } catch (MyExceptions.IsNotFileException e) {
            logger.error("fail to create initial commit");
            throw new MyExceptions.UnknownProblem();
        }

        return repository;

    }

    /**
     * Принимает путь до папки с репозиторием и удаляет ее.
     * Бросает исключение, если такой папки не существует или в ней не был создан репозиторий.
     * */
    public static void removeRepository(@NotNull Path path) throws IOException,
            MyExceptions.IsNotDirectoryException, MyExceptions.NotFoundException {

        logger.debug("removeRepository is called with path {}", path);

        FileSystemWorker.deleteRepository(path);

    }

    /**
     * Принимает папку с репозиторием и возвращает по ней репозиторий.
     * Бросает исключение, если в папке репозиторий не содержится или содержится в некорректном виде.
     */
    public static @NotNull Repository getRepository(@NotNull Path path)
            throws IOException, MyExceptions.NotFoundException, MyExceptions.UnknownProblem {

        logger.debug("getRepository is called with path {}", path);

        VCSFolders folders = new VCSFolders(path);

        if (!FileSystemWorker.exists(folders)) {
            logger.debug("cat'n find all repository folders");
            throw new MyExceptions.NotFoundException();
        }

        return new Repository(path);
    }

    /** Принимает путь до файла, который был добавлен или удален, и изменяет информацию о нем в файле index.*/
    public void add(@NotNull Path path) throws IOException, MyExceptions.UnknownProblem {

        logger.debug("add is called with path {}", path);

        path = folders.repositoryPath.resolve(path);

        boolean shouldAdd =  FileSystemWorker.exists(path);
        boolean found = false;

        if (shouldAdd) {
            logger.info("should add file");
        } else {
            logger.info("should remove file");
        }

        List<String> lines = FileSystemWorker.readLines(folders.realIndexFile);
        String content = "";
        String sha = null;

        if (shouldAdd) {
            sha = Format.getSHAFromByteArray(FileSystemWorker.readByteContent(path));
        }

        for (String line: lines) {
            String[] strings = line.split(" ");
            if (strings.length != 2) {
                throw new MyExceptions.UnknownProblem();
            }
            if (strings[0].equals(path.toString())) {
                if (!shouldAdd) continue;
                strings[1] = sha;
                found = true;
            }
            content += strings[0] + " " + strings[1] + "\n";
        }

        if (shouldAdd && !found) {
            content += path + " " + sha;
        }

        FileSystemWorker.writeTo(folders.realIndexFile, content.getBytes());

        if (shouldAdd) {
            FileSystemWorker.writeTo(folders.realObjectsFolder.resolve(sha), FileSystemWorker.readByteContent(path));
        }
    }

    /** Делает commit с заданным сообщением.*/
    public void commit(@NotNull String message) throws IOException, MyExceptions.UnknownProblem {

        logger.debug("commit is called with message {}", message);

        try {
            Tree root = getTreeForCommit(index.getPathsWithSHA());
            ArrayList<Commit> parents = new ArrayList<>();
            parents.add(head.getCommitAnyway());
            Commit commit = new Commit(message, root, parents);
            if (head.getType().equals(VCSObject.BRANCH)) {
                head.getBranch().setCommit(commit);
            } else {
                writeToHEAD(commit);
            }
        } catch (MyExceptions.IsNotFileException e) {
            throw new MyExceptions.UnknownProblem();
        }

    }

    /**
     * Принимает название ветки/коммита и переключается на нее/него.
     * Бросает иключение, если ни ветки, ни коммита с таким названием не найдено.
     * */
    public void checkout(@NotNull String branchName)
            throws MyExceptions.NotFoundException, IOException, MyExceptions.UnknownProblem {

        logger.debug("checkout is called with branch name {}", branchName);

        Branch branchFound = findBranch(branchName);
        if (branchFound == null) {
            checkoutOnCommit(branchName);
        } else {
            writeToHEAD(branchFound);
        }
    }

    /**
     * Принимает строчку и создает ветку с таким названием.
     * Бросает исключение, если ветка с таким названием уже существует.
     */
    public void branch(@NotNull String newBranchName) throws MyExceptions.AlreadyExistsException,
            IOException, MyExceptions.UnknownProblem, MyExceptions.WrongFormatException {

        logger.debug("branch is called with new branch name {}", newBranchName);

        if (newBranchName.contains(" ")) {
            logger.error("branch contains spaces");
            throw new MyExceptions.WrongFormatException();
        }

        Branch branch = findBranch(newBranchName);

        if (branch != null) {
            logger.error("branch already exists");
            throw new MyExceptions.AlreadyExistsException();
        }

        branches.add(new Branch(newBranchName, head.getCommitAnyway()));

    }

    /** Создает ветку с указанным названием и сразу переключается на нее.*/
    public void createBranchAndCheckout(@NotNull String branchName) throws MyExceptions.UnknownProblem,
            IOException, MyExceptions.AlreadyExistsException, MyExceptions.NotFoundException,
            MyExceptions.WrongFormatException {

        logger.debug("createBranchAndCheckout is called with new branch name {}", branchName);

        branch(branchName);
        checkout(branchName);
    }

    /** Удаляет ветку с указанным названием.*/
    public void removeBranch(@NotNull String name) throws IOException, MyExceptions.UnknownProblem {

        logger.debug("removeBranch is called with branch name {}", name);

        Branch branch = findBranch(name);
        if (branch != null) {
            if (!head.getType().equals(VCSObject.BRANCH) || head.getBranch() != branch) {
                branches.remove(branch);
                FileSystemWorker.delete(folders.realBranchesFolder.resolve(name));
            }
        }
    }

    /** Возвращает название текущей ветки.*/
    public @NotNull String getCurrentBranch() throws IOException, MyExceptions.UnknownProblem {

        logger.debug("getCurrentBranch is called");

        return head.getBranch().getName();
    }

    /** Возвращает отсортированный до дате создания список коммитов-предков текущего коммита.*/
    public @NotNull List<LogMessage> log() throws IOException, MyExceptions.UnknownProblem {

        logger.debug("log is called");

        Commit rootCommit = head.getCommitAnyway();
        List<Commit> commits = rootCommit.log();
        commits = commits.stream().distinct().collect(Collectors.toList());
        Collections.sort(commits, Commit::compareTo);
        return LogMessage.logMessagesFromCommits(commits);
    }

    /**
     * Принимает название ветки и сливает ее с текущей.
     * Бросает исключение, если указанной ветки не существует.
     * */
    public void merge(@NotNull String branchName) throws MyExceptions.NotFoundException,
            IOException, MyExceptions.UnknownProblem {

        logger.debug("merge is called with branch name {}", branchName);

        Branch curBranch = head.getBranch();
        Branch branch = findBranch(branchName);
        if (branch == null) {
            logger.error("no such branch found");
            throw new MyExceptions.NotFoundException();
        }

        List<Commit> parents = new ArrayList<>();
        parents.add(curBranch.getCommit());
        parents.add(branch.getCommit());

        List<PathWithSHA> pathsWithSHA = curBranch.getCommit().getTree().
                constructOriginalPaths(folders.repositoryPath);
        List<PathWithSHA> pathsWithSHAOther = branch.getCommit().getTree().
                constructOriginalPaths(folders.repositoryPath);

        pathsWithSHA.addAll(pathsWithSHAOther);

        Tree root;
        try {
            root = getTreeForCommit(pathsWithSHA);
        } catch (MyExceptions.IsNotFileException e) {
            throw new MyExceptions.UnknownProblem();
        }

        Commit commit = new Commit("Merge " + branchName + " into " + curBranch.getName() + ".",
                root, parents);
        curBranch.setCommit(commit);

        updateIndex(commit);
    }

    /** Возвращает список объектов типа File, соответствующих файлам, хранящихся в репозитории.*/
    public List<File> status() throws IOException, MyExceptions.UnknownProblem {

        logger.debug("status is called");

        return index.getAllFiles();
    }

    /** Сбрасывает состояние переданного файла.*/
    public void reset(@NotNull Path path) throws IOException, MyExceptions.UnknownProblem, MyExceptions.IsNotFileException {

        logger.debug("reset is called with path {}", path);

        path = folders.repositoryPath.resolve(path);
        String sha = index.getSHA(path);

        if (sha == null) {
            FileSystemWorker.delete(path);
        } else {
            addFileToUserDirectory(path, sha);
        }

    }

    /** Удаляет переданный файл как из репозитория, так и физически.*/
    public void rm(@NotNull Path path) throws IOException, MyExceptions.UnknownProblem {

        logger.debug("rm is called with path {}", path);

        FileSystemWorker.delete(folders.repositoryPath.resolve(path));
        add(path);

    }

    /** Удаляет все файлы, не добавленные в репозиторий.*/
    public void clean() throws IOException, MyExceptions.UnknownProblem {

        logger.debug("clean is called");

        List<File> untrackedFiles = index.getUntrackedFiles();
        for (File file: untrackedFiles) {
            FileSystemWorker.delete(file.getPath());
        }

    }

    private Repository(@NotNull Path path) throws IOException, MyExceptions.UnknownProblem {

        logger.debug("Repository constructor is called with path {}", path);

        folders = new VCSFolders(path);
        head = new Head(this);
        index = new Index(this);
        readAllBranches();

    }

    private void checkoutOnCommit(@NotNull String name)
            throws IOException, MyExceptions.NotFoundException, MyExceptions.UnknownProblem {

        logger.debug("checkoutOnCommit is called with commit name {}", name);

        Commit commit = findCommit(name);

        if (commit == null) {
            throw new MyExceptions.NotFoundException();
        }

        writeToHEAD(commit);

    }

    private @NotNull Tree getTreeForCommit(@NotNull List<PathWithSHA> lines)
            throws IOException, MyExceptions.UnknownProblem, MyExceptions.IsNotFileException {

        logger.debug("getTreeForCommit is called");

        Tree root = new Tree("root", this);

        for (PathWithSHA line: lines) {
            root = root.add(folders.repositoryPath.relativize(line.getPath()), line.getSHA());
        }

        return root;

    }

    private void initialCommit() throws IOException, MyExceptions.IsNotFileException, MyExceptions.UnknownProblem {

        logger.debug("initialCommit is called");

        Branch branch = new Branch("master", new Commit("Initial commit",
                new Tree("root", this), new ArrayList<>()));

        branches.add(branch);

        writeToHEAD(branch);

    }

    private void writeToHEAD(@NotNull Branch branch) throws IOException, MyExceptions.UnknownProblem {

        logger.debug("writeToHEAD is called");

        head.write(branch);
        updateIndex(branch.getCommit());

    }

    private void writeToHEAD(@NotNull Commit commit) throws IOException, MyExceptions.UnknownProblem {

        logger.debug("writeToCommit is called");

        head.write(commit);
        updateIndex(commit);

    }

    private void updateIndex(@NotNull Commit commit) throws IOException, MyExceptions.UnknownProblem {

        logger.debug("updateIndex is called");

        index.update(commit);
        updateUserDirectory(commit);

    }

    private void updateUserDirectory(@NotNull Commit commit) throws IOException, MyExceptions.UnknownProblem {

        logger.debug("updateUserDirectory is called");

        List<PathWithSHA> pathsWithSHA = commit.getPathWithSHAList();
        List<Path> paths = FileSystemWorker.walk(folders.repositoryPath);

        for (Path path: paths) {
            if (!path.equals(folders.repositoryPath) && !path.startsWith(folders.realVcsFolder)) {
                FileSystemWorker.delete(path);
            }
        }

        for (PathWithSHA pathWithSHA: pathsWithSHA) {
            addFileToUserDirectory(pathWithSHA.getPath(), pathWithSHA.getSHA());
        }

    }

    private void addFileToUserDirectory(@NotNull Path path, @NotNull String sha)
            throws IOException, MyExceptions.UnknownProblem {

        logger.debug("addFileToUserDirectory is called with path {} and hash {}", path, sha);

        FileSystemWorker.createFile(path);
        FileSystemWorker.writeTo(path, findBlob(path.getFileName().toString(), sha).getContent());

    }

    private void readAllBranches() throws IOException, MyExceptions.UnknownProblem {

        logger.debug("readAllBranches is called");

        List<Path> paths = FileSystemWorker.walk(folders.realBranchesFolder);

        for (Path path: paths) {
            if (FileSystemWorker.isFile(path)) {
                try {
                    Branch branch = new Branch(path, this);
                    branches.add(branch);
                } catch (MyExceptions.UnknownProblem | MyExceptions.IsNotFileException e) {
                    throw new MyExceptions.UnknownProblem();
                }
            }
        }

    }

    private @Nullable Commit findCommit(@NotNull String hash)
            throws IOException, MyExceptions.UnknownProblem {

        logger.debug("findCommit is called with hash {}", hash);

        List<Path> paths = FileSystemWorker.walk(folders.realObjectsFolder);

        for (Path path: paths) {
            if (path.getFileName().toString().equals(hash)) {
                try {
                    return new Commit(path, this);
                } catch (MyExceptions.IsNotFileException e) {
                    throw new MyExceptions.UnknownProblem();
                }
            }
        }

        return null;

    }

    @Nullable Blob findBlob(@NotNull String name, @NotNull String hash)
            throws IOException, MyExceptions.UnknownProblem {

        logger.debug("findBlob is called with name {} and hash {}", name, hash);

        List<Path> paths = FileSystemWorker.walk(folders.realObjectsFolder);

        for (Path path: paths) {
            if (path.getFileName().toString().equals(hash)) {
                try {
                    return new Blob(name, path, this);
                } catch (MyExceptions.IsNotFileException e) {
                    throw new MyExceptions.UnknownProblem();
                }
            }
        }

        return null;

    }

    @Nullable Branch findBranch(@NotNull String name) {

        logger.debug("findBranch is called with name {}", name);

        Branch branchFound = null;
        for (Branch branch: branches) {
            if (branch.getName().equals(name)) {
                branchFound = branch;
            }
        }

        return branchFound;

    }


}
