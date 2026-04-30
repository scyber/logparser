package com.example.parser;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.example.model.EventType;
import com.example.model.ProfilingEvent;

public class ProfilingEventParserTest {
    @Test
    void testParseSuccess() {
        String st = """
                #PE[14:38:01.700] : BEGIN DEVICE_TEST.SUBTEST "test digital logic"
                #PE[14:38:02.200] : END DEVICE_TEST.SUBTEST
                """;

                ProfilingEventParser parser = new ProfilingEventParser();
                Optional<ProfilingEvent> optionalEvent = parser.parse(st);
                Assertions.assertNotNull(optionalEvent.get());
                ProfilingEvent event = optionalEvent.get();
                System.out.println(event.getStepName());
                Assertions.assertEquals("DEVICE_TEST.SUBTEST", event.getStepName());
                
    }

    @Test
    void testParceFailed(){

    }


}
