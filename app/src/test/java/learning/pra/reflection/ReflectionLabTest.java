package learning.pra.reflection;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
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

    // ========== readField 测试 ==========

    /** 测试辅助类：含 public / private / 基本类型 字段 */
    static class Sample {
        public String name = "alice";
        private int age = 30;
        private boolean active = true;
    }

    @Test
    void readField_publicField() throws NoSuchFieldException, IllegalAccessException {
        Sample s = new Sample();
        Object value = ReflectionLab.readField(s, "name");
        assertEquals("alice", value);
    }

    @Test
    void readField_privateField_setAccessibleWorks() throws NoSuchFieldException, IllegalAccessException {
        Sample s = new Sample();
        Object value = ReflectionLab.readField(s, "age");
        assertEquals(30, value);   // 基本类型自动装箱为 Integer
    }

    @Test
    void readField_privateBooleanField() throws NoSuchFieldException, IllegalAccessException {
        Sample s = new Sample();
        Object value = ReflectionLab.readField(s, "active");
        assertEquals(true, value);   // 基本类型 boolean 装箱为 Boolean
    }

    @Test
    void readField_nonExistent_throwsNoSuchField() {
        Sample s = new Sample();
        assertThrows(NoSuchFieldException.class, () -> ReflectionLab.readField(s, "noSuchField"));
    }

    @Test
    void readField_nullObj_throwsNPE() {
        // obj.getClass() 直接触发 NPE，先于 NoSuchFieldException
        assertThrows(NullPointerException.class, () -> ReflectionLab.readField(null, "any"));
    }

    // ========== invokeMethod 测试 ==========

    /** 测试辅助类：含重载 / private / 静态 / 异常方法 */
    static class Calc {
        public int add(int a, int b) {
            return a + b;
        }

        public String add(String a, String b) {
            return a + b;
        }

        private String secret() {
            return "hidden";
        }

        private int square(int n) {
            return n * n;
        }

        public static int staticMultiply(int a, int b) {
            return a * b;
        }

        public void boom() {
            throw new IllegalStateException("boom-inner");
        }
    }

    @Test
    void invokeMethod_overload_intParams() throws Exception {
        Calc c = new Calc();
        Object result = ReflectionLab.invokeMethod(c, "add", 3, 5);
        assertEquals(8, result);   // int 装箱为 Integer，unwrap 还原为 int.class
    }

    @Test
    void invokeMethod_overload_stringParams_distinguishesOverload() throws Exception {
        Calc c = new Calc();
        Object result = ReflectionLab.invokeMethod(c, "add", "Hello", "World");
        assertEquals("HelloWorld", result);   // 重载选择 String 版本
    }

    @Test
    void invokeMethod_privateMethod_setAccessibleWorks() throws Exception {
        Calc c = new Calc();
        Object result = ReflectionLab.invokeMethod(c, "secret");
        assertEquals("hidden", result);
    }

    @Test
    void invokeMethod_privateMethodWithArgs() throws Exception {
        Calc c = new Calc();
        Object result = ReflectionLab.invokeMethod(c, "square", 4);
        assertEquals(16, result);
    }

    @Test
    void invokeMethod_noArgMethod() throws Exception {
        Calc c = new Calc();
        Object result = ReflectionLab.invokeMethod(c, "secret");
        assertEquals("hidden", result);
    }

    @Test
    void invokeMethod_nonExistent_throwsNoSuchMethod() {
        Calc c = new Calc();
        assertThrows(NoSuchMethodException.class, () -> ReflectionLab.invokeMethod(c, "noSuch"));
    }

    @Test
    void invokeMethod_innerException_wrappedInInvocationTargetException() {
        Calc c = new Calc();
        // 被调方法内部抛 IllegalStateException，被包成 InvocationTargetException
        InvocationTargetException ite = assertThrows(
                InvocationTargetException.class,
                () -> ReflectionLab.invokeMethod(c, "boom"));
        // 解包验证：真实异常在 getCause()
        assertTrue(ite.getCause() instanceof IllegalStateException);
        assertEquals("boom-inner", ite.getCause().getMessage());
    }

    // ========== newInstance 测试 ==========

    /** 测试辅助类：含 public / private / 重载 构造器 */
    static class Person {
        String name;
        int age;

        public Person() {
            this.name = "default";
            this.age = 0;
        }

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        private Person(String name) {
            this.name = name;
            this.age = -1;
        }

        public Person(int age, String name) {
            this.name = name + "-reversed";
            this.age = age;
        }
    }

    @Test
    void newInstance_noArgConstructor() throws Exception {
        Person p = ReflectionLab.newInstance(Person.class);
        assertEquals("default", p.name);
        assertEquals(0, p.age);
    }

    @Test
    void newInstance_publicConstructorWithArgs() throws Exception {
        Person p = ReflectionLab.newInstance(Person.class, "alice", 30);
        assertEquals("alice", p.name);
        assertEquals(30, p.age);
    }

    @Test
    void newInstance_privateConstructor_setAccessibleWorks() throws Exception {
        Person p = ReflectionLab.newInstance(Person.class, "bob");
        assertEquals("bob", p.name);
        assertEquals(-1, p.age);
    }

    @Test
    void newInstance_overloadedConstructor_distinguishesByParamOrder() throws Exception {
        // (String, int) 和 (int, String) 是不同构造器，通过 unwrap 推断类型区分
        Person p = ReflectionLab.newInstance(Person.class, 25, "carol");
        assertEquals("carol-reversed", p.name);
        assertEquals(25, p.age);
    }

    @Test
    void newInstance_nonExistentConstructor_throwsNoSuchMethod() {
        assertThrows(NoSuchMethodException.class,
                () -> ReflectionLab.newInstance(Person.class, 3.14));
    }

    @Test
    void newInstance_abstractClass_throwsInstantiationException() {
        // 用项目内抽象类，避免 JDK 模块系统的 InaccessibleObjectException
        assertThrows(InstantiationException.class,
                () -> ReflectionLab.newInstance(AbstractShape.class));
    }

    /** 测试用抽象类 */
    static abstract class AbstractShape {
    }
}
