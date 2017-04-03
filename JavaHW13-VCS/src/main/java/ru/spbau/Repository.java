package ru.spbau;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.spbau.VCSFolders.*;

/** Класс, который отвечает за работу с репозиторием.*/
public class Repository {

    VCSFolders folders;

    List<Branch> branches = new ArrayList<>();

    private Head head;

    /**
     * Принимает путь до папки и создает в ней новый репозиторий.
     * Бросает исключение, если такой папки не существует или в ней уже создан репозиторий.
     */
    static public @NotNull Repository initRepository(@NotNull Path path) throws IOException,
            MyExceptions.IsNotDirectoryException, MyExceptions.AlreadyExistsException {
        if (!Files.isDirectory(path)) {
            throw new MyExceptions.IsNotDirectoryException();
        }

        if (Files.exists(path.resolve(vcsFolder))) {
            throw new MyExceptions.AlreadyExistsException();
        }

        Files.createDirectory(path.resolve(vcsFolder));
        Files.createFile(path.resolve(HEADFile));
        Files.createFile(path.resolve(indexFile));
        Files.createDirectory(path.resolve(objectsFolder));
        Files.createDirectory(path.resolve(refsFolder));
        Files.createDirectory(path.resolve(branchesFolder));

        Repository repository = new Repository(path);
        repository.initialCommit();
        return repository;
    }

    /**
     * Принимает путь до папки с репозиторием и удаляет ее.
     * Бросает исключение, если такой папки не существует или в ней не был создан репозиторий.
     * */
    public static void removeRepository(@NotNull Path path) throws IOException,
            MyExceptions.IsNotDirectoryException, MyExceptions.NotFoundException {
        if (!Files.isDirectory(path)) {
            throw new MyExceptions.IsNotDirectoryException();
        }
        if (!Files.exists(path.resolve(vcsFolder))) {
            throw new MyExceptions.NotFoundException();
        }
        FileUtils.deleteDirectory(path.toFile());
    }

    /**
     * Принимает папку с репозиторием и возвращает по ней репозиторий.
     * Бросает исключение, если в папке репозиторий не содержится или содержится в некорректном виде.
     */
    public static @NotNull Repository getRepository(@NotNull Path path) throws IOException, MyExceptions.NotFoundException {
        Path realVcs = path.resolve(vcsFolder);
        Path realHEAD = path.resolve(HEADFile);
        Path realIndex = path.resolve(indexFile);
        Path realObjects = path.resolve(objectsFolder);
        Path realRefs = path.resolve(refsFolder);
        Path realBranches = path.resolve(branchesFolder);

        if (!Files.isDirectory(path) || Files.notExists(realVcs)
                || !Files.isDirectory(realVcs)
                || Files.notExists(realHEAD)
                || !Files.isRegularFile(realHEAD)
                || Files.notExists(realIndex)
                || !Files.isRegularFile(realIndex)
                || Files.notExists(realObjects)
                || !Files.isDirectory(realObjects)
                || Files.notExists(realRefs)
                || !Files.isDirectory(realRefs)
                || !Files.isDirectory(realBranches)) {
            throw new MyExceptions.NotFoundException();
        }

        return new Repository(path);
    }

    /** Принимает путь до файла, который был добавлен или удален, и изменяет информацию о нем в файле index.*/
    public void add(@NotNull Path path) throws IOException, MyExceptions.UnknownProblem {
        path = folders.repositoryPath.resolve(path);
        boolean shouldAdd =  Files.exists(path);
        boolean found = false;
        List<String> lines = Format.readLines(folders.realIndexFile);
        String content = "";
        String sha = null;
        if (shouldAdd) {
            sha = Format.getSHAFromByteArray(Format.readByteContent(path));
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

        Format.writeTo(folders.realIndexFile, content.getBytes());

        if (shouldAdd) {
            Format.writeTo(folders.realObjectsFolder.resolve(sha), Format.readByteContent(path));
        }
    }

    /** Делает commit с заданным сообщением.*/
    public void commit(@NotNull String message) throws IOException, MyExceptions.UnknownProblem, MyExceptions.IsNotFileException {
        List<String> lines = Format.readLines(folders.realIndexFile);
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
    public void checkout(@NotNull String branchName) throws MyExceptions.NotFoundException, IOException {
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
                Files.deleteIfExists(folders.realBranchesFolder.resolve(name));
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

    private Repository(@NotNull Path path) throws IOException {
        folders = new VCSFolders(path);
        head = new Head(this);
        readAllBranches();
    }

    private void checkoutOnCommit(@NotNull String name) throws IOException, MyExceptions.NotFoundException {
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
        List<PathWithSHA> pathsWithSHA = commit.getTree().constructOriginalPaths(folders.repositoryPath);
        String content = "";
        for (PathWithSHA line: pathsWithSHA) {
            content += (line.getPath() + " " + line.getSHA() + "\n");
        }
        Format.writeTo(folders.realIndexFile, content);

        updateUserDirectory(pathsWithSHA);
    }

    private void updateUserDirectory(@NotNull List<PathWithSHA> pathsWithSHA) throws IOException {
        Stream<Path> pathStream = Files.walk(folders.repositoryPath);
        pathStream.forEach(path -> {
            if (!path.equals(folders.repositoryPath) && !path.startsWith(folders.realVcsFolder)) {
                if (Files.isDirectory(path)) {
                    try {
                        FileUtils.deleteDirectory(path.toFile());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        for (PathWithSHA pathWithSHA: pathsWithSHA) {
            addFile(pathWithSHA.getPath(), pathWithSHA.getSHA());
        }

    }

    private void addFile(@NotNull Path path, @NotNull String sha) throws IOException {
        Files.createDirectories(path.getParent());
        Stream<Path> pathStream = Files.walk(folders.repositoryPath);
        pathStream.forEach(vcsobject -> {
            if (vcsobject.getFileName().toString().equals(sha)) {
                try {
                    Format.writeTo(path, Format.readByteContent(vcsobject));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void readAllBranches() throws IOException {
        Stream<Path> pathStream = Files.walk(folders.realBranchesFolder);
        pathStream.forEach(path -> {
            try {
                Branch branch = new Branch(path, this);
                branches.add(branch);
            } catch (Exception e) {}
        });
    }

    private @Nullable Commit findCommit(@NotNull String hash) throws IOException {
        Stream<Path> pathStream = Files.walk(folders.realObjectsFolder);
        return pathStream.reduce(null, (Commit commit, Path path) -> {
            if (path.getFileName().toString().equals(hash)) {
                try {
                    return new Commit(path, this);
                } catch (Exception e) {
                    return null;
                }
            } else {
                return commit;
            }
        }, (Commit commit1, Commit commit2) -> {
            if (commit1 == null) {
                return commit2;
            } else {
                return commit1;
            }
        });
    }

    public  @Nullable Blob find(@NotNull String name, @NotNull String hash)
            throws IOException {
        Stream<Path> pathStream = Files.walk(folders.realObjectsFolder);
        return pathStream.reduce(null, (Blob blob, Path path) -> {
            if (path.getFileName().toString().equals(hash)) {
                Blob resultBlob = null;
                try {
                    resultBlob = new Blob(name, path, this);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return resultBlob;
            } else {
                return blob;
            }
        }, (Blob blob1, Blob blob2) -> {
            if (blob1 == null) {
                return blob2;
            } else {
                return blob1;
            }
        });
    }


}
