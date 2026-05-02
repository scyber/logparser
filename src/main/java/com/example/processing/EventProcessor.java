package com.example.processing;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Deque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.model.ActiveStep;
import com.example.model.EventType;
import com.example.model.ProfilingEvent;

public class EventProcessor {

    /*
     * Potential issue with open/close test e.g. BEGIN/END cluold be closed with
     * different even
     */

    private static final Logger logger = LoggerFactory.getLogger(EventProcessor.class);
    private final Deque<ActiveStep> stack = new ArrayDeque<>();
    private final StepAggregator aggregator;

    public EventProcessor(StepAggregator aggregator) {
        this.aggregator = aggregator;
    }

    public void process(ProfilingEvent event) {
        if (event.getType() == EventType.BEGIN) {
            event.getQualifier().ifPresent(q -> logger.info("Event Processed {}", q));
            stack.push(new ActiveStep(event));
            return;
        }

        // END
        if (stack.isEmpty()) {
            // unmatched END → ignore
            return;
        }

        ActiveStep active = stack.pop();
        Duration duration = Duration.between(active.getStartTime(), event.getTimestamp());

        aggregator.record(active.getBeginEvent(), duration);
    }

}
