package learning.pra.collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ListLabTest {

    @Test
    @DisplayName("dedupKeepOrderTest_1: 去重，保留首次出现的顺序。例 [1,2,2,3,1] -> [1,2,3]")
    void dedupKeepOrderTest_1() {
        List<Integer> input = List.of(1,2,2,3,1);
        List<Integer> result = ListLab.dedupKeepOrder(input);
        assertIterableEquals(List.of(1,2,3), result);
    }

    @Test
    @DisplayName("dedupKeepOrderTest_2: 空列表返回空列表")
    void dedupKeepOrderTest_2() {
        List<Integer> input = List.of();
        List<Integer> result = ListLab.dedupKeepOrder(input);
        assertIterableEquals(List.of(), result);
    }

    @Test
    @DisplayName("dedupKeepOrderTest_3: 全部相同")
    void dedupKeepOrderTest_3() {
        List<Integer> input = List.of(1,1,1,1,1);
        List<Integer> result = ListLab.dedupKeepOrder(input);
        assertIterableEquals(List.of(1), result);
    }

    @Test
    @DisplayName("reverseCopyTest_1:反转列表，返回新 List。例 [1,2,3] -> [3,2,1]")
    void reverseCopyTest_1() {
        List<Integer> input = List.of(1,3,2);
        List<Integer> result = ListLab.reverseCopy(input);
        assertIterableEquals(List.of(2,3,1), result);
    }

    @Test
    @DisplayName("reverseCopyTest_2:反转列表，返回新 List（不修改原 List）。例 [1,2,3] -> [3,2,1]")
    void reverseCopyTest_2() {
        List<Integer> input = List.of(1,3,2);
        ListLab.reverseCopy(input);
        assertIterableEquals(List.of(1,3,2), input);
    }

    @Test
    @DisplayName("frequencyTest_1: 统计元素出现次数")
    void frequencyTest_1() {
        Map<Integer, Integer> result = ListLab.frequency(List.of(1,2,2,3,3,3));
        Map<Integer, Integer> complateMap = Map.of(1,1,2,2,3,3);
        assertEquals(complateMap, result);
        // 断言 1 出现 1 次、2 出现 2 次、3 出现 3 次
    }

    @Test
    @DisplayName("frequencyTest_2: 空列表返回空Map")
    void frequencyTest_2() {
        Map<Integer,Integer> result = ListLab.frequency(List.of());
        assertTrue(result.isEmpty());
        // 调用 frequency(List.of())
        // 断言 result.isEmpty()
    }

    @Test
    @DisplayName("maxTest_1: 正常列表返回最大值")
    void maxTest_1() {
        int result = ListLab.max(List.of(3,1,2));
        assertEquals(3, result);
        // 输入 [3,1,2]，断言结果 3
    }

    @Test
    @DisplayName("maxTest_2: 空列表抛NoSuchElementException")
    void maxTest_2() {
        assertThrows(NoSuchElementException.class, () -> {
            ListLab.max(List.of());
        });
        // assertThrows(NoSuchElementException.class, () -> ListLab.max(List.of()))
    }

    @Test
    @DisplayName("toImmutableTest_1: 内容与原列表一致")
    void toImmutableTest_1() {
        List<Integer> input = new ArrayList<>();
        input.add(1);
        input.add(2);
        input.add(3);
        List<Integer> result = ListLab.toImmutable(input);
        assertIterableEquals(input, result);
        // 输入，断言内容相等
    }

    @Test
    @DisplayName("toImmutableTest_2: 修改不可变List抛异常")
    void toImmutableTest_2() {
        List<Integer> input = new ArrayList<>();
        input.add(1);
        input.add(2);
        input.add(3);
        assertThrows(UnsupportedOperationException.class, () -> {
            List<Integer> result = ListLab.toImmutable(input);
            result.add(4);
        });
        // 拿到 result 后，assertThrows(UnsupportedOperationException.class, () -> result.add(...))
    }

}
