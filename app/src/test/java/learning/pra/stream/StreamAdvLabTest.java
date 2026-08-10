package learning.pra.stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

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
}
