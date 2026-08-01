package learning.pra.concurrent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
}
