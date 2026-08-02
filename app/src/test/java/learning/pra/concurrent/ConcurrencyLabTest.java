package learning.pra.concurrent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ConcurrencyLabTest {

    @Test
    @DisplayName("任务1: threadStart 返回新线程的名字")
    void threadStart_返回新线程名字() {
        String name = ConcurrencyLab.threadStart("lab-thread");
        assertEquals("lab-thread", name);
    }

    @Test
    @DisplayName("任务2: safeIncrement 4线程各加100000次 结果应为400000")
    void safeIncrement_多线程_结果正确() throws InterruptedException {
        ConcurrencyLab lab = new ConcurrencyLab();
        int result = lab.safeIncrement(100000);
        assertEquals(400000, result, "synchronized 保护下应得到精确结果");
    }

    @Test
    @DisplayName("任务2: unsafeIncrement 4线程各加100000次 结果大概率丢更新")
    void unsafeIncrement_多线程_会丢更新() throws InterruptedException {
        ConcurrencyLab lab = new ConcurrencyLab();
        int result = lab.unsafeIncrement(100000);
        System.out.println("unsafe 结果 = " + result + " (期望 400000，大概率小于)");
        assertTrue(result <= 400000, "最多等于 400000，丢更新会小于");
    }

    @Test
    @DisplayName("任务3: atomicIncrement 4线程各加100000次 结果应为400000")
    void atomicIncrement_多线程_结果正确() throws InterruptedException {
        ConcurrencyLab lab = new ConcurrencyLab();
        int result = lab.atomicIncrement(100000);
        assertEquals(400000, result, "AtomicInteger 应得到精确结果");
    }

    @Test
    @DisplayName("任务4: volatileDemo worker 应能正常退出并返回循环次数")
    void volatileDemo_worker正常退出() throws InterruptedException {
        ConcurrencyLab lab = new ConcurrencyLab();
        int result = lab.volatileDemo();
        System.out.println("volatile loopCount = " + result);
        assertTrue(result > 0, "worker 应跑了至少 1 次");
    }

    @Test
    @DisplayName("任务5: producerConsumer 生产0-9消费求和 应返回45")
    void producerConsumer_求和为45() {
        ConcurrencyLab lab = new ConcurrencyLab();
        int result = lab.producerConsumer();
        assertEquals(45, result, "0+1+...+9 = 45");
    }

    @Test
    @DisplayName("任务6: virtualThreadSum 100个虚拟线程求和 应返回4950")
    void virtualThreadSum_100个虚拟线程_应返回4950() throws Exception {
        ConcurrencyLab lab = new ConcurrencyLab();
        int result = lab.virtualThreadSum(100);
        assertEquals(4950, result, "0+1+...+99 = 4950");
    }

    @Test
    @DisplayName("补强1: asyncSum 两个异步任务求和")
    void asyncSum_两个异步求和() {
        ConcurrencyLab lab = new ConcurrencyLab();
        assertEquals(8, lab.asyncSum(3, 5), "3 + 5 = 8");
        assertEquals(0, lab.asyncSum(0, 0), "0 + 0 = 0");
        assertEquals(-1, lab.asyncSum(2, -3), "2 + (-3) = -1");
    }

    @Test
    @DisplayName("补强2: gatherAll 多个Supplier按输入顺序返回结果")
    void gatherAll_按顺序返回结果() {
        ConcurrencyLab lab = new ConcurrencyLab();
        List<String> result = lab.gatherAll(List.of(
            () -> "x",
            () -> "y",
            () -> "z"
        ));
        assertEquals(List.of("x", "y", "z"), result, "顺序必须与输入一致");
    }

    @Test
    @DisplayName("补强2: gatherAll 空列表返回空列表")
    void gatherAll_空列表() {
        ConcurrencyLab lab = new ConcurrencyLab();
        List<String> result = lab.gatherAll(List.of());
        assertTrue(result.isEmpty(), "空输入应返回空列表");
    }
}
