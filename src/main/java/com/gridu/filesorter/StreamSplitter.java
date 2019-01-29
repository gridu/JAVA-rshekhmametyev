package com.gridu.filesorter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Comparator;

public interface StreamSplitter {
    Path splitFileIntoSortedChunks(InputStream stream,
                                   String tempDirName,
                                   int numOfLinesInChunk,
                                   Comparator<String> comparator) throws IOException;
}
