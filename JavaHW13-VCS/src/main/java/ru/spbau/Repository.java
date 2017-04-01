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

/** Класс, который отвечает за работу с репозиторием.*/
public class Repository {

    private static final Path vcsFolder = Paths.get(".vcsFolder");
    private static final Path HEADFile = vcsFolder.resolve("HEADFile");
    private static final Path indexFile = vcsFolder.resolve("indexFile");
    private static final Path objectsFolder = vcsFolder.resolve("objectsFolder");
    private static final Path refsFolder = vcsFolder.resolve("refsFolder");
    private static final Path branchesFolder = refsFolder.resolve("HEADS");

    private Path repositoryPath;
    private Path realVcs;
    private Path realHEAD;
    private Path realIndex;
    private Path realObjects;
    private Path realRefs;
    private Path realBranches;

    private List<Branch> branches = new ArrayList<>();

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
    public void add(@NotNull Path path) throws IOException, MyExceptions.WrongFormatException {
        path = repositoryPath.resolve(path);
        boolean shouldAdd =  Files.exists(path);
        boolean found = false;
        List<String> lines = Files.readAllLines(realIndex);
        String content = "";
        String sha = null;
        if (shouldAdd) {
            sha = Format.getSHAFromByteArray(Files.readAllBytes(path));
        }
        for (String line: lines) {
            String[] strings = line.split(" ");
            if (strings.length != 2) {
                throw new MyExceptions.WrongFormatException();
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

        Format.writeTo(realIndex, content.getBytes());

        if (shouldAdd) {
            Format.writeTo(realObjects.resolve(sha), Files.readAllBytes(path));
        }
    }

    /** Делает commit с заданным сообщением.*/
    public void commit(@NotNull String message) throws IOException, MyExceptions.WrongFormatException {
        List<String> lines = Files.readAllLines(realIndex);
        List<PathWithSHA> pathsWithSHA = new ArrayList<>();
        for (String line: lines) {
            String[] strings = line.split(" ");
            if (strings.length != 2) {
                throw new MyExceptions.WrongFormatException();
            }
            pathsWithSHA.add(new PathWithSHA(Paths.get(strings[0]), strings[1]));
        }
        Tree root = getTreeForCommit(pathsWithSHA);
        ArrayList<Commit> parents = new ArrayList<>();
        parents.add(getHEADCommit());
        Commit commit = new Commit(message, root, parents, realObjects);
        if (getTypeOfHEAD().equals(VCSObject.BRANCH)) {
            getHeadBranch().setCommit(commit);
        } else {
            writeToHEAD(commit);
        }
    }

    /**
     * Принимает название ветки/коммита и переключается на нее/него.
     * Бросает иключение, если ни ветки, ни коммита с таким названием не найдено.
     * */
    public void checkout(@NotNull String branchName) throws MyExceptions.NotFoundException, IOException {
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
            IOException, MyExceptions.WrongFormatException {
        if (newBranchName.contains(" ")) {
            throw new MyExceptions.WrongFormatException();
        }
        Branch branch = findBranch(newBranchName);
        if (branch != null) {
            throw new MyExceptions.AlreadyExistsException();
        }
        branches.add(new Branch(newBranchName, getHEADCommit(), realBranches));
    }

    /** Создает ветку с указанным названием и сразу переключается на нее.*/
    public void createBranchAndCheckout(@NotNull String branchName) throws MyExceptions.WrongFormatException,
            IOException, MyExceptions.AlreadyExistsException, MyExceptions.NotFoundException {
        branch(branchName);
        checkout(branchName);
    }

    /** Удаляет ветку с указанным названием.*/
    public void removeBranch(@NotNull String name) throws IOException, MyExceptions.WrongFormatException {
        Branch branch = findBranch(name);
        if (branch != null) {
            if (!getTypeOfHEAD().equals(VCSObject.BRANCH) || getHeadBranch() != branch) {
                branches.remove(branch);
                Files.deleteIfExists(realBranches.resolve(name));
            }
        }
    }

    /** Возвращает название текущей ветки.*/
    public @NotNull String getCurrentBranch() throws IOException, MyExceptions.WrongFormatException {
        return getHeadBranch().getName();
    }

    /** Возвращает отсортированный до дате создания список коммитов-предков текущего коммита.*/
    public @NotNull List<CommitWithMessage> log() throws IOException, MyExceptions.WrongFormatException {
        Commit rootCommit = getHEADCommit();
        List<Commit> commits = rootCommit.log();
        commits = commits.stream().distinct().collect(Collectors.toList());
        Collections.sort(commits, Commit::compareTo);
        return commitsWithMessagesFromCommits(commits);
    }

    /**
     * Принимает название ветки и сливает ее с текущей.
     * Бросает исключение, если указанной ветки не существует.
     * */
    public void merge(@NotNull String branchName) throws MyExceptions.NotFoundException,
            IOException, MyExceptions.WrongFormatException {
        Branch curBranch = getHeadBranch();
        Branch branch = findBranch(branchName);
        if (branch == null) {
            throw new MyExceptions.NotFoundException();
        }

        List<Commit> parents = new ArrayList<>();
        parents.add(curBranch.getCommit());
        parents.add(branch.getCommit());

        List<PathWithSHA> pathsWithSHA = curBranch.getCommit().getTree().constructOriginalPaths(repositoryPath);
        List<PathWithSHA> pathsWithSHAOther = branch.getCommit().getTree().constructOriginalPaths(repositoryPath);

        pathsWithSHA.addAll(pathsWithSHAOther);

        Tree root = getTreeForCommit(pathsWithSHA);

        Commit commit = new Commit("Merge " + branchName + " into " + curBranch.getName() + ".",
                root, parents, realObjects);
        curBranch.setCommit(commit);

        updateIndex(commit);
    }

    private Repository(@NotNull Path path) throws IOException {
        repositoryPath = path;
        realVcs = path.resolve(vcsFolder);
        realHEAD = path.resolve(HEADFile);
        realIndex = path.resolve(indexFile);
        realObjects = path.resolve(objectsFolder);
        realRefs = path.resolve(refsFolder);
        realBranches = path.resolve(branchesFolder);
        readAllBranches();
    }

    private void checkoutOnCommit(@NotNull String name) throws IOException, MyExceptions.NotFoundException {
        Commit commit = findCommit(name);
        if (commit == null) {
            throw new MyExceptions.NotFoundException();
        }
        writeToHEAD(commit);
    }

    private @NotNull Tree getTreeForCommit(@NotNull List<PathWithSHA> lines) throws IOException, MyExceptions.WrongFormatException {
        Tree root = getHEADTree();
        for (PathWithSHA line: lines) {
            root = root.add(repositoryPath.relativize(line.getPath()), line.getSHA());
        }

        return root;
    }

    private @NotNull Commit getHeadCommit() throws IOException, MyExceptions.WrongFormatException {
        List<String> lines = Files.readAllLines(realHEAD);
        if (lines.size() != 1) {
            throw new MyExceptions.WrongFormatException();
        }
        String[] stringList = lines.get(0).split(" ");
        if (stringList.length != 2) {
            throw new MyExceptions.WrongFormatException();
        }
        if (!stringList[0].equals(VCSObject.COMMIT)) {
            throw new MyExceptions.WrongFormatException();
        }
        Commit commit = Commit.read(realObjects.resolve(stringList[1]), realObjects);
        if (commit == null) {
            throw new MyExceptions.WrongFormatException();
        }
        return commit;
    }

    private @NotNull Branch getHeadBranch() throws IOException, MyExceptions.WrongFormatException {
        List<String> lines = Files.readAllLines(realHEAD);
        if (lines.size() != 1) {
            throw new MyExceptions.WrongFormatException();
        }
        String[] stringList = lines.get(0).split(" ");
        if (stringList.length != 2) {
            throw new MyExceptions.WrongFormatException();
        }
        if (!stringList[0].equals(VCSObject.BRANCH)) {
            throw new MyExceptions.WrongFormatException();
        }
        Branch branch = findBranch(stringList[1]);
        if (branch == null) {
            throw new MyExceptions.WrongFormatException();
        }
        return branch;
    }

    private @NotNull String getTypeOfHEAD() throws IOException, MyExceptions.WrongFormatException {
        List<String> lines = Files.readAllLines(realHEAD);
        if (lines.size() != 1) {
            throw new MyExceptions.WrongFormatException();
        }
        String[] stringList = lines.get(0).split(" ");
        if (stringList.length != 2) {
            throw new MyExceptions.WrongFormatException();
        }
        return stringList[0];
    }

    private @NotNull Commit getHEADCommit() throws IOException, MyExceptions.WrongFormatException {
        if (getTypeOfHEAD().equals(VCSObject.BRANCH)) {
            return getHeadBranch().getCommit();
        } else {
            return getHeadCommit();
        }
    }

    private @NotNull Tree getHEADTree() throws IOException, MyExceptions.WrongFormatException {
        return getHEADCommit().getTree();
    }

    private @Nullable Branch findBranch(@NotNull String name) {
        Branch branchFound = null;
        for (Branch branch: branches) {
            if (branch.getName().equals(name)) {
                branchFound = branch;
            }
        }
        return branchFound;
    }

    private @Nullable Commit findCommit(@NotNull String name) throws IOException {
        Stream<Path> pathStream = Files.walk(realObjects);
        return pathStream.reduce(null, (Commit commit, Path path) -> {
            if (path.getFileName().toString().equals(name)) {
                return Commit.read(path, realObjects);
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

    private void initialCommit() throws IOException {
        Branch branch = new Branch("master", new Commit("Initial commit",
                new Tree("root", realObjects), new ArrayList<>(), realObjects), realBranches);
        branches.add(branch);
        writeToHEAD(branch);
    }

    private void writeToHEAD(@NotNull Branch branch) throws IOException {
        Format.writeTo(realHEAD, (VCSObject.BRANCH + " " + branch.getName()));
        updateIndex(branch.getCommit());
    }

    private void writeToHEAD(@NotNull Commit commit) throws IOException {
        Format.writeTo(realHEAD, (VCSObject.COMMIT + " " + commit.getSHA()));
        updateIndex(commit);
    }

    private void updateIndex(@NotNull Commit commit) throws IOException {
        List<PathWithSHA> pathsWithSHA = commit.getTree().constructOriginalPaths(repositoryPath);
        String content = "";
        for (PathWithSHA line: pathsWithSHA) {
            content += (line.getPath() + " " + line.getSHA() + "\n");
        }
        Format.writeTo(realIndex, content);

        updateUserDirectory(pathsWithSHA);
    }

    private void updateUserDirectory(@NotNull List<PathWithSHA> pathsWithSHA) throws IOException {
        Stream<Path> pathStream = Files.walk(repositoryPath);
        pathStream.forEach(path -> {
            if (!path.equals(repositoryPath) && !path.startsWith(realVcs)) {
                if (Files.isDirectory(path)) {
                    try {
                        FileUtils.deleteDirectory(path.toFile());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException e) {
                        e.printStackTrace();
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
        Stream<Path> pathStream = Files.walk(repositoryPath);
        pathStream.forEach(vcsobject -> {
            if (vcsobject.getFileName().toString().equals(sha)) {
                try {
                    Format.writeTo(path, Files.readAllBytes(vcsobject));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void readAllBranches() throws IOException {
        Stream<Path> pathStream = Files.walk(realBranches);
        pathStream.forEach(this::readBranch);
    }

    private void readBranch(@NotNull Path path) {
        try {
            List<String> lines = Files.readAllLines(path);
            Commit commit = Commit.read(realObjects.resolve(lines.get(0)), realObjects);
            if (commit != null) {
                branches.add(new Branch(path.getFileName().toString(),
                        commit, realBranches));
            }
        } catch (Exception e) {}
    }

    private List<CommitWithMessage> commitsWithMessagesFromCommits(List<Commit> commits) {
        List<CommitWithMessage> commitsWithMessages = new ArrayList<>();
        for (Commit commit: commits) {
            commitsWithMessages.add(new CommitWithMessage(commit.getSHA(), commit.getMessage(),
                    commit.getAuthor(), Format.writeDate(commit.getDate())));
        }
        return commitsWithMessages;
    }


}
