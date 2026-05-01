package com.example.source;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.example.model.ProfilingEvent;
import com.example.parser.ProfilingEventParser;

public class FileProfilingEventSourceTest {

    private ProfilingEventParser parser;

    @BeforeEach
    void setUp() {
        parser = new ProfilingEventParser();
    }

    @Test
    void testReadValidLogFile(@TempDir Path tempDir) throws IOException {
        // Create a test log file
        Path logFile = tempDir.resolve("test.log");
        String logContent = """
                #PE[14:38:01.700] : BEGIN DEVICE_TEST.SUBTEST "test digital logic"
                #PE[14:38:02.200] : END DEVICE_TEST.SUBTEST
                #PE[14:34:31.300] : BEGIN INSERTION "device #10"
                #PE[14:34:33.400] : END INSERTION
                """;
        Files.writeString(logFile, logContent);

        try (FileProfilingEventSource source = new FileProfilingEventSource(logFile, parser)) {
            List<ProfilingEvent> events = source.events().collect(Collectors.toList());

            Assertions.assertEquals(4, events.size());
            Assertions.assertEquals("DEVICE_TEST.SUBTEST", events.get(0).getStepName());
            Assertions.assertEquals("INSERTION", events.get(2).getStepName());
        }
    }

    @Test
    void testReadEmptyLogFile(@TempDir Path tempDir) throws IOException {
        // Create an empty log file
        Path logFile = tempDir.resolve("empty.log");
        Files.writeString(logFile, "");

        try (FileProfilingEventSource source = new FileProfilingEventSource(logFile, parser)) {
            List<ProfilingEvent> events = source.events().collect(Collectors.toList());

            Assertions.assertEquals(0, events.size());
        }
    }

    @Test
    void testReadLogFileWithInvalidLines(@TempDir Path tempDir) throws IOException {
        // Create a log file with some invalid lines
        Path logFile = tempDir.resolve("mixed.log");
        String logContent = """
                #PE[14:38:01.700] : BEGIN DEVICE_TEST.SUBTEST "test"
                This is not a valid log line
                #PE[14:38:02.200] : END DEVICE_TEST.SUBTEST
                Another invalid line
                #PE[14:34:31.300] : BEGIN INSERTION
                """;
        Files.writeString(logFile, logContent);

        try (FileProfilingEventSource source = new FileProfilingEventSource(logFile, parser)) {
            List<ProfilingEvent> events = source.events().collect(Collectors.toList());

            // Should only get valid events (3)
            Assertions.assertEquals(3, events.size());
            Assertions.assertEquals("DEVICE_TEST.SUBTEST", events.get(0).getStepName());
            Assertions.assertEquals("DEVICE_TEST.SUBTEST", events.get(1).getStepName());
            Assertions.assertEquals("INSERTION", events.get(2).getStepName());
        }
    }

    @Test
    void testReadLogFileWithQualifiers(@TempDir Path tempDir) throws IOException {
        Path logFile = tempDir.resolve("qualified.log");
        String logContent = """
                #PE[14:38:01.700] : BEGIN DEVICE_TEST.SUBTEST "check connection"
                #PE[14:38:02.200] : END DEVICE_TEST.SUBTEST "check connection"
                """;
        Files.writeString(logFile, logContent);

        try (FileProfilingEventSource source = new FileProfilingEventSource(logFile, parser)) {
            List<ProfilingEvent> events = source.events().collect(Collectors.toList());

            Assertions.assertEquals(2, events.size());
            Assertions.assertEquals("check connection", events.get(0).getQualifier().get());
        }
    }

    @Test
    void testFileNotFound() {
        Path nonExistentFile = Paths.get("/nonexistent/path/file.log");

        Assertions.assertThrows(
                java.io.UncheckedIOException.class,
                () -> new FileProfilingEventSource(nonExistentFile, parser));
    }

    @Test
    void testCloseMethod(@TempDir Path tempDir) throws IOException {
        Path logFile = tempDir.resolve("test.log");
        Files.writeString(logFile, "#PE[14:38:01.700] : BEGIN DEVICE_TEST");

        FileProfilingEventSource source = new FileProfilingEventSource(logFile, parser);

        // Should not throw exception
        source.close();
    }

    @Test
    void testAutoCloseableWithTryWithResources(@TempDir Path tempDir) throws IOException {
        Path logFile = tempDir.resolve("test.log");
        String logContent = """
                #PE[14:38:01.700] : BEGIN DEVICE_TEST.SUBTEST "test"
                #PE[14:38:02.200] : END DEVICE_TEST.SUBTEST
                """;
        Files.writeString(logFile, logContent);

        try (FileProfilingEventSource source = new FileProfilingEventSource(logFile, parser)) {
            List<ProfilingEvent> events = source.events().collect(Collectors.toList());
            Assertions.assertEquals(2, events.size());
        }
        // Resource should be closed automatically
    }

    @Test
    void testReadMultipleDevices(@TempDir Path tempDir) throws IOException {
        Path logFile = tempDir.resolve("devices.log");
        String logContent = """
                #PE[14:34:31.300] : BEGIN INSERTION "device #10"
                #PE[14:35:01.300] : BEGIN DEVICE_TEST.SUBTEST "check connection"
                #PE[14:35:01.600] : END DEVICE_TEST.SUBTEST
                #PE[14:34:33.400] : END INSERTION
                #PE[14:37:31.300] : BEGIN INSERTION "device #13"
                #PE[14:38:01.300] : BEGIN DEVICE_TEST.SUBTEST "test digital logic"
                #PE[14:38:02.200] : END DEVICE_TEST.SUBTEST
                #PE[14:37:33.400] : END INSERTION
                """;
        Files.writeString(logFile, logContent);

        try (FileProfilingEventSource source = new FileProfilingEventSource(logFile, parser)) {
            List<ProfilingEvent> events = source.events().collect(Collectors.toList());

            Assertions.assertEquals(8, events.size());
            Assertions.assertEquals("device #10", events.get(0).getQualifier().get());
            Assertions.assertEquals("device #13", events.get(4).getQualifier().get());
        }
    }
}
