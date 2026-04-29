package com.example.model;

import java.time.Instant;

public class ActiveStep {
private final ProfilingEvent beginEvent;
	private final Instant startTime;

	public ActiveStep(ProfilingEvent beginEvent) {
    	this.beginEvent = beginEvent;
    	this.startTime = beginEvent.getTimestamp();
	}

	public ProfilingEvent getBeginEvent() { return beginEvent; }
	public Instant getStartTime() { return startTime; }

}
