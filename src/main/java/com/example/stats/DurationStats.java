package com.example.stats;

import java.time.Duration;

public class DurationStats {

    private long count = 0;
    private Duration total = Duration.ZERO;
    private Duration max = Duration.ZERO;

    public void add(Duration d) {
        count++;
        total = total.plus(d);
        if (d.compareTo(max) > 0) {
            max = d;
        }
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

}
