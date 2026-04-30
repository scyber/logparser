package com.example.reporting;

import java.util.Map;
import com.example.processing.StepAggregator;
import com.example.stats.DurationStats;

public class ReportPrinter {

    public static void printSubtestSummary(StepAggregator aggregator) {
    	System.out.println("SUBTEST PERFORMANCE SUMMARY");
    	System.out.println("------------------------------------------------");
    	System.out.printf("%-30s %8s %10s %10s%n",
        	"Subtest", "Count", "Avg(ms)", "Max(ms)");

    	for (Map.Entry<String, DurationStats> e : aggregator.getStats().entrySet()) {
        	DurationStats s = e.getValue();
        	System.out.printf(
            	"%-30s %8d %10d %10d%n",
            	e.getKey(),
            	s.getCount(),
            	s.getAverage().abs().toMillis(),
            	s.getMax().toMillis()
				
        	);
    	}
	}

}
