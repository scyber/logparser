package com.example.processing;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.example.model.ProfilingEvent;
import com.example.stats.DurationStats;

public class StepAggregator {

    private final Map<String, DurationStats> stats = new HashMap<>();

    public void record(ProfilingEvent event, Duration duration) {
        if (!event.getStepName().contains("SUBTEST"))
            return;

        String key = event.getQualifier().orElse(event.getStepName());
        stats.computeIfAbsent(key, k -> new DurationStats())
                .add(duration);
    }

    public Map<String, DurationStats> getStats() {
        return Collections.unmodifiableMap(stats);
    }

}
