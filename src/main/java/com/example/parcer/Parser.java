package com.example.parcer;

import java.util.Optional;
import com.example.model.PEEvent;

public interface Parser {
    Optional<PEEvent> parse(String line);
}
