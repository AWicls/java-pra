package learning.pra.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 虚拟线程入门练习（第十七课概念点 5）：每任务一个虚拟线程执行求和。
 *
 * <p>用 newVirtualThreadPerTaskExecutor 提交 N 个任务，
 * try-with-resources 自动关闭（等价 shutdown + 等待），验证万级任务也能跑。
 */
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
