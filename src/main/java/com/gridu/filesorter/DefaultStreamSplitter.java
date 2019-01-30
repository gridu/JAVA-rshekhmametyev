package com.gridu.filesorter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

class DefaultStreamSplitter implements StreamSplitter {
    @Override
    public Path splitStreamIntoSortedChunks(InputStream stream,
                                            int numOfLinesInChunk,
                                            Comparator<String> comparator) throws IOException {
        if (stream == null) {
            throw new IllegalArgumentException("Input stream cannot be null");
        }

        if (comparator == null) {
            throw new IllegalArgumentException("Comparator cannot be null");
        }

        if (numOfLinesInChunk <= 0) {
            throw new IllegalArgumentException("Number of lines in a chunk should be a positive number");
        }

        Path tempDirPath = Files.createTempDirectory(UUID.randomUUID().toString());

        List<String> lines = new ArrayList<>(numOfLinesInChunk);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);

                if (lines.size() == numOfLinesInChunk) {
                    this.writeChunkToFile(tempDirPath, lines, comparator);
                    lines.clear();
                }
            }
        }

        if (!lines.isEmpty()) {
            this.writeChunkToFile(tempDirPath, lines, comparator);
        }

        return tempDirPath;
    }

    private void writeChunkToFile(Path tempDirPath,
                                  List<String> lines,
                                  Comparator<String> comparator) throws IOException {
        lines.sort(comparator);

        Files.write(tempDirPath.resolve(Paths.get(UUID.randomUUID().toString())), lines);
    }
}
