package com.example;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.parser.ProfilingEventParser;
import com.example.processing.EventProcessor;
import com.example.processing.StepAggregator;
import com.example.reporting.ReportPrinter;
import com.example.source.FileProfilingEventSource;
import com.example.source.ProfilingEventSource;

public class LogAnalyser {

	private static final Logger logger = LoggerFactory.getLogger(LogAnalyser.class);

	public static void main(String[] args) {
		if (args.length < 3) {
			System.err.println("Usage: java -jar log-analyzer.jar <logfile> <threshold_ms> <sub_step>");
			System.exit(1);
		}
		logger.info("Parsing log started {}", LocalDateTime.now());
		Path logFile = Paths.get(args[0]);
		long thresholdValue = Long.parseLong(args[1]);
		String stepNameValue = args[2];
		// Path logFile = Paths.get("event.log");

		ProfilingEventParser parser = new ProfilingEventParser();
		StepAggregator aggregator = new StepAggregator(thresholdValue, stepNameValue);
		EventProcessor processor = new EventProcessor(aggregator);

		try (ProfilingEventSource source = new FileProfilingEventSource(logFile, parser)) {
			source.events().forEach(processor::process);
		}

		ReportPrinter.printSubtestSummary(aggregator);
		logger.info("Parsing finished {}", LocalDateTime.now());
	}

}
