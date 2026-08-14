package learning.pra.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class VirtualThreadLab {

    public static int sumResults(int taskCount) throws InterruptedException, ExecutionException {
        int sum = 0;
        try(ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor()){
            List<Future<Integer>> futures = new ArrayList<>();
            for (int i = 0; i < taskCount; i++) {
                final int index = i;
                futures.add(pool.submit(() -> index));
            }
            for (Future<Integer> future : futures) {
                sum += future.get();
            }
        }
        return sum;
    }

}
