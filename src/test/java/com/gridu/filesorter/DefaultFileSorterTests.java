package com.gridu.filesorter;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.stream.Collectors;

public class DefaultFileSorterTests {
    private DefaultFileSorter fileSorter;
    private Path pathToFile;

    @Before
    public void setUp() throws IOException {
        this.fileSorter = new DefaultFileSorter();
        this.pathToFile = initializeTempFile();
    }

    @After
    public void tearDown() throws IOException {
        Files.delete(this.pathToFile);
    }

    @Test(expected = FileNotFoundException.class)
    public void throwsExceptionIfFileNotFound() throws IOException {
        this.fileSorter.sortFileByLines("asdf", String::compareTo);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionIfFileNameIsNull() throws IOException {
        this.fileSorter.sortFileByLines(null, String::compareTo);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionIfComparatorIsNull() throws IOException {
        this.fileSorter.sortFileByLines("asdf", null);
    }

    @Test
    public void sortsFileCorrectly() throws IOException {
        Path resultFilePath = this.fileSorter.sortFileByLines(this.pathToFile.toString(), String::compareTo);

        Assert.assertEquals(Files.lines(this.pathToFile).sorted().collect(Collectors.joining("\n")),
        Files.lines(resultFilePath).collect(Collectors.joining("\n")));
    }

    private static Path initializeTempFile() throws IOException {
        Path pathToFile = Files.createTempFile(UUID.randomUUID().toString(), "");

        StringJoiner joiner = new StringJoiner("\n");

        for (int i = 0; i < 10000; i++) {
            joiner.add("a").add("b").add("c");
        }

        Files.write(pathToFile, joiner.toString().getBytes());

        return pathToFile;
    }
}
