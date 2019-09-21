package demo6;

import one.nio.net.Socket;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This test sends UDP datagrams in 10 threads simultaneously
 * and calculates the total throughput (packets/s).
 *
 * one-nio (https://github.com/odnoklassniki/one-nio) sockets
 * outperform the standard DatagramChannel, because there is no
 * Java-level synchronization inside.
 */
public class OneNioTest {
    private static final AtomicLong totalPackets = new AtomicLong();
    private static Socket s;

    public static void sendLoop() {
        final ByteBuffer buf = ByteBuffer.allocateDirect(1000);
        final InetSocketAddress remoteAddr = new InetSocketAddress("127.0.0.1", 5556);

        try {
            while (true) {
                buf.clear();
                s.send(buf, 0, remoteAddr.getAddress(), remoteAddr.getPort());
                totalPackets.incrementAndGet();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        s = Socket.createDatagramSocket();
        s.bind("127.0.0.1", 5555, 128);
        s.setBlocking(false);

        Executor pool = Executors.newCachedThreadPool();
        for (int i = 0; i < 10; i++) {
            pool.execute(OneNioTest::sendLoop);
        }

        System.out.println("Warming up...");
        Thread.sleep(3000);
        totalPackets.set(0);

        System.out.println("Benchmarking...");
        Thread.sleep(5000);
        System.out.println(totalPackets.get() / 5);

        System.exit(0);
    }
}
