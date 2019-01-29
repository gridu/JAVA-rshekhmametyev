package com.gridu.filesorter;

import java.io.*;
import java.util.Comparator;

class DefaultStreamMerger implements StreamMerger {
    @Override
    public void mergeStreams(InputStream stream1,
                             InputStream stream2,
                             OutputStream outputStream,
                             Comparator<String> comparator) throws IOException {
        if (stream1 == null) {
            throw new IllegalArgumentException("First stream cannot be null");
        }

        if (stream2 == null) {
            throw new IllegalArgumentException("Second stream cannot be null");
        }

        if (outputStream == null) {
            throw new IllegalArgumentException("Output stream cannot be null");
        }

        if (comparator == null) {
            throw new IllegalArgumentException("Comparator cannot be null");
        }

        try (BufferedReader reader1 = new BufferedReader(new InputStreamReader(stream1));
             BufferedReader reader2 = new BufferedReader(new InputStreamReader(stream2));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream))) {
            mergeFromReaders(reader1, reader2, writer, comparator);
        }
    }

    private void mergeFromReaders(BufferedReader reader1,
                                  BufferedReader reader2,
                                  BufferedWriter writer,
                                  Comparator<String> comparator) throws IOException {
        boolean isFirstWrite = true;
        String lineFromFile1 = reader1.readLine();
        String lineFromFile2 = reader2.readLine();

        if (lineFromFile1 == null && lineFromFile2 == null) {
            return;
        }

        if (lineFromFile1 == null) {
            writeAllLinesFromReader(lineFromFile2, true, reader2, writer);
            return;
        }

        if (lineFromFile2 == null) {
            writeAllLinesFromReader(lineFromFile1, true, reader1, writer);
            return;
        }

        while (true) {
            int comparisonResult = comparator.compare(lineFromFile1, lineFromFile2);

            if (!isFirstWrite) {
                writer.write("\n");
            }

            if (comparisonResult < 0) {
                writer.write(lineFromFile1);
                lineFromFile1 = reader1.readLine();
            } else if (comparisonResult == 0) {
                writer.write(lineFromFile1 + "\n" + lineFromFile2);
                lineFromFile1 = reader1.readLine();
                lineFromFile2 = reader2.readLine();
            } else {
                writer.write(lineFromFile2);
                lineFromFile2 = reader2.readLine();
            }

            isFirstWrite = false;

            if (lineFromFile1 == null && lineFromFile2 == null) {
                break;
            }

            if (lineFromFile1 == null) {
                writeAllLinesFromReader(lineFromFile2, false, reader2, writer);
                break;
            }

            if (lineFromFile2 == null) {
                writeAllLinesFromReader(lineFromFile1, false, reader1, writer);
                break;
            }
        }
    }

    private void writeAllLinesFromReader(String alreadyReadLine,
                                         boolean isFirstWrite,
                                         BufferedReader reader,
                                         BufferedWriter writer) throws IOException {
        if (!isFirstWrite) {
            writer.write("\n");
        }

        writer.write(alreadyReadLine);
        String line;

        while ((line = reader.readLine()) != null) {
            writer.write("\n" + line);
        }
    }
}
