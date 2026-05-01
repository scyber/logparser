package com.example.parser;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TimeParserTest {

    @Test
    void testParseValidTime() {
        String timeString = "14:38:01.700";
        Instant result = TimeParser.parse(timeString);

        Assertions.assertNotNull(result);
        LocalTime expectedTime = LocalTime.parse("14:38:01.700",
                java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
        Instant expectedInstant = expectedTime.atDate(LocalDate.now())
                .atZone(ZoneId.systemDefault())
                .toInstant();

        Assertions.assertEquals(expectedInstant, result);
    }

    @Test
    void testParseTimeAtMidnight() {
        String timeString = "00:00:00.000";
        Instant result = TimeParser.parse(timeString);

        Assertions.assertNotNull(result);
        LocalTime expectedTime = LocalTime.MIDNIGHT;
        Instant expectedInstant = expectedTime.atDate(LocalDate.now())
                .atZone(ZoneId.systemDefault())
                .toInstant();

        Assertions.assertEquals(expectedInstant, result);
    }

    @Test
    void testParseTimeAtNoon() {
        String timeString = "12:00:00.000";
        Instant result = TimeParser.parse(timeString);

        Assertions.assertNotNull(result);
        LocalTime expectedTime = LocalTime.NOON;
        Instant expectedInstant = expectedTime.atDate(LocalDate.now())
                .atZone(ZoneId.systemDefault())
                .toInstant();

        Assertions.assertEquals(expectedInstant, result);
    }

    @Test
    void testParseTimeWithMilliseconds() {
        String timeString = "23:59:59.999";
        Instant result = TimeParser.parse(timeString);

        Assertions.assertNotNull(result);
        // Verify it's a valid instant
        Assertions.assertNotNull(result);
    }

    @Test
    void testParseInvalidTimeFormat() {
        String timeString = "14-38-01.700"; // Wrong separator

        Assertions.assertThrows(java.time.format.DateTimeParseException.class,
                () -> TimeParser.parse(timeString));
    }

    @Test
    void testParseInvalidHours() {
        String timeString = "25:00:00.000"; // Invalid hour

        Assertions.assertThrows(java.time.format.DateTimeParseException.class,
                () -> TimeParser.parse(timeString));
    }

    @Test
    void testParseInvalidMinutes() {
        String timeString = "12:75:00.000"; // Invalid minutes

        Assertions.assertThrows(java.time.format.DateTimeParseException.class,
                () -> TimeParser.parse(timeString));
    }

    @Test
    void testParseInvalidSeconds() {
        String timeString = "12:30:75.000"; // Invalid seconds

        Assertions.assertThrows(java.time.format.DateTimeParseException.class,
                () -> TimeParser.parse(timeString));
    }
}
