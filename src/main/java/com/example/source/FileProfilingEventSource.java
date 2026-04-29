package com.example.source;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

import com.example.model.ProfilingEvent;
import com.example.parser.ProfilingEventParser;

public class FileProfilingEventSource implements ProfilingEventSource {

    private final BufferedReader reader;
    private final ProfilingEventParser parser;
    

    public FileProfilingEventSource(Path path, ProfilingEventParser parser) {
        try {
            this.reader = Files.newBufferedReader(path);
            this.parser = parser;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public Stream<ProfilingEvent> events() {
        return reader.lines()
            .map(parser::parse)
            .flatMap(Optional::stream);
    }

    @Override
    public void close() {
        try {
            reader.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
