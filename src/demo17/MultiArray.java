package demo17;

import org.openjdk.jmh.annotations.Benchmark;

/**
 * JMH has built-in support for async-profiler.
 * Run jmh benchmark with `-prof async:output=flamegraph`
 *
 * The profile explains why allocation of a multidimentional array
 * is slower than allocation of an equivalent array of arrays.
 * This would not be obvious at all without native stack traces.
 * See https://stackoverflow.com/q/58158445/3448419 for details.
 */
public class MultiArray {
    static int rows = 10000;
    static int cols = 10;

    @Benchmark
    public int[][] newArray() {
        return new int[rows][cols];
    }

    @Benchmark
    public int[][] newLoop() {
        int[][] temps = new int[rows][];
        for (int i = 0; i < temps.length; i++) {
            temps[i] = new int[cols];
        }
        return temps;
    }
}
