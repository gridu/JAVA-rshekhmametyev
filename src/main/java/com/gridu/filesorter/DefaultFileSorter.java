package com.gridu.filesorter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.UUID;

public class DefaultFileSorter implements FileSorter {
    private final FileSplitter splitter;
    private final StreamMerger merger;

    public DefaultFileSorter(FileSplitter splitter, StreamMerger merger) {
        this.splitter = splitter;
        this.merger = merger;
    }

    public DefaultFileSorter() {
        this.splitter = new DefaultFileSplitter();
        this.merger = new DefaultStreamMerger();
    }

    @Override
    public Path sortFileByLines(String filePath, Comparator<String> comparator) throws IOException {
        Path path = Paths.get(filePath);

        if (!Files.exists(path)) {
            throw new FileNotFoundException();
        }

        int numOfLinesInChunk = 1000;

        String tempDirName = "chunks";

        Path chunksDir = this.splitter.splitFileIntoSortedChunks(filePath,
                tempDirName,
                numOfLinesInChunk,
                comparator);

        File tempDir = new File(chunksDir.toUri());

        File[] files;

        while ((files = tempDir.listFiles()).length > 1) {
            mergeChunks(files, chunksDir, comparator);
        }

        return files[0].toPath();
    }

    private void mergeChunks(File[] files, Path dirPath, Comparator<String> comparator) throws IOException {
        for (int i = 0; i < files.length; i += 2) {
            if (i + 1 == files.length) {
                break;
            }

            InputStream file1 = Files.newInputStream(files[i].toPath());
            InputStream file2 = Files.newInputStream(files[i + 1].toPath());
            Path resultFilePath = Files.createFile(dirPath.resolve(UUID.randomUUID().toString()));

            this.merger.mergeStreams(file1, file2, resultFilePath.toString(), comparator);
            files[i].delete();
            files[i + 1].delete();
        }
    }
}
