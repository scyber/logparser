package com.example.model;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ActiveStepTest {

    @Test
    void testActiveStepCreation() {
        Instant timestamp = Instant.now();
        ProfilingEvent beginEvent = new ProfilingEvent(
                timestamp,
                EventType.BEGIN,
                "DEVICE_TEST.SUBTEST",
                Optional.of("test digital logic"));

        ActiveStep activeStep = new ActiveStep(beginEvent);

        Assertions.assertNotNull(activeStep);
        Assertions.assertEquals(beginEvent, activeStep.getBeginEvent());
        Assertions.assertEquals(timestamp, activeStep.getStartTime());
    }

    @Test
    void testGetBeginEvent() {
        Instant timestamp = Instant.now();
        ProfilingEvent beginEvent = new ProfilingEvent(
                timestamp,
                EventType.BEGIN,
                "INSERTION",
                Optional.empty());

        ActiveStep activeStep = new ActiveStep(beginEvent);

        Assertions.assertEquals(beginEvent, activeStep.getBeginEvent());
    }

    @Test
    void testGetStartTime() {
        Instant timestamp = Instant.parse("2026-05-01T10:30:00Z");
        ProfilingEvent beginEvent = new ProfilingEvent(
                timestamp,
                EventType.BEGIN,
                "REMOVAL",
                Optional.empty());

        ActiveStep activeStep = new ActiveStep(beginEvent);

        Assertions.assertEquals(timestamp, activeStep.getStartTime());
    }

    @Test
    void testStartTimeEqualsEventTimestamp() {
        Instant timestamp = Instant.now();
        ProfilingEvent beginEvent = new ProfilingEvent(
                timestamp,
                EventType.BEGIN,
                "TEST",
                Optional.empty());

        ActiveStep activeStep = new ActiveStep(beginEvent);

        Assertions.assertEquals(activeStep.getStartTime(), beginEvent.getTimestamp());
    }
}
