package com.example.parser;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TimeParser {
private static final DateTimeFormatter FORMAT =
    	DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

	public static Instant parse(String time) {
    	LocalTime t = LocalTime.parse(time, FORMAT);
    	return t.atDate(LocalDate.now())
            	.atZone(ZoneId.systemDefault())
            	.toInstant();
	}

}
