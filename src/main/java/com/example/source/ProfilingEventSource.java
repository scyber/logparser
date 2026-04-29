package com.example.source;

import java.util.stream.Stream;

import com.example.model.ProfilingEvent;

public interface ProfilingEventSource extends AutoCloseable {

    Stream<ProfilingEvent> events();

    @Override
    void close();

}
