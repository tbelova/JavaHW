package ru.spbau;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;

/** Класс, хранящий пути до всех папок и файлов, используемых для работы репозитория.*/
public class VCSFolders {

    public static final Path vcsFolder = Paths.get(".vcsFolder");
    public static final Path HEADFile = vcsFolder.resolve("HEADFile");
    public static final Path indexFile = vcsFolder.resolve("indexFile");
    public static final Path objectsFolder = vcsFolder.resolve("objectsFolder");
    public static final Path refsFolder = vcsFolder.resolve("refsFolder");
    public static final Path branchesFolder = refsFolder.resolve("HEADS");

    public final Path repositoryPath;
    public final Path realVcsFolder;
    public final Path realHEADFile;
    public final Path realIndexFile;
    public final Path realObjectsFolder;
    public final Path realRefsFolder;
    public final Path realBranchesFolder;

    /** Принимает путь до папки с репозиторием, конструирует VCSFolders.*/
    public @NotNull VCSFolders(@NotNull Path path) {
        repositoryPath = path;
        realVcsFolder = path.resolve(vcsFolder);
        realHEADFile = path.resolve(HEADFile);
        realIndexFile = path.resolve(indexFile);
        realObjectsFolder = path.resolve(objectsFolder);
        realRefsFolder = path.resolve(refsFolder);
        realBranchesFolder = path.resolve(branchesFolder);
    }

}
