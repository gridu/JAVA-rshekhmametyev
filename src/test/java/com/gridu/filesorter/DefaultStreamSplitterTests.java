package com.gridu.filesorter;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class DefaultStreamSplitterTests {
    private DefaultStreamSplitter _splitter;

    private final String streamContents;
    private final int numOfLinesInChunk;
    private final int resultNumberOfChunks;

    public DefaultStreamSplitterTests(String streamContents, int numOfLinesInChunk, int resultNumberOfChunks) {
        this.streamContents = streamContents;
        this.numOfLinesInChunk = numOfLinesInChunk;
        this.resultNumberOfChunks = resultNumberOfChunks;
    }

    @Before
    public void setUp() {
        _splitter = new DefaultStreamSplitter();
    }

    @Parameterized.Parameters
    public static Collection parameters() {
        return Arrays.asList(new Object[][] {
                { "", 10, 0 },
                { "line1\nline2\nline3\nline4", 1, 4 },
                { "line1\nline2\nline3\nline4", 3, 2 }
        });
    }

    @Test
    public void splitsIntoChunksCorrectly() throws IOException {
        Path tempDirPath = split(this.streamContents, this.numOfLinesInChunk);

        Assert.assertEquals(this.resultNumberOfChunks, Files.list(tempDirPath).count());
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionIfNumberOfLinesInChunkIsLessThanZero() throws IOException {
        _splitter.splitStreamIntoSortedChunks(new ByteInputStream(), -10, String::compareTo);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionIfNumberOfLinesInChunkIsZero() throws IOException {
        _splitter.splitStreamIntoSortedChunks(new ByteInputStream(), 0, String::compareTo);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionIfInputStreamIsNull() throws IOException {
        _splitter.splitStreamIntoSortedChunks(null, 10, String::compareTo);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionIfComparatorIsNull() throws IOException {
        _splitter.splitStreamIntoSortedChunks(new ByteInputStream(), 10, null);
    }

    private Path split(String stringToSplit, int numOfLinesInChunk) throws IOException {
        return _splitter.splitStreamIntoSortedChunks(new ByteArrayInputStream(stringToSplit.getBytes()),
                numOfLinesInChunk,
                String::compareTo);
    }
}
