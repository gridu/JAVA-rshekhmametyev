package com.gridu.filesorter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Comparator;

public interface StreamMerger {
    void mergeStreams(InputStream stream1,
                      InputStream stream2,
                      OutputStream outputStream,
                      Comparator<String> comparator) throws IOException;
}
