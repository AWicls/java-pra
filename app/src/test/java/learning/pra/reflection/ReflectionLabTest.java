package learning.pra.reflection;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ReflectionLabTest {

    private static class Inner {
    }

    @Test
    void classInfo_string() {
        Map<String, String> info = ReflectionLab.classInfo(new String("hi"));
        assertEquals("String", info.get("simpleName"));
        assertEquals("java.lang.String", info.get("typeName"));
        assertEquals("bootstrap", info.get("classLoader"));
        assertEquals("java.base", info.get("module"));
    }

    @Test
    void classInfo_arrayList() {
        Map<String, String> info = ReflectionLab.classInfo(new ArrayList<>());
        assertEquals("ArrayList", info.get("simpleName"));
        assertEquals("java.util.ArrayList", info.get("typeName"));
        assertEquals("bootstrap", info.get("classLoader"));
        assertEquals("java.base", info.get("module"));
    }

    @Test
    void classInfo_object() {
        Map<String, String> info = ReflectionLab.classInfo(new Object());
        assertEquals("Object", info.get("simpleName"));
        assertEquals("java.lang.Object", info.get("typeName"));
        assertEquals("bootstrap", info.get("classLoader"));
        assertEquals("java.base", info.get("module"));
    }

    @Test
    void classInfo_customInnerClass_isUnnamedModule() {
        Map<String, String> info = ReflectionLab.classInfo(new Inner());
        assertEquals("Inner", info.get("simpleName"));
        assertTrue(info.get("typeName").contains("ReflectionLabTest"), "内部类 typeName 含外部类名");
        assertNotEquals("bootstrap", info.get("classLoader"), "自定义类由 AppClassLoader 加载");
        assertEquals("unnamed", info.get("module"));
    }

    @Test
    void classInfo_intArray_typeNameUsesGetNameSyntax() {
        int[] arr = new int[]{1, 2, 3};
        Map<String, String> info = ReflectionLab.classInfo(arr);
        assertEquals("int[]", info.get("simpleName"));
        assertEquals("[I", info.get("typeName"), "getName 对基本类型数组返回 [I，不是 int[]");
    }

    @Test
    void classInfo_nullInput_throwsNPE() {
        assertThrows(NullPointerException.class, () -> ReflectionLab.classInfo(null));
    }
}
