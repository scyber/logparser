package com.example.model;

import java.time.LocalTime;

public record PEEvent(LocalTime timestamp, EventType type, String stepPath, String name) {

}
