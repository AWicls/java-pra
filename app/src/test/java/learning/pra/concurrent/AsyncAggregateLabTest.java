package learning.pra.concurrent;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AsyncAggregateLab 并发聚合的单元测试（第十七课概念点 3）。
 *
 * <p>验证聚合结果格式（"用户1|商品2|物流3"）+ 并发聚合耗时远小于串行和。
 *
 * @see AsyncAggregateLab
 */
class AsyncAggregateLabTest {

    @Test
    void 聚合结果格式正确() {
        assertEquals("用户1|商品2|物流3", AsyncAggregateLab.aggregate(1, 2, 3));
    }

    @Test
    void 并发聚合耗时远小于串行和() {
        long start = System.currentTimeMillis();
        AsyncAggregateLab.aggregate(1, 2, 3);
        long elapsed = System.currentTimeMillis() - start;
        assertTrue(elapsed < 550,
                "串行应约600ms，并发应约300ms，实际: " + elapsed + "ms");
    }
}
