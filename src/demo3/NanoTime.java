package demo3;

/**
 * This demo shows how a kernel-aware profiler may reveal subtle
 * performance issues not visible by other profilers.
 *
 * E.g. depending on the current Linux clock source, the performance of
 * System.nanoTime() may differ by the two orders of magnitude.
 *
 * Try running the test with two different clock sources:
 *   echo hpet > /sys/bus/clocksource/devices/clocksource0/current_clocksource
 *   echo tsc > /sys/bus/clocksource/devices/clocksource0/current_clocksource
 */
public class NanoTime {

    public static void main(String[] args) {
        while (true) {
            System.out.println(measureTimeCalls(1));
        }
    }

    private static long measureTimeCalls(int sec) {
        long deadline = System.nanoTime() + sec * 1000_000_000L;

        long iterations = 0;
        while (System.nanoTime() < deadline) {
            iterations++;
        }
        return iterations;
    }
}
