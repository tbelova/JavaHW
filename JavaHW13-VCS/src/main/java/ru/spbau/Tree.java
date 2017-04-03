package ru.spbau;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Tree extends VCSObject {

    private String name;

    private List<Tree> trees;
    private List<Blob> blobs;

    public static @NotNull Tree read(@NotNull String name, @NotNull Path path, @NotNull Repository repository)
            throws MyExceptions.IsNotFileException,
            IOException, MyExceptions.UnknownProblem {
        if (!Files.isRegularFile(path)) {
            throw new MyExceptions.IsNotFileException();
        }
        return new Tree(name, path, repository);
    }

    public Tree(@NotNull String name, @NotNull Path path, @NotNull Repository repository) throws IOException,
            MyExceptions.UnknownProblem, MyExceptions.IsNotFileException {
        this.repository = repository;

        content = Format.readByteContent(path);
        trees = new ArrayList<>();
        blobs = new ArrayList<>();
        this.name = name;
        updateSHA();

        List<String> lines = Format.readLines(path);
        for (String s : lines) {
            String[] stringList = s.split(" ");
            if (stringList.length != 3) {

                throw new MyExceptions.UnknownProblem();
            }
            if (stringList[0].equals(VCSObject.BLOB)) {
                blobs.add(new Blob(stringList[1], path.getParent().resolve(stringList[2]), repository));
            } else if (stringList[0].equals(VCSObject.TREE)) {
                trees.add(read(stringList[1], path.getParent().resolve(stringList[2]), repository));
            } else {
                throw new MyExceptions.UnknownProblem();
            }
        }
    }

    public @NotNull String getName() {
        return name;
    }

    public @NotNull Tree add(@NotNull Path path, @NotNull String hash)
            throws IOException, MyExceptions.UnknownProblem {
        if (path.getNameCount() == 0) {
            throw new MyExceptions.UnknownProblem();
        }
        if (path.getNameCount() == 1) {
            List<Blob> blobList = new ArrayList<>(blobs);
            List<Tree> treeList = new ArrayList<>(trees);
            blobList.add(repository.find(path.getName(0).toString(), hash));
            return new Tree(name, treeList, blobList, repository);
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
                treeList.add(new Tree(path.getName(0).toString(), repository)
                        .add(path.subpath(1, path.getNameCount()), hash));
            }

            return new Tree(name, treeList, blobList, repository);
        }

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

    private Tree(@NotNull String name, @NotNull List<Tree> trees, @NotNull List<Blob> blobs, @NotNull Repository repository)
            throws IOException {
        this.name = name;
        this.trees = trees;
        this.blobs = blobs;
        this.repository = repository;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        for (Tree tree: trees) {
            outputStream.write((VCSObject.TREE + " " + tree.getName() + " " + tree.getSHA() + "\n").getBytes());
        }
        for (Blob blob: blobs) {
            outputStream.write((VCSObject.BLOB + " " + blob.getName() + " " + blob.getSHA() + "\n").getBytes());
        }
        content = outputStream.toByteArray();
        updateSHA();
        write();
    }

    public Tree(@NotNull String name, @NotNull Repository repository) throws IOException {
        this.name = name;
        this.repository = repository;
        content = new byte[0];
        trees = new ArrayList<>();
        blobs = new ArrayList<>();
        updateSHA();
        write();
    }

    @Override
    public @NotNull String getType() {
        return VCSObject.TREE;
    }

    private void updateSHA() {
        sha = Format.getSHAFromByteArray(content);
    }

}


