package com.perfscope.util;

public class Duration {

    private static final long MICROSECOND_THRESHOLD = 1000;
    private static final long MILLISECOND_THRESHOLD = MICROSECOND_THRESHOLD * 1000;
    private static final long SECONDS_THRESHOLD = MILLISECOND_THRESHOLD * 1000;

    private final long nanos;

    private Duration(long nanos) {
        this.nanos = nanos;
    }

    public static Duration ofNanos(long nanos) {
        return new Duration(nanos);
    }

    public static String format(long nanos) {
        if (nanos >= SECONDS_THRESHOLD) return formatSize(nanos, SECONDS_THRESHOLD, "s");
        if (nanos >= MILLISECOND_THRESHOLD) return formatSize(nanos, MILLISECOND_THRESHOLD, "ms");
        if (nanos >= MICROSECOND_THRESHOLD) return formatSize(nanos, MICROSECOND_THRESHOLD, "us");
        return formatSize(nanos, 1, "ns");
    }

    private static String formatSize(long size, long divider, String unitName) {
        return Math.round((double) size / divider) + " " + unitName;
    }

    @Override
    public String toString() {
        return format(nanos);
    }
}
