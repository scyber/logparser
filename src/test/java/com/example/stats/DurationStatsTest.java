package com.example.stats;

import java.time.Duration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DurationStatsTest {

    private DurationStats stats;

    @BeforeEach
    void setUp() {
        stats = new DurationStats(500); // 500ms threshold
    }

    @Test
    void testInitialState() {
        Assertions.assertEquals(0, stats.getCount());
        Assertions.assertEquals(Duration.ZERO, stats.getAverage());
        Assertions.assertEquals(Duration.ZERO, stats.getMax());
    }

    @Test
    void testAddSingleDuration() {
        Duration duration = Duration.ofMillis(100);
        stats.add(duration);

        Assertions.assertEquals(1, stats.getCount());
        Assertions.assertEquals(duration, stats.getAverage());
        Assertions.assertEquals(duration, stats.getMax());
    }

    @Test
    void testAddMultipleDurations() {
        Duration d1 = Duration.ofMillis(100);
        Duration d2 = Duration.ofMillis(200);
        Duration d3 = Duration.ofMillis(300);

        stats.add(d1);
        stats.add(d2);
        stats.add(d3);

        Assertions.assertEquals(3, stats.getCount());
        Assertions.assertEquals(Duration.ofMillis(200), stats.getAverage());
        Assertions.assertEquals(d3, stats.getMax());
    }

    @Test
    void testMaxDuration() {
        stats.add(Duration.ofMillis(100));
        stats.add(Duration.ofMillis(500));
        stats.add(Duration.ofMillis(300));

        Assertions.assertEquals(Duration.ofMillis(500), stats.getMax());
    }

    @Test
    void testAverageDuration() {
        stats.add(Duration.ofMillis(100));
        stats.add(Duration.ofMillis(200));
        stats.add(Duration.ofMillis(300));

        Assertions.assertEquals(Duration.ofMillis(200), stats.getAverage());
    }

    @Test
    void testThresholdNotExceeded() {
        // Should not log warning for durations below threshold
        stats.add(Duration.ofMillis(100));
        stats.add(Duration.ofMillis(200));

        // No exception should occur
        Assertions.assertEquals(2, stats.getCount());
    }

    @Test
    void testThresholdExceeded() {
        // Should log warning for durations above threshold
        stats.add(Duration.ofMillis(600)); // Above 500ms threshold

        Assertions.assertEquals(1, stats.getCount());
        Assertions.assertTrue(stats.getMax().toMillis() > 500);
    }

    @Test
    void testZeroDuration() {
        stats.add(Duration.ZERO);

        Assertions.assertEquals(1, stats.getCount());
        Assertions.assertEquals(Duration.ZERO, stats.getAverage());
    }

    @Test
    void testLargeDurations() {
        stats.add(Duration.ofSeconds(10));
        stats.add(Duration.ofSeconds(20));
        stats.add(Duration.ofSeconds(30));

        Assertions.assertEquals(3, stats.getCount());
        Assertions.assertEquals(Duration.ofSeconds(20), stats.getAverage());
        Assertions.assertEquals(Duration.ofSeconds(30), stats.getMax());
    }

    @Test
    void testGetMax() {
        stats.add(Duration.ofMillis(50));
        stats.add(Duration.ofMillis(150));
        stats.add(Duration.ofMillis(250));

        Assertions.assertEquals(Duration.ofMillis(250), stats.getMax());
    }
}
