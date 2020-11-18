# Presentation materials

## Improving Performance with async-profiler

The profiler is your best friend in finding performance bottlenecks.
Although there are a lot of profilers available, even popular tools can be misleading.
Furthermore, performance problems are sometimes hidden deep inside JVM or even the OS kernel.
async-profiler is a modern open source tool that enhances the Java profiling experience
by utilizing JVM internal structures, OS capabilities, and hardware performance counters.
In this live demo session, you will see how async-profiler helps you discover
subtle performance issues and native memory leaks in a Java application.

## Slides

[java-profiling.pdf](presentation/java-profiling.pdf)

## Code samples

### demo1

This demo shows that most sampling profilers are misleading.
The given program appends 5 symbols to the end of StringBuilder
and deletes 5 symbols from the beginning of StringBuilder.

The real bottleneck here is `delete()`, since it involves moving
of 1 million characters. However, safepoint-based profilers
will display `Thread.isAlive()` as the hottest method.
JFR will not report anything useful at all, since it cannot
traverse stack traces when JVM is running `System.arraycopy()`.

### demo2

This test starts two similar clients. The code of the clients
is the same, except that `BusyClient` constantly receives new data,
while `IdleClient` does almost nothing but waiting for incoming data.

Most sampling profilers will not make difference between
`BusyClient` and `IdleClient`, because JVM does not know whether
a native method consumes CPU or just waits inside a blocking call.

### demo3

This code demonstrates the importance of the kernel-level profiling.

The performance of `FileInputStream.read()` can drastically differ
depending on the array size. For example, reading the file with a
32MB buffer is a lot slower than with a 31MB buffer.
Only kernel-aware profilers will show where the problem is.

Another example shows that depending on the current Linux clock source,
the performance of `System.nanoTime()` may differ by the two orders
of magnitude.

### demo4

In this test, a large amount of time is spent inside the vtable stub
and inside the method prologue/epilogue.

Most sampling profilers, including AsyncGetCallTrace-based,
fail to traverse Java stack in these cases.
See [JDK-8178287](https://bugs.openjdk.java.net/browse/JDK-8178287).

### demo5

This demo shows how to start async-profiler as a JVM TI agent at JVM boot time.

Here is the [list of options](https://github.com/jvm-profiling-tools/async-profiler/blob/master/src/arguments.cpp) to use with `-agentpath`.

### demo6

This test sends UDP datagrams in 10 threads simultaneously
and calculates the total throughput (packets/s).

The default `DatagramChannel` in Java NIO demonstrates poor performance
because of the synchronized block inside `send()` method.

Use async-profiler's lock profiling mode `-e lock`
to find the source of lock contention.

### demo7

Redundant memory allocation is the common source of performance problems.
In this demo we improve the performance of a sample application
with the help of allocation profiler: `-e alloc`.

### demo8

The demonstration of async-profiler Java API.

 - `AsyncProfiler.getInstance()`
 - `start()` / `stop()`
 - `dumpTraces()`
 - `execute()`
 - selective profiling with `resume()` / `stop()`

### demo9

This demo shows the importance of hardware performance counters.
Two tests (`test128K` and `test8M`) execute the same number of
operations, however, `test128K` completes much quicker than `test8M`.

CPU profiling shows no difference between two tests,
but cache-misses profiling highlights `test8M`
as the problematic method.

### demo10

This test demonstrates a native memory leak caused by
unclosed `InputStream`.
Async-profiler's ability to profile native functions
(`-e malloc` and `-e mprotect`)
helps to track the memory leak down to the origin
in Java code, i.e. `getResourceAsStream()`.

Async-profiler can also profile Linux kernel tracepoints
and show the mixed Java + native + kernel stack traces.

In this test, we discover the page cache leak
by profiling `-e filemap:mm_filemap_add_to_page_cache`

Misconfigured log rotation results in a constantly growing
log file accompanied by a constantly growing page cache.

### demo11

This example shows how async-profiler helps to find the origin
of an exception when no exception stack trace is printed.

Run profiler with `-e Java_java_lang_Throwable_fillInStackTrace`

### demo12

This example makes use of JFR compatible output.
Run with `-f out.jfr`

The program generates a continuous load with
the peaks of CPU usage every 5 seconds.
The analysis of JFR output in Java Mission Control
shows the cause of the peaks.

### demo13

This is an example of safepoint profiling.
Async-profiler displays actions that trigger a global safepoint,
including

 - Biased Lock revocation
 - Deoptimization
 - GC
