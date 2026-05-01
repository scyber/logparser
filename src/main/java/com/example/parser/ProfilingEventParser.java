package com.example.parser;

import java.time.Instant;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.model.EventType;
import com.example.model.ProfilingEvent;

public class ProfilingEventParser {

	private final Logger logger = LoggerFactory.getLogger(ProfilingEventParser.class);
	/*
	 * Pattern matcher could be moved to resources as external resource or somehow
	 * provided externally for processing even from
	 * api, file, etc.
	 */

	private static final Pattern PATTERN = Pattern.compile(
			"#PE\\[(?<time>[^\\]]+)]\\s*:\\s*(?<type>BEGIN|END)\\s+(?<name>[A-Z_.]+)(?:\\s+\"(?<qual>[^\"]+)\")?");

	public Optional<ProfilingEvent> parse(String line) {
		Matcher m = PATTERN.matcher(line);
		if (!m.find())
			return Optional.empty();

		try {
			Instant ts = TimeParser.parse(m.group("time"));
			EventType type = EventType.valueOf(m.group("type"));
			String name = m.group("name");
			Optional<String> qual = Optional.ofNullable(m.group("qual"));

			return Optional.of(new ProfilingEvent(ts, type, name, qual));
		} catch (Exception e) {
			logger.error("Failed to parse line {}", line);
			// logging exception parcing
			// malformed timestamp or enum
			return Optional.empty();
		}
	}

}
