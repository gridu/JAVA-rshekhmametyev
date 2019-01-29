package com.gridu.filesorter;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.*;
import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class DefaultStreamMergerTests {
    private final String stream1Contents;
    private final String stream2Contents;
    private final String outputResult;
    private DefaultStreamMerger _merger;

    public DefaultStreamMergerTests(String stream1Contents, String stream2Contents, String outputResult) {
        this.stream1Contents = stream1Contents;
        this.stream2Contents = stream2Contents;
        this.outputResult = outputResult;
    }

    @Before
    public void setUp() {
        _merger = new DefaultStreamMerger();
    }

    @Parameterized.Parameters
    public static Collection parameters() {
        return Arrays.asList(new Object[][]
                {
                        {"a\nb\nc\nd", "e\nf\ng\nh", "a\nb\nc\nd\ne\nf\ng\nh"},
                        {"", "", ""},
                        {"a\nb\nc\nd", "", "a\nb\nc\nd"},
                        {"", "a\nb\nc\nd", "a\nb\nc\nd"},
                        {"a", "b\nc\nd", "a\nb\nc\nd"},
                        {"a\nb\nc", "d", "a\nb\nc\nd"}
                });
    }

    @Test
    public void mergesStreamsCorrectly() throws IOException {
        OutputStream outputStream = initStreamsAndMerge(this.stream1Contents, this.stream2Contents);

        Assert.assertEquals(this.outputResult, outputStream.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionIfFirstStreamIsNull() throws IOException {
        StreamMerger merger = new DefaultStreamMerger();
        merger.mergeStreams(null, new ByteInputStream(), new ByteOutputStream(), String::compareTo);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionIfSecondStreamIsNull() throws IOException {
        StreamMerger merger = new DefaultStreamMerger();
        merger.mergeStreams(new ByteInputStream(), null, new ByteOutputStream(), String::compareTo);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionIfOutputStreamIsNull() throws IOException {
        StreamMerger merger = new DefaultStreamMerger();
        merger.mergeStreams(new ByteInputStream(), new ByteInputStream(), null, String::compareTo);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionIfComparatorStreamIsNull() throws IOException {
        StreamMerger merger = new DefaultStreamMerger();
        merger.mergeStreams(new ByteInputStream(), new ByteInputStream(), new ByteOutputStream(), null);
    }

    private OutputStream initStreamsAndMerge(String lines1, String lines2) throws IOException {
        InputStream stream1 = new ByteArrayInputStream(lines1.getBytes());
        InputStream stream2 = new ByteArrayInputStream(lines2.getBytes());
        OutputStream outputStream = new ByteArrayOutputStream();

        _merger.mergeStreams(stream1, stream2, outputStream, String::compareTo);

        return outputStream;
    }
}
