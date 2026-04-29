package com.example.model;

import java.time.Instant;
import java.util.Optional;

public class ProfilingEvent {

    private final Instant timestamp;
    private final EventType type;
    private final String stepName;
    private final Optional<String> qualifier;

    public ProfilingEvent(Instant timestamp,
            EventType type,
            String stepName,
            Optional<String> qualifier) {
        this.timestamp = timestamp;
        this.type = type;
        this.stepName = stepName;
        this.qualifier = qualifier;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public EventType getType() {
        return type;
    }

    public String getStepName() {
        return stepName;
    }

    public Optional<String> getQualifier() {
        return qualifier;
    }

}
