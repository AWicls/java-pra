package learning.pra.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ExecutorPoolLab {

    public static int countDistinctWorkers(int taskCount, int poolSize) throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(poolSize);

        Set<String> workers = ConcurrentHashMap.newKeySet();

        for (int i = 0; i < taskCount; i++) {
                pool.submit(() ->{
                    try {
                        Thread.sleep(10);
                    }catch (InterruptedException e){
                        Thread.currentThread().interrupt();
                    }
                    workers.add(Thread.currentThread().getName());

                    return 1;
                });
        }

        pool.shutdown();
        pool.awaitTermination(5, TimeUnit.SECONDS);

        return workers.size();

    }

    public static int sumResults(int taskCount, int poolSize) throws InterruptedException, ExecutionException {
        ExecutorService pool = Executors.newFixedThreadPool(poolSize);
        List<Future<Integer>> list = new ArrayList<>();

        for (int i = 0; i < taskCount; i++) {
            final int index = i;
            list.add(pool.submit(() -> index));
        }

        int sum = 0;
        for (Future<Integer> future : list) {
            sum += future.get();
        }

        pool.shutdown();
        return sum;
    }

}
