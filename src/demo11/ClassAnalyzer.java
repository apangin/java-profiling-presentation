package demo11;

import one.classpath.ClassPathScanner;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This example shows how async-profiler helps to find the origin
 * of an exception when no exception stack trace is printed.
 *
 * Run profiler with `-e Java_java_lang_Throwable_fillInStackTrace`
 */
public class ClassAnalyzer implements Runnable {
    private final ClassPathScanner scanner;

    public ClassAnalyzer(ClassPathScanner scanner) {
        this.scanner = scanner;
    }

    public void run() {
        try {
            long count = scanner.stream().count();
            System.out.println("Processed " + count + " classes");
        } catch (Exception e) {
            System.err.println("Exception in ClassAnalyzer: " + e);
        }
    }

    public static void main(String[] args) throws Exception {
        ClassPathScanner scanner = new ClassPathScanner();
        scanner.scanBootClassPath();

        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(
                new ClassAnalyzer(scanner),
                1, 1, TimeUnit.SECONDS);
    }
}
