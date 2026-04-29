package com.example.model;

public class Stats {
    private long count = 0;
    private long total = 0;
    private long max = 0;

    public void add(long durationMs) {
        count++;
        total += durationMs;
        max = Math.max(max, durationMs);
    }

    public long avg() {
        return count == 0 ? 0 : total / count;
    }

    public long max() {
        return max;
    }

    public long count() {
        return count;
    }

}
