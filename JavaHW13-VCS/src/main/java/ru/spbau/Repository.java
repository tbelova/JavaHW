package ru.spbau;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


/** Класс, который отвечает за работу с репозиторием.*/
public class Repository {

    VCSFolders folders;

    List<Branch> branches = new ArrayList<>();

    private Head head;
    private Index index;

    /**
     * Принимает путь до папки и создает в ней новый репозиторий.
     * Бросает исключение, если такой папки не существует или в ней уже создан репозиторий.
     */
    static public @NotNull Repository initRepository(@NotNull Path path) throws IOException,
            MyExceptions.IsNotDirectoryException, MyExceptions.AlreadyExistsException, MyExceptions.UnknownProblem {

        FileSystemWorker.createRepository(new VCSFolders(path));

        Repository repository =
                new Repository(path);
        repository.initialCommit();
        return repository;
    }

    /**
     * Принимает путь до папки с репозиторием и удаляет ее.
     * Бросает исключение, если такой папки не существует или в ней не был создан репозиторий.
     * */
    public static void removeRepository(@NotNull Path path) throws IOException,
            MyExceptions.IsNotDirectoryException, MyExceptions.NotFoundException {
        FileSystemWorker.deleteRepository(path);
    }

    /**
     * Принимает папку с репозиторием и возвращает по ней репозиторий.
     * Бросает исключение, если в папке репозиторий не содержится или содержится в некорректном виде.
     */
    public static @NotNull Repository getRepository(@NotNull Path path)
            throws IOException, MyExceptions.NotFoundException, MyExceptions.UnknownProblem {
        VCSFolders folders = new VCSFolders(path);

        if (!FileSystemWorker.exists(folders)) {
            throw new MyExceptions.NotFoundException();
        }

        return new Repository(path);
    }

    /** Принимает путь до файла, который был добавлен или удален, и изменяет информацию о нем в файле index.*/
    public void add(@NotNull Path path) throws IOException, MyExceptions.UnknownProblem {
        path = folders.repositoryPath.resolve(path);
        boolean shouldAdd =  FileSystemWorker.exists(path);
        boolean found = false;
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
    public void commit(@NotNull String message) throws IOException, MyExceptions.UnknownProblem, MyExceptions.IsNotFileException {
        List<String> lines = FileSystemWorker.readLines(folders.realIndexFile);
        List<PathWithSHA> pathsWithSHA = new ArrayList<>();
        for (String line: lines) {
            String[] strings = line.split(" ");
            if (strings.length != 2) {
                throw new MyExceptions.UnknownProblem();
            }
            pathsWithSHA.add(new PathWithSHA(Paths.get(strings[0]), strings[1]));
        }
        Tree root = getTreeForCommit(pathsWithSHA);
        ArrayList<Commit> parents = new ArrayList<>();
        parents.add(head.getCommitAnyway());
        Commit commit = new Commit(message, root, parents, this);
        if (head.getType().equals(VCSObject.BRANCH)) {
            head.getBranch().setCommit(commit);
        } else {
            writeToHEAD(commit);
        }
    }

    /**
     * Принимает название ветки/коммита и переключается на нее/него.
     * Бросает иключение, если ни ветки, ни коммита с таким названием не найдено.
     * */
    public void checkout(@NotNull String branchName)
            throws MyExceptions.NotFoundException, IOException, MyExceptions.IsNotFileException, MyExceptions.UnknownProblem {
        Branch branchFound = Branch.find(branchName, branches);
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
            IOException, MyExceptions.UnknownProblem, MyExceptions.WrongFormatException,
            MyExceptions.IsNotFileException {
        if (newBranchName.contains(" ")) {
            throw new MyExceptions.WrongFormatException();
        }
        Branch branch = Branch.find(newBranchName, branches);
        if (branch != null) {
            throw new MyExceptions.AlreadyExistsException();
        }
        branches.add(new Branch(newBranchName, head.getCommitAnyway(), this));
    }

    /** Создает ветку с указанным названием и сразу переключается на нее.*/
    public void createBranchAndCheckout(@NotNull String branchName) throws MyExceptions.UnknownProblem,
            IOException, MyExceptions.AlreadyExistsException, MyExceptions.NotFoundException,
            MyExceptions.WrongFormatException, MyExceptions.IsNotFileException {
        branch(branchName);
        checkout(branchName);
    }

    /** Удаляет ветку с указанным названием.*/
    public void removeBranch(@NotNull String name) throws IOException, MyExceptions.UnknownProblem {
        Branch branch = Branch.find(name, branches);
        if (branch != null) {
            if (!head.getType().equals(VCSObject.BRANCH) || head.getBranch() != branch) {
                branches.remove(branch);
                FileSystemWorker.delete(folders.realBranchesFolder.resolve(name));
            }
        }
    }

    /** Возвращает название текущей ветки.*/
    public @NotNull String getCurrentBranch() throws IOException, MyExceptions.UnknownProblem {
        return head.getBranch().getName();
    }

    /** Возвращает отсортированный до дате создания список коммитов-предков текущего коммита.*/
    public @NotNull List<LogMessage> log() throws IOException, MyExceptions.UnknownProblem, MyExceptions.IsNotFileException {
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
            IOException, MyExceptions.UnknownProblem, MyExceptions.IsNotFileException {
        Branch curBranch = head.getBranch();
        Branch branch = Branch.find(branchName, branches);
        if (branch == null) {
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

        Tree root = getTreeForCommit(pathsWithSHA);

        Commit commit = new Commit("Merge " + branchName + " into " + curBranch.getName() + ".",
                root, parents, this);
        curBranch.setCommit(commit);

        updateIndex(commit);
    }

    private Repository(@NotNull Path path) throws IOException, MyExceptions.UnknownProblem {
        folders = new VCSFolders(path);
        head = new Head(this);
        index = new Index(this);
        readAllBranches();
    }

    private void checkoutOnCommit(@NotNull String name)
            throws IOException, MyExceptions.NotFoundException, MyExceptions.UnknownProblem, MyExceptions.IsNotFileException {
        Commit commit = findCommit(name);
        if (commit == null) {
            throw new MyExceptions.NotFoundException();
        }
        writeToHEAD(commit);
    }

    private @NotNull Tree getTreeForCommit(@NotNull List<PathWithSHA> lines) throws IOException, MyExceptions.UnknownProblem, MyExceptions.IsNotFileException {
        Tree root = head.getTree();
        for (PathWithSHA line: lines) {
            root = root.add(folders.repositoryPath.relativize(line.getPath()), line.getSHA());
        }

        return root;
    }

    private void initialCommit() throws IOException {
        Branch branch = new Branch("master", new Commit("Initial commit",
                new Tree("root", this), new ArrayList<>(), this), this);
        branches.add(branch);
        writeToHEAD(branch);
    }

    private void writeToHEAD(@NotNull Branch branch) throws IOException {
        head.write(branch);
        updateIndex(branch.getCommit());
    }

    private void writeToHEAD(@NotNull Commit commit) throws IOException {
        head.write(commit);
        updateIndex(commit);
    }

    private void updateIndex(@NotNull Commit commit) throws IOException {
        index.update(commit);
        updateUserDirectory(commit);
    }

    private void updateUserDirectory(@NotNull Commit commit) throws IOException {

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

    private void addFileToUserDirectory(@NotNull Path path, @NotNull String name) throws IOException {

        FileSystemWorker.createFile(path);
        List<Path> paths = FileSystemWorker.walk(folders.repositoryPath);

        for (Path vcsobject: paths) {
            if (vcsobject.getFileName().toString().equals(name)) {
                FileSystemWorker.writeTo(path, FileSystemWorker.readByteContent(vcsobject));
            }
        }

    }

    private void readAllBranches() throws IOException, MyExceptions.UnknownProblem {
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
            throws IOException, MyExceptions.IsNotFileException, MyExceptions.UnknownProblem {

        List<Path> paths = FileSystemWorker.walk(folders.realObjectsFolder);

        for (Path path: paths) {
            if (path.getFileName().toString().equals(hash)) {
                return new Commit(path, this);
            }
        }

        return null;
    }

    public  @Nullable Blob findBlob(@NotNull String name, @NotNull String hash)
            throws IOException, MyExceptions.IsNotFileException {

        List<Path> paths = FileSystemWorker.walk(folders.realObjectsFolder);

        for (Path path: paths) {
            if (path.getFileName().toString().equals(hash)) {
                return new Blob(name, path, this);
            }
        }

        return null;
    }


}
