package demo12;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.LongStream;

/**
 * This example makes use of JFR compatible output.
 * Run with `-f out.jfr`
 *
 * The program generates a continuous load with
 * the peaks of CPU usage every 5 seconds.
 * The analysis of JFR output in Java Mission Control
 * shows the cause of the peaks.
 */
public class CacheTest {
    private static final int SIZE = 100_000;

    private final Cache cache = new Cache();

    public static void main(String[] args) throws Exception {
        CacheTest test = new CacheTest();

        while (true) {
            test.run();
            Thread.sleep(20);
        }
    }

    public CacheTest() {
        for (long id = 0; id < SIZE; id++) {
            cache.put(id, Long.toString(ThreadLocalRandom.current().nextLong()));
        }
    }

    private void run() {
        long count = LongStream.range(0, SIZE)
                .mapToObj(cache::get)
                .filter(Objects::nonNull)
                .count();
    }
}
