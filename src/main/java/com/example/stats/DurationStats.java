package com.example.stats;

import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DurationStats {

    private static final Logger log = LoggerFactory.getLogger(DurationStats.class);
    
    //ToDo Threshold could be in a param command line for better way and flexibility
    private static final Duration THRESHOLD = Duration.ofMillis(300);

    private long count = 0;
    private Duration total = Duration.ZERO;
    private Duration max = Duration.ZERO;
    private Duration currentDeviation = Duration.ZERO;


    public Duration add(Duration d) {
        count++;
        total = total.plus(d);

        if (d.compareTo(max) > 0) {
            max = d;
        }

        currentDeviation = calculateDeviation(d);
        //ToDo compare to THRESHOLD
        if (currentDeviation.compareTo(THRESHOLD) > 0) {
            log.warn(
                "Duration deviation exceeded threshold: current(ms)={}, average(ms)={}, deviation(ms)={}, threshold(ms)={}",
                d.toMillis(), getAverage().toMillis(), currentDeviation.toMillis(), THRESHOLD.toMillis()
            );
        }

        return currentDeviation;
    }

    public long getCount() {
        return count;
    }

    public Duration getAverage() {
        return count == 0 ? Duration.ZERO : total.dividedBy(count);
    }

    public Duration getMax() {
        return max;
    }

    public Duration getCurrentDeviation() {
        return currentDeviation;
    }

    private Duration calculateDeviation(Duration d) {
        if (count < 2) {
            return Duration.ZERO;
        }

        Duration average = getAverage();
        Duration cDuration = d.abs();
        return cDuration.minus(average).abs();
    }
}
