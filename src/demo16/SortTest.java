package demo16;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This is a simplified example from https://stackoverflow.com/q/38354595/3448419
 * It demonstrates long Time-to-Safepoint pauses due to counted loops.
 *
 * `--begin/--end` options can be used to automatically start/stop profiling
 * upon execution of the specified functions. In this demo, async-profiler
 * helps to find a reason of long TTSP pauses.
 * See https://github.com/jvm-profiling-tools/async-profiler/issues/74
 *
 * Run with
 *   -t
 *   --begin SafepointSynchronize::begin
 *   --end RuntimeService::record_safepoint_synchronized
 */
public class SortTest {

    private static void swap(int[] ar, int from, int to) {
        int old = ar[from];
        ar[from] = ar[to];
        ar[to] = old;
    }

    private static void insertionSort(int[] ar) {
        for (int i = 0; i < ar.length; i++) {
            int j = i;
            while (j >= 1 && ar[j] < ar[j - 1]) {
                swap(ar, j - 1, j);
                j--;
            }
        }
    }

    public static void bubbleSort(int[] array) {
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array.length - 1; j++) {
                if (array[j] > array[j + 1]) {
                    swap(array, j + 1, j);
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        int[] array1 = new Random(0).ints(100_000).toArray();
        int[] array2 = new Random(0).ints(100_000).toArray();
        int[] array3 = new Random(0).ints(100_000).toArray();

        ExecutorService executor = Executors.newCachedThreadPool();
        executor.execute(() -> insertionSort(array1));
        executor.execute(() -> bubbleSort(array2));
        executor.execute(() -> Arrays.sort(array3));
        executor.shutdown();

        int i = 0;
        do {
            System.out.println(i++);
        } while (!executor.awaitTermination(100, TimeUnit.MILLISECONDS));
    }
}
