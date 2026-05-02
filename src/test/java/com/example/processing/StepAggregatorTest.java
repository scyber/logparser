package com.example.processing;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.model.EventType;
import com.example.model.ProfilingEvent;
import com.example.stats.DurationStats;

public class StepAggregatorTest {

    private StepAggregator aggregator;

    @BeforeEach
    void setUp() {
        aggregator = new StepAggregator(500L, "SUBTEST"); // 500ms threshold
    }

    @Test
    void testInitialState() {
        Map<String, DurationStats> stats = aggregator.getStats();

        Assertions.assertNotNull(stats);
        Assertions.assertTrue(stats.isEmpty());
    }

    @Test
    void testRecordSubtestWithQualifier() {
        Instant now = Instant.now();
        ProfilingEvent event = new ProfilingEvent(
                now,
                EventType.BEGIN,
                "DEVICE_TEST.SUBTEST",
                Optional.of("check connection"));

        Duration duration = Duration.ofMillis(100);
        aggregator.record(event, duration);

        Map<String, DurationStats> stats = aggregator.getStats();
        Assertions.assertEquals(1, stats.size());
        Assertions.assertTrue(stats.containsKey("check connection"));
        Assertions.assertEquals(1, stats.get("check connection").getCount());
    }

    @Test
    void testRecordSubtestWithoutQualifier() {
        Instant now = Instant.now();
        ProfilingEvent event = new ProfilingEvent(
                now,
                EventType.BEGIN,
                "DEVICE_TEST.SUBTEST",
                Optional.empty());

        Duration duration = Duration.ofMillis(200);
        aggregator.record(event, duration);

        Map<String, DurationStats> stats = aggregator.getStats();
        Assertions.assertEquals(1, stats.size());
        Assertions.assertTrue(stats.containsKey("DEVICE_TEST.SUBTEST"));
    }

    @Test
    void testIgnoreNonSubtestEvents() {
        Instant now = Instant.now();
        ProfilingEvent event = new ProfilingEvent(
                now,
                EventType.BEGIN,
                "INSERTION",
                Optional.of("device #10"));

        Duration duration = Duration.ofMillis(100);
        aggregator.record(event, duration);

        Map<String, DurationStats> stats = aggregator.getStats();
        Assertions.assertTrue(stats.isEmpty());
    }

    @Test
    void testRecordMultipleSubtests() {
        Instant now = Instant.now();

        ProfilingEvent event1 = new ProfilingEvent(
                now,
                EventType.BEGIN,
                "DEVICE_TEST.SUBTEST",
                Optional.of("check connection"));

        ProfilingEvent event2 = new ProfilingEvent(
                now.plusMillis(1000),
                EventType.BEGIN,
                "DEVICE_TEST.SUBTEST",
                Optional.of("test digital logic"));

        aggregator.record(event1, Duration.ofMillis(100));
        aggregator.record(event2, Duration.ofMillis(200));

        Map<String, DurationStats> stats = aggregator.getStats();
        Assertions.assertEquals(2, stats.size());
    }

    @Test
    void testRecordDuplicateSubtest() {
        Instant now = Instant.now();
        ProfilingEvent event = new ProfilingEvent(
                now,
                EventType.BEGIN,
                "DEVICE_TEST.SUBTEST",
                Optional.of("check connection"));

        aggregator.record(event, Duration.ofMillis(100));
        aggregator.record(event, Duration.ofMillis(200));

        Map<String, DurationStats> stats = aggregator.getStats();
        Assertions.assertEquals(1, stats.size());
        Assertions.assertEquals(2, stats.get("check connection").getCount());
    }

    @Test
    void testGetStatsReturnsImmutableMap() {
        Map<String, DurationStats> stats1 = aggregator.getStats();
        Map<String, DurationStats> stats2 = aggregator.getStats();

        Assertions.assertNotSame(stats1, stats2);
    }
}
