package com.example.model;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ProfilingEventTest {

    @Test
    void testProfilingEventCreationWithQualifier() {
        Instant timestamp = Instant.now();
        String stepName = "DEVICE_TEST.SUBTEST";
        String qualifier = "test digital logic";

        ProfilingEvent event = new ProfilingEvent(
                timestamp,
                EventType.BEGIN,
                stepName,
                Optional.of(qualifier));

        Assertions.assertEquals(timestamp, event.getTimestamp());
        Assertions.assertEquals(EventType.BEGIN, event.getType());
        Assertions.assertEquals(stepName, event.getStepName());
        Assertions.assertTrue(event.getQualifier().isPresent());
        Assertions.assertEquals(qualifier, event.getQualifier().get());
    }

    @Test
    void testProfilingEventCreationWithoutQualifier() {
        Instant timestamp = Instant.now();
        String stepName = "INSERTION";

        ProfilingEvent event = new ProfilingEvent(
                timestamp,
                EventType.END,
                stepName,
                Optional.empty());

        Assertions.assertEquals(timestamp, event.getTimestamp());
        Assertions.assertEquals(EventType.END, event.getType());
        Assertions.assertEquals(stepName, event.getStepName());
        Assertions.assertFalse(event.getQualifier().isPresent());
    }

    @Test
    void testGetTimestamp() {
        Instant timestamp = Instant.parse("2026-05-01T10:30:00Z");
        ProfilingEvent event = new ProfilingEvent(
                timestamp,
                EventType.BEGIN,
                "TEST",
                Optional.empty());

        Assertions.assertEquals(timestamp, event.getTimestamp());
    }

    @Test
    void testGetType() {
        ProfilingEvent beginEvent = new ProfilingEvent(
                Instant.now(),
                EventType.BEGIN,
                "TEST",
                Optional.empty());

        ProfilingEvent endEvent = new ProfilingEvent(
                Instant.now(),
                EventType.END,
                "TEST",
                Optional.empty());

        Assertions.assertEquals(EventType.BEGIN, beginEvent.getType());
        Assertions.assertEquals(EventType.END, endEvent.getType());
    }
}
