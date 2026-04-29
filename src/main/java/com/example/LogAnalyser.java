package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.parser.ProfilingEventParser;
import com.example.processing.EventProcessor;
import com.example.processing.StepAggregator;
import com.example.reporting.ReportPrinter;


public class LogAnalyser {

    private static final Logger logger = LoggerFactory.getLogger(LogAnalyser.class);

    public static void main(String[] args) {
    	if (args.length != 1) {
        	System.err.println("Usage: java -jar log-analyzer.jar <logfile>");
        	System.exit(1);
    	}

    	Path logFile = Paths.get(args[0]);

    	ProfilingEventParser parser = new ProfilingEventParser();
    	StepAggregator aggregator = new StepAggregator();
    	EventProcessor processor = new EventProcessor(aggregator);

    	try (BufferedReader reader = Files.newBufferedReader(logFile)) {
        	reader.lines()
              	.map(parser::parse)
              	.flatMap(Optional::stream)
              	.forEach(processor::process);
    	} catch (IOException e) {	
			logger.error(e.getMessage());
		
		}

    	ReportPrinter.printSubtestSummary(aggregator);
	}


}
