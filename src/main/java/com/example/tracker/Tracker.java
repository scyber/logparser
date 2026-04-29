package com.example.tracker;

import com.example.model.PEEvent;

public interface Tracker {
    void handle(PEEvent event);
}
