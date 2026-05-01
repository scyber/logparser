package com.example.processing;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.model.EventType;
import com.example.model.ProfilingEvent;

public class EventProcessorTest {
    
    private EventProcessor processor;
    private StepAggregator aggregator;
    
    @BeforeEach
    void setUp() {
        aggregator = new StepAggregator(500L);
        processor = new EventProcessor(aggregator);
    }
    
    @Test
    void testProcessBeginEvent() {
        Instant now = Instant.now();
        ProfilingEvent beginEvent = new ProfilingEvent(
            now,
            EventType.BEGIN,
            "DEVICE_TEST.SUBTEST",
            Optional.of("test digital logic")
        );
        
        // Should not throw exception
        processor.process(beginEvent);
        
        // Stats should be empty (only END event triggers recording)
        Assertions.assertTrue(aggregator.getStats().isEmpty());
    }
    
    @Test
    void testProcessMatchedBeginEndPair() {
        Instant startTime = Instant.now();
        Instant endTime = startTime.plusMillis(500);
        
        ProfilingEvent beginEvent = new ProfilingEvent(
            startTime,
            EventType.BEGIN,
            "DEVICE_TEST.SUBTEST",
            Optional.of("check connection")
        );
        
        ProfilingEvent endEvent = new ProfilingEvent(
            endTime,
            EventType.END,
            "DEVICE_TEST.SUBTEST",
            Optional.empty()
        );
        
        processor.process(beginEvent);
        processor.process(endEvent);
        
        // Stats should be populated
        Assertions.assertEquals(1, aggregator.getStats().size());
        Assertions.assertEquals(1, aggregator.getStats().get("check connection").getCount());
    }
    
    @Test
    void testProcessNestedEvents() {
        Instant t1 = Instant.now();
        Instant t2 = t1.plusMillis(100);
        Instant t3 = t2.plusMillis(200);
        Instant t4 = t3.plusMillis(100);
        
        ProfilingEvent begin1 = new ProfilingEvent(t1, EventType.BEGIN, "DEVICE_TEST", Optional.empty());
        ProfilingEvent begin2 = new ProfilingEvent(t2, EventType.BEGIN, "DEVICE_TEST.SUBTEST", Optional.of("test 1"));
        ProfilingEvent end2 = new ProfilingEvent(t3, EventType.END, "DEVICE_TEST.SUBTEST", Optional.empty());
        ProfilingEvent end1 = new ProfilingEvent(t4, EventType.END, "DEVICE_TEST", Optional.empty());
        
        processor.process(begin1);
        processor.process(begin2);
        processor.process(end2);
        processor.process(end1);
        
        // Should have recorded the subtest
        Assertions.assertEquals(1, aggregator.getStats().size());
    }
    
    @Test
    void testProcessUnmatchedEndEvent() {
        Instant now = Instant.now();
        ProfilingEvent endEvent = new ProfilingEvent(
            now,
            EventType.END,
            "DEVICE_TEST.SUBTEST",
            Optional.empty()
        );
        
        // Should not throw exception for unmatched END
        processor.process(endEvent);
        
        // Stats should be empty
        Assertions.assertTrue(aggregator.getStats().isEmpty());
    }
    
    @Test
    void testProcessMultiplePairs() {
        Instant t1 = Instant.now();
        
        // First pair
        ProfilingEvent begin1 = new ProfilingEvent(
            t1,
            EventType.BEGIN,
            "DEVICE_TEST.SUBTEST",
            Optional.of("test 1")
        );
        ProfilingEvent end1 = new ProfilingEvent(
            t1.plusMillis(100),
            EventType.END,
            "DEVICE_TEST.SUBTEST",
            Optional.empty()
        );
        
        // Second pair
        ProfilingEvent begin2 = new ProfilingEvent(
            t1.plusMillis(200),
            EventType.BEGIN,
            "DEVICE_TEST.SUBTEST",
            Optional.of("test 2")
        );
        ProfilingEvent end2 = new ProfilingEvent(
            t1.plusMillis(300),
            EventType.END,
            "DEVICE_TEST.SUBTEST",
            Optional.empty()
        );
        
        processor.process(begin1);
        processor.process(end1);
        processor.process(begin2);
        processor.process(end2);
        
        Assertions.assertEquals(2, aggregator.getStats().size());
        Assertions.assertEquals(1, aggregator.getStats().get("test 1").getCount());
        Assertions.assertEquals(1, aggregator.getStats().get("test 2").getCount());
    }
    
    @Test
    void testProcessWithoutQualifier() {
        Instant startTime = Instant.now();
        Instant endTime = startTime.plusMillis(200);
        
        ProfilingEvent beginEvent = new ProfilingEvent(
            startTime,
            EventType.BEGIN,
            "DEVICE_TEST.SUBTEST",
            Optional.empty()
        );
        
        ProfilingEvent endEvent = new ProfilingEvent(
            endTime,
            EventType.END,
            "DEVICE_TEST.SUBTEST",
            Optional.empty()
        );
        
        processor.process(beginEvent);
        processor.process(endEvent);
        
        Assertions.assertEquals(1, aggregator.getStats().size());
        Assertions.assertTrue(aggregator.getStats().containsKey("DEVICE_TEST.SUBTEST"));
    }
}
