package learning.pra.concurrent;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExecutorPoolLabTest {

    @Test
    void 固定线程池复用线程() throws InterruptedException {
        int workers = ExecutorPoolLab.countDistinctWorkers(20, 4);
        assertTrue(workers > 0);
        assertTrue(workers <= 4, "20 个任务应只用到 4 个线程，实际出现: " + workers);
    }

    @Test
    void 提交任务结果求和正确() throws Exception {
        assertEquals(190, ExecutorPoolLab.sumResults(20, 4));   // 20*19/2
    }
}
