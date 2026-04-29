package com.example.parcer;

import java.time.LocalTime;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.model.EventType;
import com.example.model.PEEvent;

public class LogParser implements Parser {

    // ToDo resource Boundle

    private final Logger logger = LoggerFactory.getLogger(LogParser.class);
    private static final Pattern PE_PATTERN = Pattern.compile(
            "#PE\\[(?<time>\\d{2}:\\d{2}:\\d{2}\\.\\d{3})]\\s*:\\s*(?<type>BEGIN|END)\\s+(?<step>[A-Z_\\.]+)(\\s+\"(?<name>[^\"]+)\")?");

    /*
     * private static final Pattern PE_PATTERN = Pattern.compile(
     * "#PE\\[(?<time>\\d{2}:\\d{2}:\\d{2}\\.\\d{3})]\\s*:\\s*(?<type>BEGIN|END)\\s+(?<step>[A-Z_\\.]+)(\\s+\"(?<name>[^\"]+)\")?"
     * );
     * 
     */

    @Override
    public Optional<PEEvent> parse(String line) {
        Matcher m = PE_PATTERN.matcher(line);
        if (!m.matches()) {
            logger.warn("!!Not parced properly " + line);
            return Optional.empty();
        }

        LocalTime time = LocalTime.parse(m.group("time"));
        EventType type = EventType.valueOf(m.group("type"));
        String step = m.group("step");
        String name = m.group("name");

        return Optional.of(new PEEvent(time, type, step, name));
    }

}
