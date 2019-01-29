package com.gridu.filesorter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;

interface FileSorter {
    Path sortFileByLines(String filePath, Comparator<String> comparator) throws IOException;
}
