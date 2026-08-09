package learning.pra.generics;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GenericPecsLabTest {

    @Test
    @DisplayName("copyAll_Integer拷贝到Number容器（PECS extends+super典型场景）")
    void copyAll_子类到父类() {
        List<Integer> from = new ArrayList<>(List.of(1, 2, 3));
        List<Number> to = new ArrayList<>();
        GenericPecsLab.copyAll(from, to);
        assertIterableEquals(List.of(1, 2, 3), to);
    }

    @Test
    @DisplayName("copyAll_String拷贝到Object容器")
    void copyAll_String到Object() {
        List<String> from = new ArrayList<>(List.of("a", "b"));
        List<Object> to = new ArrayList<>();
        GenericPecsLab.copyAll(from, to);
        assertIterableEquals(List.of("a", "b"), to);
    }

    @Test
    @DisplayName("copyAll_不修改源List")
    void copyAll_不修改源() {
        List<Integer> from = new ArrayList<>(List.of(1, 2, 3));
        List<Number> to = new ArrayList<>();
        GenericPecsLab.copyAll(from, to);
        assertIterableEquals(List.of(1, 2, 3), from);
    }

    @Test
    @DisplayName("copyAll_空源列表目标保持不变")
    void copyAll_空源() {
        List<Integer> from = new ArrayList<>();
        List<Number> to = new ArrayList<>(List.of(0));
        GenericPecsLab.copyAll(from, to);
        assertIterableEquals(List.of(0), to);
    }

    @Test
    @DisplayName("copyAll_目标已含元素时向后追加")
    void copyAll_目标已含元素追加() {
        List<Integer> from = new ArrayList<>(List.of(7, 8));
        List<Number> to = new ArrayList<>(List.of(1, 2));
        GenericPecsLab.copyAll(from, to);
        assertIterableEquals(List.of(1, 2, 7, 8), to);
    }

    // ========== newArray：类型擦除 workaround（运行期造泛型数组）==========

    @Test
    @DisplayName("newArray_返回真正的String[]而非Object[]")
    void newArray_String数组() {
        String[] arr = GenericPecsLab.newArray(String.class, 3);
        assertEquals(String.class, arr.getClass().getComponentType());   // 元素类型是 String
        assertEquals(3, arr.length);
    }

    @Test
    @DisplayName("newArray_Integer数组长度正确")
    void newArray_Integer数组() {
        Integer[] arr = GenericPecsLab.newArray(Integer.class, 5);
        assertEquals(Integer.class, arr.getClass().getComponentType());
        assertEquals(5, arr.length);
    }

    // ========== createInstance：反射在运行期造对象 ==========

    @Test
    @DisplayName("createInstance_返回指定类型的实例")
    void createInstance_StringBuilder() throws Exception {
        Object sb = GenericPecsLab.createInstance(StringBuilder.class);
        assertInstanceOf(StringBuilder.class, sb);
    }

    @Test
    @DisplayName("createInstance_能作为T直接用（泛型强类型）")
    void createInstance_泛型强类型() throws Exception {
        StringBuilder sb = GenericPecsLab.createInstance(StringBuilder.class);
        sb.append("hello");
        assertEquals("hello", sb.toString());   // 无需强转，编译期就是 StringBuilder
    }

    // ========== bestName：多上界 <T extends Comparable<T> & Named> ==========

    // Person 同时满足 Comparable<Person>（按 score 比）+ Named（报名字）
    static class Person implements Comparable<Person>, GenericPecsLab.Named {
        final String name;
        final int score;
        Person(String name, int score) { this.name = name; this.score = score; }
        @Override public int compareTo(Person o) { return Integer.compare(this.score, o.score); }
        @Override public String getName() { return name; }
    }

    @Test
    @DisplayName("bestName_返回分数最高者的名字")
    void bestName_分数最高() {
        List<Person> persons = new ArrayList<>(List.of(
                new Person("alice", 80),
                new Person("bob", 95),
                new Person("carol", 70)));
        assertEquals("bob", GenericPecsLab.bestName(persons));
    }

    @Test
    @DisplayName("bestName_平局时返回第一个")
    void bestName_平局返回首个() {
        List<Person> persons = new ArrayList<>(List.of(
                new Person("first", 90),
                new Person("second", 90)));
        assertEquals("first", GenericPecsLab.bestName(persons));   // 只取 > 不取 >=
    }

    @Test
    @DisplayName("bestName_空列表抛IllegalArgumentException")
    void bestName_空列表抛异常() {
        List<Person> empty = new ArrayList<>();
        assertThrows(IllegalArgumentException.class, () -> GenericPecsLab.bestName(empty));
    }
}
