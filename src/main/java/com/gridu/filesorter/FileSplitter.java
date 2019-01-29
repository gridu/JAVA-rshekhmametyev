package com.gridu.filesorter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;

public interface FileSplitter {
    Path splitFileIntoSortedChunks(String filePath,
                                   String tempDirName,
                                   int numOfLinesInChunk,
                                   Comparator<String> comparator) throws IOException;
}
