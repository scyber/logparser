package com.example.stats;

import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DurationStats {

    private static final Logger log = LoggerFactory.getLogger(DurationStats.class);

    // ToDo Threshold could be in a param command line for better way and
    // flexibility

    private final Duration treshHoldValue;

    public DurationStats(long thresholdValue) {
        this.treshHoldValue = Duration.ofMillis(thresholdValue);
    }

    private long count = 0;
    private Duration total = Duration.ZERO;
    private Duration max = Duration.ZERO;
    private Duration currentDeviation = Duration.ZERO;

    public Duration add(Duration currentDuration) {
        count++;
        total = total.plus(currentDuration);

        if (currentDuration.compareTo(max) > 0) {
            max = currentDuration;
        }

        currentDeviation = calculateDeviation(currentDuration);
        // ToDo compare to THRESHOLD
        if (currentDuration.compareTo(treshHoldValue) > 0) {
            log.warn(
                    "Duration deviation exceeded threshold: current(ms)={}, average(ms)={}, deviation(ms)={}, threshold(ms)={}",
                    currentDuration.toMillis(), getAverage().toMillis(), currentDeviation.toMillis(),
                    treshHoldValue.toMillis());
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
