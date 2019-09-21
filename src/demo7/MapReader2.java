package demo7;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * MapReader optimization:
 * rewrite String.split() to avoid redundant allocations.
 */
public class MapReader2 {

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
                int sep = line.indexOf(':');
                String key = line.substring(0, sep).trim();
                String value = line.substring(sep + 1).trim();
                map.put(key, Long.parseLong(value));
            }
        }

        return map;
    }
}
