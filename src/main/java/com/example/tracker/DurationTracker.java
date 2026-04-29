package com.example.tracker;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.model.EventType;
import com.example.model.PEEvent;
import com.example.model.Stats;

public class DurationTracker implements FacadTracker {

    private final Logger logger = LoggerFactory.getLogger(DurationTracker.class);
    private final Map<String, LocalTime> openSteps = new ConcurrentHashMap<>();
    private final Map<String, Stats> stats = new ConcurrentHashMap<>();

    public void handle(PEEvent event) {
        String key = key(event);

        if (event.type() == EventType.BEGIN) {
            openSteps.put(key, event.timestamp());
        } else {
            LocalTime start = openSteps.remove(key);
            if (start != null) {
                long millis = Duration.between(start, event.timestamp()).toMillis();
                stats.computeIfAbsent(key, k -> new Stats())
                        .add(millis);
            }
        }
    }

    private String key(PEEvent e) {
        return e.stepPath() + (e.name() != null ? ":" + e.name() : "");
    }

    @Override
    public Map<String, Stats> getResult() {
        return stats;
    }

}
