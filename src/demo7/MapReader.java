package demo7;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Initial non-optimized version of the program
 * for reading a large map from a text file.
 *
 * This implementation allocates too many redundant objects.
 * Run async-profiler with `-e alloc` to find where
 * the most objects are allocated from.
 */
public class MapReader {

    public static void main(String[] args) throws Exception {
        long startTime = System.nanoTime();
        Map<String, Long> map = readMap("in.txt");
        long time = System.nanoTime() - startTime;

        System.out.printf("Read %d elements in %.3f seconds\n",
                map.size(), time / 1e9);
    }

    private static Map<String, Long> readMap(String fileName) throws IOException {
        Map<String, Long> map = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            for (String line; (line = br.readLine()) != null; ) {
                String[] kv = line.split(":", 2);
                String key = kv[0].trim();
                String value = kv[1].trim();
                map.put(key, Long.parseLong(value));
            }
        }

        return map;
    }
}
