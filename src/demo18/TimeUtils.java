package demo18;

import java.lang.management.ManagementFactory;

public class TimeUtils {
    public static final long NANOS_IN_SECOND = 1_000_000_000L;

    private static final long startTime =
            System.nanoTime() - ManagementFactory.getRuntimeMXBean().getUptime() * 1_000_000L;

    public static long now() {
        return System.nanoTime() - startTime;
    }

    public static String toSeconds(long timestamp) {
        return String.format("%.5f", (double) timestamp / NANOS_IN_SECOND);
    }
}
