package com.example.processing;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.example.model.ProfilingEvent;
import com.example.stats.DurationStats;

public class StepAggregator {

    private final Map<String, DurationStats> stats = new HashMap<>();
    private final Long thresholdValue;

    public StepAggregator(Long thresholdValue) {
        this.thresholdValue = thresholdValue;
    }

    // ToDo Hardcoded but might be better to Identify SUBTEST etc

    public void record(ProfilingEvent event, Duration duration) {
        if (!event.getStepName().contains("SUBTEST"))
            return;

        String key = event.getQualifier().orElse(event.getStepName());
        stats.computeIfAbsent(key, k -> new DurationStats(thresholdValue))
                .add(duration);
    }

    public Map<String, DurationStats> getStats() {
        return Collections.unmodifiableMap(stats);
    }

}
