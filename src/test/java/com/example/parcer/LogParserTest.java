package com.example.parcer;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.example.model.EventType;
import com.example.model.PEEvent;

public class LogParserTest {

    private final LogParser parser = new LogParser();

    @Test
    void parseValidBeginEvent() {
        String line = "#PE[12:34:56.789] : BEGIN STEP_ONE \"First step\"";

        Optional<PEEvent> result = parser.parse(line);

        assertTrue(result.isPresent());
        PEEvent event = result.get();
        assertEquals(LocalTime.of(12, 34, 56, 789_000_000), event.timestamp());
        assertEquals(EventType.BEGIN, event.type());
        assertEquals("STEP_ONE", event.stepPath());
        assertEquals("First step", event.name());
    }

    @Test
    void parseValidEndEventWithoutName() {
        String line = "#PE[00:00:00.000] : END STEP_TWO";

        Optional<PEEvent> result = parser.parse(line);

        assertTrue(result.isPresent());
        PEEvent event = result.get();
        assertEquals(LocalTime.MIDNIGHT, event.timestamp());
        assertEquals(EventType.END, event.type());
        assertEquals("STEP_TWO", event.stepPath());
        assertNull(event.name());
    }

    @Test
    void parseInvalidLineReturnsEmpty() {
        String line = "INVALID LINE";

        Optional<PEEvent> result = parser.parse(line);

        assertFalse(result.isPresent());
    }
}
