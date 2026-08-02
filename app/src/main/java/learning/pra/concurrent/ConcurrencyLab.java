package learning.pra.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class ConcurrencyLab {

    // 持有状态字段
    private int unsafeCount = 0;
    private int safeCount = 0;
    private final Object lock = new Object();

    private int total() {
        return unsafeCount + safeCount;
    }

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
            synchronized (lock) {
                safeCount++;
            }
        });
    }

    // 辅助方法：消除 unsafe/safe 的重复代码
    private int runWithFourThreads(int times, Runnable increment) {
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Thread t = new Thread(() -> {
                for (int j = 0; j < times; j++)
                    increment.run();
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
    private final AtomicInteger atomicCount = new AtomicInteger(0); // 怎么初始化？

    public int atomicIncrement(int times) {
        atomicCount.set(0);
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Thread t = new Thread(() -> {
                for (int j = 0; j < times; j++)
                    atomicCount.incrementAndGet();
            }, "atom-" + i);
            threads.add(t);
            t.start();
        }
        threads.forEach(ConcurrencyLab::joinQuietly);
        return atomicCount.get();
    }

    private volatile boolean running = true; // volatile 保证可见性
    private int loopCount = 0; // 不需要 volatile（只在工作线程改）

    public int volatileDemo() {
        running = true;
        loopCount = 0;

        Thread worker = new Thread(() -> {
            while (running) { // ??? 检查 running 标志
                loopCount++; // 不需要原子（单线程改）
                try {
                    Thread.sleep(100); // ← 加 sleep 防溢出
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }, "worker");

        worker.start();

        // ??? 主线程 sleep 一会儿（让 worker 跑几下）
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }

        // ??? 然后把 running 设为 false
        running = false;

        // ??? join 等待 worker 退出
        joinQuietly(worker);

        return loopCount;
    }

    public int producerConsumer() {
        ArrayBlockingQueue<Integer> queue = new ArrayBlockingQueue<>(5);

        int[] sum = new int[1];
        // 生产者
        Thread producer = new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    queue.put(i);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    int value = queue.take();
                    sum[0] += value;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        consumer.start();
        joinQuietly(producer);
        joinQuietly(consumer);

        return sum[0];
    }

    public int virtualThreadSum(int n) throws InterruptedException, ExecutionException {
        // ??? 创建 newVirtualThreadPerTaskExecutor
        // ??? 创建 n 个任务,每个 sleep 1ms 后返回编号 i
        // ??? 收集 Future,用 get() 取结果
        // ??? try-with-resources 自动等全部完成
        // ??? 返回 0+1+...+(n-1) = n*(n-1)/2

        int sum = 0;

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();) {
            List<Future<Integer>> futures = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                final int index = i;
                Future<Integer> future = executor.submit(() -> {
                    Thread.sleep(1);
                    return index;
                });
                futures.add(future);
            }
            for (Future<Integer> future : futures) {
                sum += future.get();
            }
        }

        return sum;

    }

    public int asyncSum(int a, int b) {
        CompletableFuture<Integer> cfA = CompletableFuture.supplyAsync(() -> a);
        CompletableFuture<Integer> cfB = CompletableFuture.supplyAsync(() -> b);
        CompletableFuture<Integer> sum = cfA.thenCombine(cfB, (x, y) -> x + y);
        return sum.join();
    }

    public List<String> gatherAll(List<Supplier<String>> suppliers) {
        List<CompletableFuture<String>> futures = new ArrayList<>();
        for (Supplier<String> supplier : suppliers) {
            futures.add(CompletableFuture.supplyAsync(supplier));
        }

        CompletableFuture<Void> all = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        all.join();

        List<String> results = new ArrayList<>();

        for (CompletableFuture<String> future : futures) {
            results.add(future.join());
        }

        return results;
    }

    public String withFallback(Supplier<String> failing, String fallback) {
        CompletableFuture<String> cf = CompletableFuture.supplyAsync(failing);
        CompletableFuture<String> safe = cf.exceptionally(ex -> fallback);
        return safe.join();
    }

    public static String withTimeout(long millis) {
        CompletableFuture<String> cf = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(millis * 5);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
            return "done";
        });
        CompletableFuture<String> timed = cf.orTimeout(millis, TimeUnit.MILLISECONDS);
        return timed.join();
    }

    private static CompletableFuture<Integer> next(int x) {
        return CompletableFuture.supplyAsync(() -> x + 1);
    }

    public static CompletableFuture<CompletableFuture<CompletableFuture<Integer>>> applyNested(int n) {
        CompletableFuture<Integer> cf0 = CompletableFuture.supplyAsync(() -> n);
        CompletableFuture<CompletableFuture<Integer>> cf1 = cf0.thenApply(x -> next(x)); // x -> next(x)
        CompletableFuture<CompletableFuture<CompletableFuture<Integer>>> cf2 = cf1.thenApply(x -> x.thenApply(y -> next(y)));
        return cf2;
    }

    // 5b: 扁平化（用 thenCompose）
    public static CompletableFuture<Integer> composeFlat(int n) {
        CompletableFuture<Integer> cf0 = CompletableFuture.supplyAsync(() -> n);
        CompletableFuture<Integer> cf1 = cf0.thenCompose(x -> next(x)); // x -> next(x)
        CompletableFuture<Integer> cf2 = cf1.thenCompose(x -> next(x));
        return cf2;
    }

}
