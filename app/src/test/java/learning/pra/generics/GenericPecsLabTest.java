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
}
