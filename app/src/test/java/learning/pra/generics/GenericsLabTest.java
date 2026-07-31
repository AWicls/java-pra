package learning.pra.generics;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class GenericsLabTest {

    // ========== max：泛型 + 上界 + Comparable（重点测）==========

    @Test
    @DisplayName("max_Integer列表返回最大值")
    void max_整数列表() {
        assertEquals(3, GenericsLab.max(List.of(3, 1, 2)));
    }

    @Test
    @DisplayName("max_String列表按字典序返回最大值")
    void max_字符串列表() {
        // "banana" > "apple"（字典序）
        assertEquals("banana", GenericsLab.max(List.of("apple", "banana")));
    }

    @Test
    @DisplayName("max_空列表抛NoSuchElementException")
    void max_空列表抛异常() {
        List<Integer> empty = List.of();   // 显式声明类型，否则 List.of() 推断成 List<Object>
        assertThrows(NoSuchElementException.class, () -> GenericsLab.max(empty));
    }

    @Test
    @DisplayName("max_单元素列表返回该元素")
    void max_单元素() {
        assertEquals(42, GenericsLab.max(List.of(42)));
    }

    // ========== Stack<T>：泛型类的状态管理（重点测）==========

    @Test
    @DisplayName("Stack_后进先出LIFO")
    void stack_LIFO() {
        GenericsLab.Stack<String> stack = new GenericsLab.Stack<>();
        stack.push("a");
        stack.push("b");
        stack.push("c");
        assertEquals("c", stack.pop());   // 后进先出
        assertEquals("b", stack.pop());
        assertEquals("a", stack.pop());
    }

    @Test
    @DisplayName("Stack_size和isEmpty状态正确")
    void stack_状态() {
        GenericsLab.Stack<Integer> stack = new GenericsLab.Stack<>();
        assertTrue(stack.isEmpty());
        assertEquals(0, stack.size());

        stack.push(1);
        stack.push(2);
        assertFalse(stack.isEmpty());
        assertEquals(2, stack.size());
    }

    @Test
    @DisplayName("Stack_空栈pop抛NoSuchElementException")
    void stack_空栈pop抛异常() {
        GenericsLab.Stack<Integer> stack = new GenericsLab.Stack<>();
        assertThrows(NoSuchElementException.class, stack::pop);
    }

    @Test
    @DisplayName("Stack_泛型支持不同类型")
    void stack_泛型() {
        GenericsLab.Stack<Double> stack = new GenericsLab.Stack<>();
        stack.push(1.5);
        stack.push(2.5);
        assertEquals(2.5, stack.pop());
    }

    // ========== copy：PECS（重点测）==========

    @Test
    @DisplayName("copy_Integer复制到Number容器（PECS典型场景）")
    void copy_子类到父类() {
        List<Integer> from = new ArrayList<>(List.of(1, 2, 3));
        List<Number> to = new ArrayList<>();
        GenericsLab.copy(from, to);
        assertIterableEquals(List.of(1, 2, 3), to);
    }

    @Test
    @DisplayName("copy_不修改源List")
    void copy_不修改源() {
        List<Integer> from = new ArrayList<>(List.of(1, 2, 3));
        List<Number> to = new ArrayList<>();
        GenericsLab.copy(from, to);
        assertIterableEquals(List.of(1, 2, 3), from);   // 源不变
    }

    @Test
    @DisplayName("copy_空源列表目标不变")
    void copy_空源() {
        List<Integer> from = new ArrayList<>();
        List<Number> to = new ArrayList<>(List.of(0));
        GenericsLab.copy(from, to);
        assertIterableEquals(List.of(0), to);   // 目标保持原样
    }

    // ========== sameClassAtRuntime：类型擦除验证 ==========

    @Test
    @DisplayName("sameClassAtRuntime_不同泛型List运行时class相同（类型擦除）")
    void sameClassAtRuntime_类型擦除() {
        assertTrue(GenericsLab.sameClassAtRuntime());
    }

    // ========== reverse：简单验证（不重点测，但顺带跑一下）==========

    @Test
    @DisplayName("reverse_泛型反转String列表")
    void reverse_字符串() {
        assertIterableEquals(List.of("b", "a"), GenericsLab.reverse(List.of("a", "b")));
    }
}
