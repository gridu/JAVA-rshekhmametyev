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
    private DefaultFileSorter _fileSorter;
    private Path _pathToFile;

    @Before
    public void setUp() throws IOException {
        _fileSorter = new DefaultFileSorter();
        _pathToFile = initializeTempFile();
    }

    @After
    public void tearDown() throws IOException {
        Files.delete(_pathToFile);
    }

    @Test(expected = FileNotFoundException.class)
    public void throwsExceptionIfFileNotFound() throws IOException {
        _fileSorter.sortFileByLines("asdf", String::compareTo);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionIfFileNameIsNull() throws IOException {
        _fileSorter.sortFileByLines(null, String::compareTo);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionIfComparatorIsNull() throws IOException {
        _fileSorter.sortFileByLines("asdf", null);
    }

    @Test
    public void sortsFileCorrectly() throws IOException {
        Path resultFilePath = _fileSorter.sortFileByLines(_pathToFile.toString(), String::compareTo);

        Assert.assertEquals(Files.lines(_pathToFile).sorted().collect(Collectors.joining("\n")),
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
