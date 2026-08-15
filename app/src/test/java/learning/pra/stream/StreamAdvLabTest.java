package learning.pra.stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

/**
 * StreamAdvLab Stream 进阶的单元测试（第十四课）。
 *
 * <p>覆盖 groupingBy 系列、partition 边界（==threshold 算达标）、flatMap/distinct、
 * joining、mapping/collectingAndThen、并行流一致性、自定义 Collector 均值。
 *
 * @see StreamAdvLab
 * @see Sale
 */
class StreamAdvLabTest {

    private final List<Sale> sales = List.of(
            new Sale("华东", "手机", 5000),
            new Sale("华东", "手机", 3000),
            new Sale("华东", "耳机", 800),
            new Sale("华南", "手机", 4000),
            new Sale("华南", "耳机", 600));

    @Test
    void countByRegion_groupsAndCounts() {
        Map<String, Long> result = StreamAdvLab.countByRegion(sales);
        assertEquals(3L, result.get("华东"));   // 华东 3 笔
        assertEquals(2L, result.get("华南"));   // 华南 2 笔
        assertEquals(2, result.size());
    }

    @Test
    void countByRegion_emptyList_returnsEmptyMap() {
        assertTrue(StreamAdvLab.countByRegion(List.of()).isEmpty());
    }

    @Test
    void sumByProduct_accumulatesRepeatedProduct() {
        Map<String, Integer> result = StreamAdvLab.sumByProduct(sales);
        assertEquals(12000, result.get("手机"));  // 华东5000+3000 + 华南4000
        assertEquals(1400, result.get("耳机"));   // 华东800 + 华南600
    }

    @Test
    void sumByProduct_emptyList_returnsEmptyMap() {
        assertTrue(StreamAdvLab.sumByProduct(List.of()).isEmpty());
    }

    @Test
    void partitionByAmount_splitsByThreshold() {
        Map<Boolean, List<Sale>> result =
                StreamAdvLab.partitionByAmount(sales, 1000);
        assertEquals(3, result.get(true).size());   // 5000/3000/4000 达标
        assertEquals(2, result.get(false).size());  // 800/600 未达标
    }

    @Test
    void partitionByAmount_boundaryEqualsThreshold_countsAsQualified() {
        List<Sale> edge = List.of(
                new Sale("华东", "手机", 1000),
                new Sale("华南", "耳机", 999));
        Map<Boolean, List<Sale>> result =
                StreamAdvLab.partitionByAmount(edge, 1000);
        assertEquals(1, result.get(true).size());   // 1000 == 1000 应算达标
        assertEquals(1, result.get(false).size());  // 999 未达标
    }

    @Test
    void flattenTags_mergesAllBoxTags() {
        List<TaggedBox> boxes = List.of(
                new TaggedBox("华东", List.of("手机", "耳机")),
                new TaggedBox("华南", List.of("平板", "手机")));
        assertEquals(List.of("手机", "耳机", "平板", "手机"),
                StreamAdvLab.flattenTags(boxes));
    }

    @Test
    void flattenTags_emptyList_returnsEmpty() {
        assertTrue(StreamAdvLab.flattenTags(List.of()).isEmpty());
    }

    @Test
    void distinctTags_removesDuplicatesAcrossBoxes() {
        List<TaggedBox> boxes = List.of(
                new TaggedBox("华东", List.of("手机", "耳机")),
                new TaggedBox("华南", List.of("平板", "手机")));
        assertEquals(List.of("手机", "耳机", "平板"),
                StreamAdvLab.distinctTags(boxes));
    }

    @Test
    void distinctTags_emptyList_returnsEmpty() {
        assertTrue(StreamAdvLab.distinctTags(List.of()).isEmpty());
    }

    @Test
    void joinProducts_joinsDistinctWithComma() {
        assertEquals("手机, 耳机, 平板",
                StreamAdvLab.joinProducts(salesWithDup()));
    }

    @Test
    void joinProducts_emptyList_returnsEmptyString() {
        assertEquals("", StreamAdvLab.joinProducts(List.of()));
    }

    @Test
    void productsByRegion_groupsProductNames() {
        Map<String, List<String>> result =
                StreamAdvLab.productsByRegion(salesWithDup());
        assertEquals(List.of("手机", "耳机"), result.get("华东"));
        assertEquals(List.of("平板", "手机"), result.get("华南"));
    }

    @Test
    void productsByRegion_emptyList_returnsEmptyMap() {
        assertTrue(StreamAdvLab.productsByRegion(List.of()).isEmpty());
    }

    @Test
    void totalAmountBuRegion_sumsPerRegionAsInteger() {
        Map<String, Integer> result =
                StreamAdvLab.totalAmountBuRegion(salesWithDup());
        assertEquals(1000, result.get("华东"));   // 700+300
        assertEquals(1500, result.get("华南"));   // 500+1000
        assertEquals(Integer.class, result.get("华东").getClass());
    }

    @Test
    void totalAmountBuRegion_emptyList_returnsEmptyMap() {
        assertTrue(StreamAdvLab.totalAmountBuRegion(List.of()).isEmpty());
    }

    private List<Sale> salesWithDup() {
        return List.of(
                new Sale("华东", "手机", 700),
                new Sale("华东", "耳机", 300),
                new Sale("华南", "平板", 500),
                new Sale("华南", "手机", 1000));
    }

    @Test
    void parallelAndSerialSum_agreeOnLargeList() {
        List<Integer> big = IntStream.rangeClosed(1, 10000).boxed().toList();
        int expected = 10000 * (10000 + 1) / 2;   // 1..10000 求和公式
        assertEquals(expected, StreamAdvLab.parallelSum(big));
        assertEquals(expected, StreamAdvLab.serialSum(big));
    }

    @Test
    void parallelSum_emptyList_returnsZero() {
        assertEquals(0, StreamAdvLab.parallelSum(List.of()));
        assertEquals(0, StreamAdvLab.serialSum(List.of()));
    }

    @Test
    void parallelSortedDesc_returnsStableDescending() {
        List<Integer> input = List.of(3, 1, 4, 1, 5, 9, 2, 6);
        assertEquals(List.of(9, 6, 5, 4, 3, 2, 1, 1),
                StreamAdvLab.parallelSortedDesc(input));
    }

    @Test
    void parallelSortedDesc_emptyList_returnsEmpty() {
        assertTrue(StreamAdvLab.parallelSortedDesc(List.of()).isEmpty());
    }

    @Test
    void averageCollector_averagesIntegers() {
        List<Integer> nums = List.of(2, 4, 6, 8);
        double avg = nums.stream().collect(StreamAdvLab.averageCollector());
        assertEquals(5.0, avg, 0.001);
    }

    @Test
    void averageCollector_emptyStream_returnsZero() {
        double avg = List.<Integer>of().stream().collect(StreamAdvLab.averageCollector());
        assertEquals(0.0, avg, 0.001);
    }

    @Test
    void averageCollector_nonIntegralAverage() {
        List<Integer> nums = List.of(1, 2);
        double avg = nums.stream().collect(StreamAdvLab.averageCollector());
        assertEquals(1.5, avg, 0.001);
    }
}
