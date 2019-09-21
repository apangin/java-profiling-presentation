package demo3;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * This code demonstrates the importance of the kernel-level profiling.
 * The performance of FileInputStream.read() can drastically differ
 * depending on the array size. For example, reading the file with a
 * 32MB buffer is a lot slower than with a 31MB buffer.
 * Only kernel-aware profilers will show where the problem is.
 *
 * By default, the standard allocator in Linux calls mmap() directly
 * when allocating chunks >= 32MB. async-profiler will show many
 * page faults in this case.
 */
public class FileReader {

    public void readFile(String fileName, int bufSize) throws IOException {
        byte[] buf = new byte[bufSize];

        try (FileInputStream in = new FileInputStream(fileName)) {
            int bytesRead;
            while ((bytesRead = in.read(buf)) > 0) {
                Blackhole.consume(buf, bytesRead);
            }
        }
    }

    private static long benchmark(String fileName, int bufSize, int count) throws IOException {
        FileReader reader = new FileReader();
        long startTime = System.nanoTime();

        for (int i = 0; i < count; i++) {
            reader.readFile(fileName, bufSize);
        }

        long endTime = System.nanoTime();
        return endTime - startTime;
    }

    public static void main(String[] args) throws IOException {
        String fileName = "in.txt";
        int bufSize = Integer.parseInt(args[0]) * 1024;

        benchmark(fileName, bufSize, 20);
        long time = benchmark(fileName, bufSize, 100);

        System.out.println("Elapsed time: " + (time / 1e9) + " s");
    }
}
