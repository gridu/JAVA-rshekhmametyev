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
    private final StreamSplitter splitter;
    private final StreamMerger merger;

    public DefaultFileSorter(StreamSplitter splitter, StreamMerger merger) {
        this.splitter = splitter;
        this.merger = merger;
    }

    public DefaultFileSorter() {
        this.splitter = new DefaultStreamSplitter();
        this.merger = new DefaultStreamMerger();
    }

    @Override
    public Path sortFileByLines(String filePath, Comparator<String> comparator) throws IOException {
        Path path = Paths.get(filePath);

        if (!Files.exists(path)) {
            throw new FileNotFoundException();
        }

        int numOfLinesInChunk = 1000;

        Path chunksDir = this.splitter.splitFileIntoSortedChunks(Files.newInputStream(path),
                UUID.randomUUID().toString(),
                numOfLinesInChunk,
                comparator);

        File tempDir = new File(chunksDir.toUri());

        File[] files = tempDir.listFiles();

        if (files == null) {
            return null;
        }

        while (files.length > 1) {
            mergeChunks(files, chunksDir, comparator);
            files = tempDir.listFiles();
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

            this.merger.mergeStreams(file1, file2, Files.newOutputStream(resultFilePath), comparator);
            files[i].delete();
            files[i + 1].delete();
        }
    }
}
