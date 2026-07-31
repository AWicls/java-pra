package learning.pra.stream;

import java.util.*;
import java.util.stream.*;

public final class StreamLab {
    private StreamLab() {
    }

    // 1. 求列表中所有偶数的平方和。例 [1,2,3,4,5,6] -> 4+16+36 = 56
    public static int sumOfEvenSquares(List<Integer> nums) {
        return nums.stream()
                .mapToInt(n -> n * n)
                .sum();
    }

    // 2. 把字符串列表转成大写并去重。例 ["a","b","A","a"] -> ["A","B"]
    // 返回不可变 List
    public static List<String> upperCaseDistinct(List<String> strs) {
        return null;
    }

    // 3. 按字符串长度分组。例 ["a","bb","cc","ddd"] -> {1:["a"], 2:["bb","cc"], 3:["ddd"]}
    public static Map<Integer, List<String>> groupByLength(List<String> strs) {
        return null;
    }

    // 4. 统计每个单词出现次数，返回 Map<单词, 次数>
    // 例 ["apple","banana","apple"] -> {"apple":2, "banana":1}
    public static Map<String, Long> wordFrequency(List<String> words) {
        return null;
    }

    // 5. 找出列表中第一个大于 10 的元素，没有则返回 -1
    // 用 Stream 实现（不要用 for 循环）
    public static int findFirstGreaterThanTen(List<Integer> nums) {
        return 0;
    }
}
