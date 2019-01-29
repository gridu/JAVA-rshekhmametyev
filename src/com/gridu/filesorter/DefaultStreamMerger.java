package com.gridu.filesorter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

public class DefaultStreamMerger implements StreamMerger {
    @Override
    public void mergeStreams(InputStream stream1,
                             InputStream stream2,
                             String resultFilePath,
                             Comparator<String> comparator) throws IOException {
        Path filePath = Paths.get(resultFilePath);

        if (!Files.exists(filePath)) {
            throw new FileNotFoundException();
        }

        try (BufferedReader reader1 = new BufferedReader(new InputStreamReader(stream1));
             BufferedReader reader2 = new BufferedReader(new InputStreamReader(stream2));
             BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            mergeFromReaders(reader1, reader2, writer, comparator);
        }
    }

    private void mergeFromReaders(BufferedReader reader1,
                                  BufferedReader reader2,
                                  BufferedWriter writer,
                                  Comparator<String> comparator) throws IOException {
        while (true) {
            String lineFromFile1 = reader1.readLine();
            String lineFromFile2 = reader2.readLine();

            if (lineFromFile1 == null && lineFromFile2 == null) {
                break;
            }

            if (lineFromFile2 == null) {
                writeAllLinesFromReader(lineFromFile1, reader1, writer);
                break;
            }

            if (lineFromFile1 == null) {
                writeAllLinesFromReader(lineFromFile2, reader2, writer);
                break;
            }

            writeSorted(lineFromFile1,
                    lineFromFile2,
                    writer,
                    comparator);
        }
    }

    private void writeAllLinesFromReader(String alreadyReadLine,
                                         BufferedReader reader,
                                         BufferedWriter writer) throws IOException {
        writer.write(alreadyReadLine + "\n");
        String line;

        while ((line = reader.readLine()) != null) {
            writer.write(line + "\n");
        }
    }

    private void writeSorted(String line1,
                             String line2,
                             BufferedWriter writer,
                             Comparator<String> comparator) throws IOException {
        int comparisonResult = comparator.compare(line1, line2);

        if (comparisonResult <= 0) {
            writer.write(line1 + "\n");
            writer.write(line2 + "\n");
        } else {
            writer.write(line2 + "\n");
            writer.write(line1 + "\n");
        }
    }
}
