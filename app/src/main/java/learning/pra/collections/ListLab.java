package learning.pra.collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public final class ListLab {
    private ListLab() {
    }

    // 1. 去重，保留首次出现的顺序。例 [1,2,2,3,1] -> [1,2,3]
    public static List<Integer> dedupKeepOrder(List<Integer> source) {
        LinkedHashSet<Integer> linkedHashSet = new LinkedHashSet<>();
        linkedHashSet.addAll(source);
        return linkedHashSet.stream().toList();
    }

    // 2. 反转列表，返回新 List（不修改原 List）。例 [1,2,3] -> [3,2,1]
    public static List<Integer> reverseCopy(List<Integer> source) {
        List<Integer> reversed = new ArrayList<>(source);
        return reversed.reversed();
    }

    // 3. 统计每个元素出现次数。例 [1,2,2,3,3,3] -> {1:1, 2:2, 3:3}
    public static Map<Integer,Integer> frequency(List<Integer> source) {
        Map<Integer, Integer> map = new HashMap<>();
        for (Integer i : source) {
            map.put(i, map.getOrDefault(i, 0) + 1);
        }
        return map;
     }

    // 4. 求最大值（空列表抛 NoSuchElementException）
    public static int max(List<Integer> source) {
        return Collections.max(source);
    }

    // 5. 把列表转成不可变 List（调用方不能修改）
    public static List<Integer> toImmutable(List<Integer> source) {
        return List.copyOf(source);
    }
}
