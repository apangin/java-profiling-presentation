package demo18;

/**
 * Using async-profiler to trace voluntary context switches.
 * Based on https://stackoverflow.com/q/64392845/3448419
 *
 * -I/-X options are essential for displaying only events
 * we are interested in.
 *
 * Profiler options:
 *   -d 60                        // duration
 *   -e context-switches          // an event to profile
 *   -i 2                         // interval = 2 events
 *   -t                           // split profile by threads
 *   -f context-switches.html     // output file name
 *   -I 'main*'                   // include only main thread
 *   -X 'exit_to_usermode_loop*'  // exclude nonvoluntary context switches
 */
public class ContextSwitches {

    public static void main(final String[] args) {
        new BackgroundThread().start();

        int delay = Integer.getInteger("delay", 60);

        long start = TimeUtils.now();
        System.out.println("Started at " + TimeUtils.toSeconds(start));

        // Busy loop
        long now = start;
        while (now < start + TimeUtils.NANOS_IN_SECOND * delay) {
            now = TimeUtils.now();
        }

        long stop = TimeUtils.now();
        System.out.println("Stopped at " + TimeUtils.toSeconds(stop));
    }
}
