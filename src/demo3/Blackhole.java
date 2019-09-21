package demo3;

public class Blackhole {
    static volatile byte b;
    static volatile double d;

    public static void consume(byte[] buf, int bytesRead) {
        Blackhole.b = buf[bytesRead - 1];
    }

    public static void consume(double d) {
        Blackhole.d = d;
    }
}
