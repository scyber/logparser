package com.example.parser;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TimeParser {
	private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

	// We don't have such information in logs e.g. potential issue with this could
	// be in results
	public static Instant parse(String time) {
		LocalTime t = LocalTime.parse(time, FORMAT);
		return t.atDate(LocalDate.now())
				.atZone(ZoneId.systemDefault())
				.toInstant();
	}

}
