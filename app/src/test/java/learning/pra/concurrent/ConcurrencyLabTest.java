package learning.pra.concurrent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * ConcurrencyLab 并发核心的单元测试（第五课 + 第十七课补强）。
 *
 * <p>覆盖线程命名、synchronized 精确 vs unsafe 丢更新、AtomicInteger、volatile 退出、
 * 生产者消费者、虚拟线程求和、CompletableFuture 补强（fallback / 超时 / 嵌套 vs 扁平）。
 *
 * @see ConcurrencyLab
 */
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

    @Test
    @DisplayName("补强3: withFallback 异常时返回降级值")
    void withFallback_异常时返回fallback() {
        ConcurrencyLab lab = new ConcurrencyLab();
        String result = lab.withFallback(
            () -> { throw new RuntimeException("boom"); },
            "价格暂不可用"
        );
        assertEquals("价格暂不可用", result, "异常时应返回 fallback");
    }

    @Test
    @DisplayName("补强3: withFallback 正常时返回原值")
    void withFallback_正常时返回原值() {
        ConcurrencyLab lab = new ConcurrencyLab();
        String result = lab.withFallback(() -> "￥99", "价格暂不可用");
        assertEquals("￥99", result, "正常时应返回原值，不走 fallback");
    }

    @Test
    @DisplayName("补强4: withTimeout 超时抛 CompletionException cause 为 TimeoutException")
    void withTimeout_超时抛CompletionException_且cause是TimeoutException() {
        CompletionException ex = assertThrows(
            CompletionException.class,
            () -> ConcurrencyLab.withTimeout(100)
        );
        assertTrue(ex.getCause() instanceof TimeoutException,
            "cause 应为 TimeoutException，实际是: " + ex.getCause());
    }

    @Test
    @DisplayName("补强5: composeFlat 用 thenCompose 链2次 扁平返回结果")
    void composeFlat_thenCompose链2次_扁平返回() {
        assertEquals(2, ConcurrencyLab.composeFlat(0).join(), "0 -> 1 -> 2");
        assertEquals(12, ConcurrencyLab.composeFlat(10).join(), "10 -> 11 -> 12");
    }

    @Test
    @DisplayName("补强5: applyNested 用 thenApply 链2次 返回3层嵌套类型")
    void applyNested_thenApply链2次_返回3层嵌套() {
        // 编译通过即证明返回类型是 CF<CF<CF<Integer>>>（3 层嵌套）
        // 取最终值要 join 三次，体现嵌套地狱代价
        CompletableFuture<CompletableFuture<CompletableFuture<Integer>>> nested =
            ConcurrencyLab.applyNested(0);
        int result = nested.join().join().join();
        assertEquals(2, result, "0 -> 1 -> 2，但要 join 三次才能拿到");
    }
}
