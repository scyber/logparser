package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.parcer.Parser;
import com.example.parcer.LogParser;
import com.example.tracker.DurationTracker;
import com.example.tracker.FacadTracker;
import com.example.tracker.Tracker;

public class LogAnalyser {

    private static final Logger logger = LoggerFactory.getLogger(LogAnalyser.class);

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            logger.warn("Usage: LogAnalyzer <logfile>");
            return;
        }

        Parser parser = new LogParser();
        FacadTracker tracker = new DurationTracker();

        try (BufferedReader reader = Files.newBufferedReader(Path.of(args[0]))) {
            reader.lines()
                    .map(parser::parse)
                    .flatMap(Optional::stream)
                    .filter(e -> e.stepPath().startsWith("DEVICE_TEST"))
                    .forEach(tracker::handle);
        }

        tracker.getResult().forEach((k, v) -> {
            System.out.printf(
                    "Subtest: %s | count=%d avg=%dms max=%dms%n",
                    k, v.count(), v.avg(), v.max());
        });
    }

}
