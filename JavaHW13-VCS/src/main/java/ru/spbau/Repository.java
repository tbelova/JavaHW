package ru.spbau;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
        if (!path.startsWith(repositoryPath)) {
            throw new MyExceptions.WrongFormatException();
        }
        boolean shouldAdd =  Files.exists(path);
        boolean found = false;
        List<String> lines = Files.readAllLines(realIndex);
        String content = "";
        String sha = getSHAFromByteArray(Files.readAllBytes(path));
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

        OutputStream outputStream;
        outputStream = Files.newOutputStream(realIndex);
        outputStream.write(content.getBytes());
        outputStream.close();

        if (shouldAdd) {
            outputStream = Files.newOutputStream(realObjects.resolve(sha));
            outputStream.write(Files.readAllBytes(path));
            outputStream.close();
        }
    }

    /** Делает commit с заданным сообщением.*/
    public void commit(@NotNull String message) throws IOException, MyExceptions.WrongFormatException {
        List<String> lines = Files.readAllLines(realIndex);
        List<PathWithSHA> pathsWithSHALine = new ArrayList<>();
        for (String line: lines) {
            String[] strings = line.split(" ");
            if (strings.length != 2) {
                throw new MyExceptions.WrongFormatException();
            }
            pathsWithSHALine.add(new PathWithSHA(Paths.get(strings[0]), strings[1]));
        }
        Tree root = getTreeForCommit(pathsWithSHALine);
        ArrayList<Commit> parents = new ArrayList<>();
        parents.add(getHEADCommit());
        Commit commit = new Commit(message, root, parents);
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
        branches.add(new Branch(newBranchName, getHEADCommit()));
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

        Commit commit = new Commit("Merge " + branchName + " into " + curBranch.getName() + ".", root, parents);
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
        return readCommit(realObjects.resolve(stringList[1]));
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
            System.out.println(lines.get(0));
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
            if (branch.name.equals(name)) {
                branchFound = branch;
            }
        }
        return branchFound;
    }

    private @Nullable Commit findCommit(@NotNull String name) throws IOException {
        Stream<Path> pathStream = Files.walk(realObjects);
        return pathStream.reduce(null, (Commit commit, Path path) -> {
            if (path.getFileName().toString().equals(name)) {
                return readCommit(path);
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
        Branch branch = new Branch("master", new Commit("Initial commit", new Tree("root"), new ArrayList<>()));
        branches.add(branch);
        writeToHEAD(branch);
    }

    private void writeToHEAD(@NotNull Branch branch) throws IOException {
        OutputStream outputStream = Files.newOutputStream(realHEAD);
        outputStream.write((VCSObject.BRANCH + " ").getBytes());
        outputStream.write(branch.getName().getBytes());
        outputStream.close();

        updateIndex(branch.getCommit());
    }

    private void writeToHEAD(@NotNull Commit commit) throws IOException {
        OutputStream outputStream = Files.newOutputStream(realHEAD);
        outputStream.write((VCSObject.COMMIT + " ").getBytes());
        outputStream.write(commit.getSHA().getBytes());
        outputStream.close();

        updateIndex(commit);
    }

    private void updateIndex(@NotNull Commit commit) throws IOException {
        List<PathWithSHA> pathsWithSHA = commit.getTree().constructOriginalPaths(repositoryPath);
        OutputStream outputStream = Files.newOutputStream(realIndex);
        for (PathWithSHA line: pathsWithSHA) {
            outputStream.write((line.getPath() + " " + line.getSHA() + "\n").getBytes());
        }
        outputStream.close();

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
        OutputStream outputStream = Files.newOutputStream(path);
        Stream<Path> pathStream = Files.walk(repositoryPath);
        pathStream.forEach(vcsobject -> {
            if (vcsobject.getFileName().toString().equals(sha)) {
                try {
                    outputStream.write(Files.readAllBytes(vcsobject));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        outputStream.close();
    }

    private void readAllBranches() throws IOException {
        Stream<Path> pathStream = Files.walk(realBranches);
        pathStream.forEach(this::readBranch);
    }

    private void readBranch(Path path) {
        try {
            List<String> lines = Files.readAllLines(path);
            branches.add(new Branch(path.getFileName().toString(), readCommit(realObjects.resolve(lines.get(0)))));
        } catch (Exception e) {}
    }

    private class Branch {
        private String name;
        private Commit commit;

        public Branch(@NotNull String name, @NotNull Commit commit) throws IOException {
            this.name = name;
            this.commit = commit;
            write();
        }

        public @NotNull String getName() {
            return name;
        }

        public @NotNull Commit getCommit() {
            return commit;
        }

        public void setCommit(@NotNull Commit commit) throws IOException {
            this.commit = commit;
            write();
        }

        private void write() throws IOException {
            OutputStream outputStream = Files.newOutputStream(realBranches.resolve(name));
            outputStream.write(commit.getSHA().getBytes());
            outputStream.close();
        }
    }

    private List<CommitWithMessage> commitsWithMessagesFromCommits(List<Commit> commits) {
        List<CommitWithMessage> commitsWithMessages = new ArrayList<>();
        for (Repository.Commit commit: commits) {
            commitsWithMessages.add(new CommitWithMessage(commit.getSHA(), commit.getMessage()));
        }
        return commitsWithMessages;
    }

    private @Nullable Commit readCommit(@NotNull Path path) {
        try {
            List<String> lines = Files.readAllLines(path);
            List<Commit> parents = new ArrayList<>();
            for (int i = 4; i < lines.size(); i++) {
                parents.add(readCommit(realObjects.resolve(lines.get(i))));
            }
            return new Commit(lines.get(0),
                    readTree("root", realObjects.resolve(lines.get(1))),
                    readDate(lines.get(2)), lines.get(3), parents);
        } catch (Exception e) {
            return null;
        }
    }

    private @NotNull Date readDate(@NotNull String date) throws ParseException {
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        return df.parse(date);
    }

    private @NotNull String writeDate(@NotNull Date date) {
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        return df.format(date);
    }

    private class Commit extends VCSObject implements Comparable<Commit> {

        private Tree tree;
        private String sha;
        private byte[] content;
        private String message;
        private List<Commit> parentCommits;
        private Date date;
        private String author;

        public Commit(@NotNull String message, @NotNull Tree tree,
                      @NotNull Date date, @NotNull String author, @NotNull List<Commit> parents) throws IOException {
            this.message = message;
            this.tree = tree;
            this.date = date;
            this.author = author;
            parentCommits = new ArrayList<>(parents);
            String contentString = message + "\n" + tree.getSHA() + "\n" + writeDate(date) + "\n" + author;
            for (Commit parentCommit: parentCommits) {
                contentString += "\n" + parentCommit.getSHA();
            }
            content = contentString.getBytes();
            updateSHA();
            write();
        }

        public Commit(@NotNull String message, @NotNull Tree tree, @NotNull List<Commit> parents) throws IOException {
            this(message, tree, new Date(), System.getProperty("user.name"), parents);
        }

        public @NotNull List<Commit> log() {
            List<Commit> log = new ArrayList<>();
            log.add(this);
            for (Commit parent: parentCommits) {
                List<Commit> parentLog = parent.log();
                for (Commit commit: parentLog) {
                    log.add(commit);
                }
            }
            return log;
        }

        public @NotNull String getMessage() {
            return message;
        }

        public @NotNull Tree getTree() {
            return tree;
        }

        public @NotNull Date getDate() {
            return date;
        }

        @Override
        public @NotNull String getSHA() {
            return sha;
        }

        @Override
        public @NotNull String getType() {
            return VCSObject.COMMIT;
        }

        @Override
        public @NotNull byte[] getContent() {
            return content;
        }

        private void updateSHA() {
            sha = getSHAFromByteArray(content);
        }

        private void write() throws IOException {
            OutputStream outputStream = Files.newOutputStream(realObjects.resolve(sha));
            outputStream.write(content);
            outputStream.close();
        }

        @Override
        public int compareTo(@NotNull Commit commit) {
            return date.compareTo(commit.getDate());
        }
    }

    private @NotNull Tree readTree(@NotNull String name, @NotNull Path path) throws MyExceptions.IsNotFileException,
            IOException, MyExceptions.WrongFormatException {
        if (!Files.isRegularFile(path)) {
            throw new MyExceptions.IsNotFileException();
        }
        return new Tree(name, path);
    }

    private class Tree extends VCSObject {

        private String sha;
        private byte[] content;
        private String name;

        private List<Tree> trees;
        private List<Blob> blobs;

        public Tree(@NotNull String name, @NotNull Path path) throws IOException,
                MyExceptions.WrongFormatException, MyExceptions.IsNotFileException {
            content = Files.readAllBytes(path);
            trees = new ArrayList<>();
            blobs = new ArrayList<>();
            this.name = name;
            updateSHA();

            List<String> lines = Files.readAllLines(path);
            for (String s: lines) {
                String[] stringList = s.split(" ");
                if (stringList.length != 3) {
                    throw new MyExceptions.WrongFormatException();
                }
                if (stringList[0].equals(VCSObject.BLOB)) {
                    blobs.add(readBlob(stringList[2], path.getParent().resolve(stringList[1])));
                } else if (stringList[0].equals(VCSObject.TREE)) {
                    trees.add(readTree(stringList[2], path.getParent().resolve(stringList[1])));
                } else {
                    throw new MyExceptions.WrongFormatException();
                }
            }
        }

        public @NotNull String getName() {
            return name;
        }

        public @NotNull Tree add(@NotNull Path path, @NotNull String hash)
                throws IOException, MyExceptions.WrongFormatException {
            if (path.getNameCount() == 0) {
                throw new MyExceptions.WrongFormatException();
            }
            if (path.getNameCount() == 1) {
                List<Blob> blobList = new ArrayList<>(blobs);
                List<Tree> treeList = new ArrayList<>(trees);
                blobList.add(findBlob(path.getName(0).toString(), hash));
                return new Tree(name, treeList, blobList);
            } else {
                List<Tree> treeList = new ArrayList<>();
                List<Blob> blobList = new ArrayList<>(blobs);

                boolean found = false;
                for (Tree tree : trees) {
                    if (tree.getName().equals(path.getName(0).toString())) {
                        treeList.add(tree.add(path.subpath(1, path.getNameCount()), hash));
                        found = true;
                    } else {
                        treeList.add(tree);
                    }
                }
                if (!found) {
                    treeList.add(new Tree(path.getName(0).toString())
                            .add(path.subpath(1, path.getNameCount()), hash));
                }

                return new Tree(name, treeList, blobList);
            }

        }

        public  @Nullable Blob findBlob(@NotNull String name, @NotNull String hash) throws IOException {
            Stream<Path> pathStream = Files.walk(realObjects);
            return pathStream.reduce(null, (Blob blob, Path path) -> {
                if (path.getFileName().toString().equals(hash)) {
                    Blob resultBlob = null;
                    try {
                        resultBlob = readBlob(name, path);
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

        public @NotNull List<PathWithSHA> constructOriginalPaths(@NotNull Path path) {
            List<PathWithSHA> pathsWithSHA = new ArrayList<>();
            for (Blob blob: blobs) {
                pathsWithSHA.add(new PathWithSHA(path.resolve(blob.getName()), blob.getSHA()));
            }
            for (Tree tree: trees) {
                List<PathWithSHA> pathWithSHAChildren = tree.constructOriginalPaths(path.resolve(tree.getName()));
                for (PathWithSHA pathWithSHA: pathWithSHAChildren) {
                    pathsWithSHA.add(pathWithSHA);
                }
            }

            return pathsWithSHA;
        }

        private Tree(@NotNull String name, @NotNull List<Tree> trees, @NotNull List<Blob> blobs) throws IOException {
            this.name = name;
            this.trees = trees;
            this.blobs = blobs;
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            for (Tree tree: trees) {
                outputStream.write((VCSObject.TREE + " " + tree.getName() + " " + tree.getSHA() + "\n").getBytes());
            }
            content = outputStream.toByteArray();
            updateSHA();
            write();
        }

        private Tree(@NotNull String name) throws IOException {
            this.name = name;
            content = new byte[0];
            trees = new ArrayList<>();
            blobs = new ArrayList<>();
            updateSHA();
            write();
        }

        @Override
        public @NotNull String getSHA() {
            return sha;
        }

        @Override
        public @NotNull String getType() {
            return VCSObject.TREE;
        }

        @Override
        public @NotNull byte[] getContent() {
            return content;
        }

        private void updateSHA() {
            sha = getSHAFromByteArray(content);
        }

        private void write() throws IOException {
            OutputStream outputStream = Files.newOutputStream(realObjects.resolve(sha));
            outputStream.write(content);
            outputStream.close();
        }
    }

    private class PathWithSHA {
        private Path path;
        private String sha;

        public PathWithSHA(@NotNull Path path, @NotNull String sha) {
            this.path = path;
            this.sha = sha;
        }

        public @NotNull Path getPath() {
            return path;
        }

        public @NotNull String getSHA() {
            return sha;
        }

    }

    private  @NotNull Blob readBlob(@NotNull String name, @NotNull Path path)
            throws MyExceptions.IsNotFileException, IOException {
        if (!Files.isRegularFile(path)) {
            throw new MyExceptions.IsNotFileException();
        }

        return new Blob(name, Files.readAllBytes(path));
    }

    private class Blob extends VCSObject {

        private String sha;
        private byte[] content;
        private String name;

        public Blob(@NotNull String name, @NotNull byte[] content) throws IOException {
            this.content = content;
            this.name = name;
            updateSHA();
            write();
        }

        @Override
        public @NotNull String getSHA() {
            return sha;
        }

        @Override
        public @NotNull String getType() {
            return VCSObject.BLOB;
        }

        @Override
        public @NotNull byte[] getContent() {
            return content;
        }

        public @NotNull String getName() {
            return name;
        }

        private void updateSHA() {
            sha = DigestUtils.sha1Hex(content);
        }

        private void write() throws IOException {
            OutputStream outputStream = Files.newOutputStream(realObjects.resolve(sha));
            outputStream.write(content);
            outputStream.close();
        }
    }

    private abstract class VCSObject {
        
        public static final String BLOB = "blob";
        public static final String TREE = "tree";
        public static final String COMMIT = "commit";
        public static final String BRANCH = "branch";
        public static final String TAG = "tag";

        public abstract String getSHA();

        public abstract String getType();

        public abstract byte[] getContent();

    }

    private @NotNull String getSHAFromByteArray(@NotNull byte[] content) {
        return DigestUtils.sha1Hex(content);
    }


}
