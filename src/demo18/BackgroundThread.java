package demo18;

import java.time.LocalDateTime;
import java.util.concurrent.locks.LockSupport;

public class BackgroundThread extends Thread {
    static volatile int x;

    public BackgroundThread() {
        setDaemon(true);
    }

    @Override
    public void run() {
        while (true) {
            x = LocalDateTime.now().getNano();
            LockSupport.parkNanos(1000000);
        }
    }
}
