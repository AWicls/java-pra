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
}
