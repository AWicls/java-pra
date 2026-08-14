package learning.pra.concurrent;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VirtualThreadLabTest {

    @Test
    void 虚拟线程执行一万个任务求和正确() throws Exception {
        assertEquals(49_995_000, VirtualThreadLab.sumResults(10_000));   // 10000*9999/2
    }
}
