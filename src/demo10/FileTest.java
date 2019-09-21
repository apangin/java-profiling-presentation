package demo10;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Async-profiler can profile Linux kernel tracepoints
 * and show the mixed Java + native + kernel stack traces.
 *
 * In this test, we discover the page cache leak
 * by profiling `-e filemap:mm_filemap_add_to_page_cache`
 *
 * Misconfigured log rotation results in a constantly growing
 * log file accompanied by a constantly growing page cache.
 */
public class FileTest {
    private final Log log = LogFactory.getLog(getClass());

    public static void main(String[] args) {
        new FileTest().run();
    }

    public void run() {
        while (true) {
            try {
                readFile("/tmp/somefile.txt");
            } catch (Exception e) {
                log.error("Something goes wrong", e);
            }
        }
    }

    private long readFile(String fileName) throws IOException  {
        Path file = Paths.get(fileName);
        try (Stream<String> lines = Files.lines(file)) {
            return lines.count();
        }
    }
}
