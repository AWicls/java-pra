package learning.pra.stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class StreamLabTest {

    // ========== sumOfEvenSquares：重点测（含 bug）==========

    @Test
    @DisplayName("sumOfEvenSquares_偶数平方和")
    void sumOfEvenSquares_正常() {
        // 2^2 + 4^2 + 6^2 = 4 + 16 + 36 = 56
        assertEquals(56, StreamLab.sumOfEvenSquares(List.of(1, 2, 3, 4, 5, 6)));
    }

    @Test
    @DisplayName("sumOfEvenSquares_空列表返回0")
    void sumOfEvenSquares_空列表() {
        assertEquals(0, StreamLab.sumOfEvenSquares(List.of()));
    }

    @Test
    @DisplayName("sumOfEvenSquares_全奇数返回0")
    void sumOfEvenSquares_全奇数() {
        assertEquals(0, StreamLab.sumOfEvenSquares(List.of(1, 3, 5)));
    }

    // ========== groupByLength：重点测 ==========

    @Test
    @DisplayName("groupByLength_按长度分组")
    void groupByLength_正常() {
        Map<Integer, List<String>> result = StreamLab.groupByLength(List.of("a", "bb", "cc", "ddd"));
        assertEquals(List.of("a"), result.get(1));
        assertEquals(List.of("bb", "cc"), result.get(2));
        assertEquals(List.of("ddd"), result.get(3));
    }

    @Test
    @DisplayName("groupByLength_空列表返回空Map")
    void groupByLength_空列表() {
        Map<Integer, List<String>> result = StreamLab.groupByLength(List.of());
        assertTrue(result.isEmpty());
    }

    // ========== wordFrequency：重点测 ==========

    @Test
    @DisplayName("wordFrequency_统计单词出现次数")
    void wordFrequency_正常() {
        Map<String, Long> result = StreamLab.wordFrequency(List.of("apple", "banana", "apple"));
        assertEquals(2L, result.get("apple"));
        assertEquals(1L, result.get("banana"));
    }

    @Test
    @DisplayName("wordFrequency_空列表返回空Map")
    void wordFrequency_空列表() {
        Map<String, Long> result = StreamLab.wordFrequency(List.of());
        assertTrue(result.isEmpty());
    }

    // ========== upperCaseDistinct：简单验证（含 bug）==========

    @Test
    @DisplayName("upperCaseDistinct_转大写去重")
    void upperCaseDistinct_正常() {
        List<String> result = StreamLab.upperCaseDistinct(List.of("a", "b", "A", "a"));
        assertIterableEquals(List.of("A", "B"), result);
    }

    @Test
    @DisplayName("upperCaseDistinct_返回不可变List")
    void upperCaseDistinct_不可变() {
        List<String> result = StreamLab.upperCaseDistinct(List.of("a"));
        assertThrows(UnsupportedOperationException.class, () -> result.add("X"));
    }

    // ========== findFirstGreaterThanTen：简单验证 ==========

    @Test
    @DisplayName("findFirstGreaterThanTen_找到第一个大于10的")
    void findFirstGreaterThanTen_找到() {
        assertEquals(15, StreamLab.findFirstGreaterThanTen(List.of(5, 15, 20)));
    }

    @Test
    @DisplayName("findFirstGreaterThanTen_没有则返回-1")
    void findFirstGreaterThanTen_没有() {
        assertEquals(-1, StreamLab.findFirstGreaterThanTen(List.of(1, 2, 3)));
    }
}
