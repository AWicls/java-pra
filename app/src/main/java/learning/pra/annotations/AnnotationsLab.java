package learning.pra.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 注解学习实验：演示 JDK 四个内置注解 + 自定义 {@code @Label} 注解。
 *
 * <p>本类演示第九课知识点：<br>
 * 1. {@link #demoBuiltInAnnotations}：四个内置注解（@FunctionalInterface/@Deprecated/@SuppressWarnings/@Override）<br>
 * 2. {@link Label}：自定义注解（@Target(FIELD) + @Retention(RUNTIME)）<br>
 * 3. {@link #readLabels(Class)}：反射读字段上的 @Label 注解
 */
public class AnnotationsLab {

    /**
     * 演示四个 JDK 内置注解的用法，并返回每个注解的说明。
     *
     * <p>键值说明：
     * <ul>
     *   <li>{@code FunctionalInterface} - 贴在 {@link Greeter} 接口上，使其可用 lambda 简写</li>
     *   <li>{@code Deprecated} - 贴在 {@link Worker#oldMethod()} 上，标记过时</li>
     *   <li>{@code SuppressWarnings} - 贴在 {@link Worker#useOld()} 上，抑制过时警告</li>
     *   <li>{@code Override} - 贴在 {@link MyWorker#oldMethod()} 上，标记重写父类方法</li>
     * </ul>
     *
     * @return 注解名 -> 说明 的映射
     */
    public static Map<String, String> demoBuiltInAnnotations() {
        HashMap<String, String> map = new HashMap<>();

        Greeter greeter = new Greeter() {
            @Override
            public String greet(String name) {
                return "greeter";
            }
        };

        Worker worker = new Worker();
        String oldMethod = worker.oldMethod();
        String useOld = worker.useOld();

        MyWorker myWorker = new MyWorker();
        String oldMethod2 = myWorker.oldMethod();

        map.put("FunctionalInterface", greeter.toString());
        map.put("Deprecated", oldMethod);
        map.put("SuppressWarnings", useOld);
        map.put("Override", oldMethod2);

        return map;
    }

    /** 函数式接口：只有一个抽象方法，贴 @FunctionalInterface 后可用 lambda 简写。 */
    @FunctionalInterface
    interface Greeter {
        /** 问候方法。 */
        String greet(String name);
    }

    /** 演示 @Deprecated 和 @SuppressWarnings("deprecation") 配对的 Worker。 */
    static class Worker {
        /** 过时方法，调用方会被警告建议改用新方法。 */
        @Deprecated
        public String oldMethod() {
            return "oldMethod";
        }

        /** 调用过时方法但抑制警告（演示 @SuppressWarnings 配对用法）。 */
        @SuppressWarnings("deprecation")
        public String useOld() {
            return oldMethod();
        }
    }

    /** 重写父类 oldMethod，演示 @Override 检查重写是否正确。 */
    static class MyWorker extends Worker {
        @Override
        public String oldMethod() {
            return "MyWorker";
        }
    }

    /**
     * 自定义注解：给字段贴一个中文标签。
     *
     * <p>元注解说明：
     * <ul>
     *   <li>{@code @Target(FIELD)} - 只能贴在字段上</li>
     *   <li>{@code @Retention(RUNTIME)} - 保留到运行期，反射可读</li>
     * </ul>
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Label {
        /** 标签文本（单属性 value 使用时可省略名字）。 */
        String value();
    }

    /** 演示用 @Label 贴字段标签的 User 类。 */
    static class User {
        @Label("用户名")
        String name;

        @Label("年龄")
        int age;
    }

    /**
     * 用反射读取类中所有字段上的 @Label 注解。
     *
     * <p>实现要点：用 {@code getDeclaredFields()} 拿本类所有字段（含 private），
     * 遍历时对没贴 @Label 的字段跳过（getAnnotation 返回 null）。
     *
     * @param clazz 任意类
     * @return 字段名 -> 标签值 的映射（没贴 @Label 的字段不收集）
     */
    public static Map<String, String> readLabels(Class<?> clazz) {
        HashMap<String, String> map = new HashMap<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Label annotation = field.getAnnotation(Label.class);
            if (annotation == null) {
                continue;
            }
            String name = field.getName();
            map.put(name, annotation.value());
        }

        return map;
    }
}
