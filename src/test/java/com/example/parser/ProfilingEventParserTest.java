package com.example.parser;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.model.EventType;
import com.example.model.ProfilingEvent;

public class ProfilingEventParserTest {

    private ProfilingEventParser parser;

    @BeforeEach
    void setUp() {
        parser = new ProfilingEventParser();
    }

    @Test
    void testParseBeginEventWithQualifier() {
        String line = "#PE[14:38:01.700] : BEGIN DEVICE_TEST.SUBTEST \"test digital logic\"";
        Optional<ProfilingEvent> optionalEvent = parser.parse(line);

        Assertions.assertTrue(optionalEvent.isPresent());
        ProfilingEvent event = optionalEvent.get();
        Assertions.assertEquals("DEVICE_TEST.SUBTEST", event.getStepName());
        Assertions.assertEquals(EventType.BEGIN, event.getType());
        Assertions.assertEquals("test digital logic", event.getQualifier().get());
        Assertions.assertNotNull(event.getTimestamp());
    }

    @Test
    void testParseEndEventWithQualifier() {
        String line = "#PE[14:38:02.200] : END DEVICE_TEST.SUBTEST \"test digital logic\"";
        Optional<ProfilingEvent> optionalEvent = parser.parse(line);

        Assertions.assertTrue(optionalEvent.isPresent());
        ProfilingEvent event = optionalEvent.get();
        Assertions.assertEquals("DEVICE_TEST.SUBTEST", event.getStepName());
        Assertions.assertEquals(EventType.END, event.getType());
    }

    @Test
    void testParseBeginEventWithoutQualifier() {
        String line = "#PE[14:34:31.300] : BEGIN INSERTION";
        Optional<ProfilingEvent> optionalEvent = parser.parse(line);

        Assertions.assertTrue(optionalEvent.isPresent());
        ProfilingEvent event = optionalEvent.get();
        Assertions.assertEquals("INSERTION", event.getStepName());
        Assertions.assertEquals(EventType.BEGIN, event.getType());
        Assertions.assertFalse(event.getQualifier().isPresent());
    }

    @Test
    void testParseEndEventWithoutQualifier() {
        String line = "#PE[14:34:33.400] : END INSERTION";
        Optional<ProfilingEvent> optionalEvent = parser.parse(line);

        Assertions.assertTrue(optionalEvent.isPresent());
        ProfilingEvent event = optionalEvent.get();
        Assertions.assertEquals("INSERTION", event.getStepName());
        Assertions.assertEquals(EventType.END, event.getType());
    }

    @Test
    void testParseNestedEventName() {
        String line = "#PE[14:35:01.300] : BEGIN DEVICE_TEST.SUBTEST \"check connection\"";
        Optional<ProfilingEvent> optionalEvent = parser.parse(line);

        Assertions.assertTrue(optionalEvent.isPresent());
        ProfilingEvent event = optionalEvent.get();
        Assertions.assertEquals("DEVICE_TEST.SUBTEST", event.getStepName());
    }

    @Test
    void testParseInvalidLine() {
        String line = "This is not a valid log line";
        Optional<ProfilingEvent> optionalEvent = parser.parse(line);

        Assertions.assertFalse(optionalEvent.isPresent());
    }

    @Test
    void testParseMissingQualifierMarker() {
        String line = "#PE[14:38:01.700] : BEGIN DEVICE_TEST";
        Optional<ProfilingEvent> optionalEvent = parser.parse(line);

        Assertions.assertTrue(optionalEvent.isPresent());
        ProfilingEvent event = optionalEvent.get();
        Assertions.assertEquals("DEVICE_TEST", event.getStepName());
        Assertions.assertFalse(event.getQualifier().isPresent());
    }

    @Test
    void testParseMalformedTimestamp() {
        String line = "#PE[25:75:90.999] : BEGIN DEVICE_TEST \"qualifier\"";
        Optional<ProfilingEvent> optionalEvent = parser.parse(line);

        // Should fail to parse due to invalid time
        Assertions.assertFalse(optionalEvent.isPresent());
    }

    @Test
    void testParseInvalidEventType() {
        String line = "#PE[14:38:01.700] : INVALID DEVICE_TEST \"test\"";
        Optional<ProfilingEvent> optionalEvent = parser.parse(line);

        Assertions.assertFalse(optionalEvent.isPresent());
    }

    @Test
    void testParseRemovalEvent() {
        String line = "#PE[14:36:04.200] : BEGIN REMOVAL";
        Optional<ProfilingEvent> optionalEvent = parser.parse(line);

        Assertions.assertTrue(optionalEvent.isPresent());
        ProfilingEvent event = optionalEvent.get();
        Assertions.assertEquals("REMOVAL", event.getStepName());
        Assertions.assertEquals(EventType.BEGIN, event.getType());
    }
}
