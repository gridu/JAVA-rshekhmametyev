package com.gridu.filesorter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;

public interface StreamMerger {
    void mergeStreams(InputStream stream1,
                      InputStream stream2,
                      String targetDir,
                      Comparator<String> comparator) throws IOException;
}
