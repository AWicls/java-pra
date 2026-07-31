package learning.pra.generics;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class GenericsLab {
    private GenericsLab() {
    }

    // 1. 泛型方法：反转任意类型 List。例 reverse([1,2,3]) -> [3,2,1]，reverse(["a","b"]) ->
    // ["b","a"]
    public static <T> List<T> reverse(List<T> source) {
        List<T> listCopy = List.copyOf(source);
        List<T> reversed = listCopy.reversed();
        return reversed;
    }

    // 2. 泛型方法 + 上界：求 List 中最大值。max([3,1,2]) -> 3，max(["banana","apple"]) ->
    // "banana"
    // 要求空列表抛 NoSuchElementException（这次自己用 for 循环实现，不用 Collections.max）
    public static <T extends Comparable<T>> T max(List<T> source) {
        if (source.isEmpty()) {
            throw new NoSuchElementException();
        }
        T max = source.get(0);
        for (T t : source) {
            int compareResult = t.compareTo(max);
            if (compareResult > 0) {
                max = t;
            }
        }
        return max;
    }

    // 3. PECS 实战：把 from 的元素全部复制到 to
    // 提示签名：public static <T> void copy(List<? extends T> from, List<? super T> to)
    public static <T> void copy(List<? extends T> from, List<? super T> to) {
        for (T t : from) {
            to.add(t);
        }
    }

    // 4. 泛型类：实现一个简单的栈 Stack<T>（push / pop / isEmpty / size）
    // 作为 GenericsLab 的静态内部类
    public static class Stack<T> {
        private final List<T> items = new ArrayList<>();

        public void push(T item) {
            items.addLast(item);
        }

        public T pop() {
            if (items.isEmpty()) {
                throw new NoSuchElementException();
            }
                return items.removeLast();
        }

        public boolean isEmpty() {
            return items.isEmpty();
        }

        public int size() {
            return items.size();
        }
        // 自己实现
    }

    // 5. 类型擦除验证：返回两个不同泛型 List 的 class 是否相同
    public static boolean sameClassAtRuntime() {
        List<String> a = new ArrayList<>();
        List<Integer> b = new ArrayList<>();
        return a.getClass() == b.getClass();
    }
}
