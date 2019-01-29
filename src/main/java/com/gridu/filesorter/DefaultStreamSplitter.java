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
    public Path splitFileIntoSortedChunks(InputStream stream,
                                          String tempDirName,
                                          int numOfLinesInChunk,
                                          Comparator<String> comparator) throws IOException {
        Path tempDirPath = Files.createTempDirectory(tempDirName);

        List<String> lines = new ArrayList<>(numOfLinesInChunk);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);

                if (lines.size() == numOfLinesInChunk) {
                    writeChunkToFile(tempDirPath, lines, comparator);
                    lines.clear();
                }
            }
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
