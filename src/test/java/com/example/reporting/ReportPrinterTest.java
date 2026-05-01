package com.example.reporting;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.example.model.EventType;
import com.example.model.ProfilingEvent;
import com.example.processing.EventProcessor;
import com.example.processing.StepAggregator;

public class ReportPrinterTest {
    
    private StepAggregator aggregator;
    
    @BeforeEach
    void setUp() {
        aggregator = new StepAggregator(500L);
    }
    
    @Test
    void testPrintSubtestSummaryEmptyStats() {
        // Should not throw exception with empty aggregator
        ReportPrinter.printSubtestSummary(aggregator);
    }
    
    @Test
    void testPrintSubtestSummaryWithData() {
        Instant now = Instant.now();
        
        // Add some test data
        ProfilingEvent event1 = new ProfilingEvent(
            now,
            EventType.BEGIN,
            "DEVICE_TEST.SUBTEST",
            Optional.of("check connection")
        );
        
        ProfilingEvent event2 = new ProfilingEvent(
            now.plusMillis(100),
            EventType.END,
            "DEVICE_TEST.SUBTEST",
            Optional.empty()
        );
        
        EventProcessor processor = new EventProcessor(aggregator);
        processor.process(event1);
        processor.process(event2);
        
        // Should not throw exception
        ReportPrinter.printSubtestSummary(aggregator);
    }
    
    @Test
    void testPrintSubtestSummaryMultipleSubtests() {
        Instant now = Instant.now();
        EventProcessor processor = new EventProcessor(aggregator);
        
        // First subtest
        ProfilingEvent begin1 = new ProfilingEvent(
            now,
            EventType.BEGIN,
            "DEVICE_TEST.SUBTEST",
            Optional.of("test 1")
        );
        ProfilingEvent end1 = new ProfilingEvent(
            now.plusMillis(100),
            EventType.END,
            "DEVICE_TEST.SUBTEST",
            Optional.empty()
        );
        
        // Second subtest
        ProfilingEvent begin2 = new ProfilingEvent(
            now.plusMillis(200),
            EventType.BEGIN,
            "DEVICE_TEST.SUBTEST",
            Optional.of("test 2")
        );
        ProfilingEvent end2 = new ProfilingEvent(
            now.plusMillis(400),
            EventType.END,
            "DEVICE_TEST.SUBTEST",
            Optional.empty()
        );
        
        processor.process(begin1);
        processor.process(end1);
        processor.process(begin2);
        processor.process(end2);
        
        // Should not throw exception and should include both subtests
        ReportPrinter.printSubtestSummary(aggregator);
    }
}
