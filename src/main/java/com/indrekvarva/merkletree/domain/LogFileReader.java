package com.indrekvarva.merkletree.domain;

import lombok.Value;

import java.io.*;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 * Reads the log file into the memory all at once, so this is inefficient and not appropriate for large files.
 * Could be improved to process N number of rows at once and process them as they come in.
 */
@Value
public class LogFileReader {

    private String encoding;

    public LinkedList<byte[]> read(File file) throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), this.encoding));
            return reader
                    .lines()
                    .map((stringLine) -> stringLine.getBytes(Charset.forName(encoding)))
                    .collect(Collectors.toCollection(LinkedList::new));
        } finally {
            if (reader != null) {
                reader.close();
            }
        }


    }
}
