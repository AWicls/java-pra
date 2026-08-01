package learning.pra.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ConcurrencyLab {


    // 持有状态字段
    private int unsafeCount = 0;
    private int safeCount = 0;
    private final Object lock = new Object();

    private int total() { return unsafeCount + safeCount; }

    public static String threadStart(String threadName) {
        String[] name = new String[1];
        Thread t = new Thread(() -> name[0] = Thread.currentThread().getName(), threadName);
        t.start();
        joinQuietly(t);
        return name[0];
    }

    public int unsafeIncrement(int times) {
        unsafeCount = 0;
        return runWithFourThreads(times, () -> unsafeCount++);
    }

    public int safeIncrement(int times) {
        safeCount = 0;
        return runWithFourThreads(times, () -> {
            synchronized (lock) { safeCount++; }
        });
    }

    // 辅助方法：消除 unsafe/safe 的重复代码
    private int runWithFourThreads(int times, Runnable increment) {
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Thread t = new Thread(() -> {
                for (int j = 0; j < times; j++) increment.run();
            }, "worker-" + i);
            threads.add(t);
            t.start();
        }
        threads.forEach(ConcurrencyLab::joinQuietly);
        return total();
    }

    // 辅助方法：消除 join + 异常处理样板
    private static void joinQuietly(Thread t) {
        try {
            t.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    // 字段
private final AtomicInteger atomicCount = new AtomicInteger(0);   // 怎么初始化？

public int atomicIncrement(int times) {
    atomicCount.set(0);
    List<Thread> threads = new ArrayList<>();
    for (int i = 0; i < 4; i++) {
        Thread t = new Thread(() -> {
            for (int j = 0; j < times; j++) atomicCount.incrementAndGet();
        }, "atom-" + i);
        threads.add(t);
        t.start();
    }
    threads.forEach(ConcurrencyLab::joinQuietly);
    return atomicCount.get();
}

}
