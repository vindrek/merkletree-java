package com.indrekvarva.merkletree.domain;

import lombok.Value;

import java.io.*;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.stream.Collectors;

@Value
public class LogFileReader {

    private String encoding;

    public LinkedList<byte[]> read(File file) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), this.encoding));

        return reader
                .lines()
                .map((stringLine) -> stringLine.getBytes(Charset.forName(encoding)))
                .collect(Collectors.toCollection(LinkedList::new));
    }
}
