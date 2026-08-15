package learning.pra.annotations;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AnnotationsLab 内置注解演示的单元测试（第九课）。
 *
 * <p>验证 @FunctionalInterface / @Deprecated / @SuppressWarnings / @Override 四个内置
 * 注解的贴注，以及函数式接口作为 lambda 使用。
 *
 * @see AnnotationsLab
 */
class AnnotationsLabTest {

    @Test
    void map_contains_four_annotation_keys() {
        Map<String, String> map = AnnotationsLab.demoBuiltInAnnotations();
        assertNotNull(map.get("FunctionalInterface"), "缺少 FunctionalInterface 说明");
        assertNotNull(map.get("Deprecated"), "缺少 Deprecated 说明");
        assertNotNull(map.get("SuppressWarnings"), "缺少 SuppressWarnings 说明");
        assertNotNull(map.get("Override"), "缺少 Override 说明");
    }

    @Test
    void greeter_interface_is_functional_interface() {
        // 反射读取接口上的 @FunctionalInterface 注解，验证真的贴了
        boolean present = AnnotationsLab.Greeter.class.isAnnotationPresent(
                java.lang.FunctionalInterface.class);
        assertTrue(present, "Greeter 接口必须贴 @FunctionalInterface");
    }

    @Test
    void worker_oldmethod_is_deprecated() throws NoSuchMethodException {
        // 反射读取方法上的 @Deprecated 注解
        Method m = AnnotationsLab.Worker.class.getDeclaredMethod("oldMethod");
        boolean present = m.isAnnotationPresent(Deprecated.class);
        assertTrue(present, "Worker.oldMethod() 必须贴 @Deprecated");
    }

    @Test
    void worker_useold_calls_deprecated_without_warning() {
        // @SuppressWarnings 是 SOURCE 保留，反射读不到，但能编译通过说明它生效了
        AnnotationsLab.Worker w = new AnnotationsLab.Worker();
        String result = w.useOld();
        assertNotNull(result, "useOld 应能调用过时的 oldMethod 而不报错");
    }

    @Test
    void myworker_oldmethod_overrides_parent() throws NoSuchMethodException {
        // @Override 是 SOURCE 保留，反射读不到，但子类方法能被正确调用说明重写生效
        Method parent = AnnotationsLab.Worker.class.getDeclaredMethod("oldMethod");
        Method child = AnnotationsLab.MyWorker.class.getDeclaredMethod("oldMethod");
        assertEquals(parent.getName(), child.getName(), "方法名应一致");
        assertArrayEquals(parent.getParameterTypes(), child.getParameterTypes(),
                "参数类型应一致（@Override 重写要求签名一致）");
        // 多态验证：MyWorker 实例调用 oldMethod 走子类实现
        AnnotationsLab.Worker w = new AnnotationsLab.MyWorker();
        String result = w.oldMethod();
        assertNotEquals("oldMethod", result, "多态应走子类 MyWorker.oldMethod 的实现");
    }

    @Test
    void functional_interface_can_be_used_as_lambda() {
        // 验证 @FunctionalInterface 的价值：能用 lambda 简写
        AnnotationsLab.Greeter g = name -> "hello," + name;
        String result = g.greet("test");
        assertTrue(result.contains("test"), "lambda 实现的 greet 应包含传入的 name");
    }
}
