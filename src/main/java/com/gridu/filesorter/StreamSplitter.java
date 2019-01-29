package com.gridu.filesorter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Comparator;

interface StreamSplitter {
    Path splitStreamIntoSortedChunks(InputStream stream,
                                     int numOfLinesInChunk,
                                     Comparator<String> comparator) throws IOException;
}
