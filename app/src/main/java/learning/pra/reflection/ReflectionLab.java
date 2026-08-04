package learning.pra.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 反射学习实验：运行期动态探查/操作类的能力。
 *
 * <p>本类演示第八课知识点：<br>
 * 1. {@link #classInfo}：获取 {@code Class<?>} 元数据（类名/类加载器/模块）<br>
 * 2. {@link #readField}：反射读字段（含 private，演示 {@code setAccessible} 破防）<br>
 * 3. {@link #invokeMethod}：反射调用方法（含重载、private，演示参数类型推断）<br>
 * 4. {@link #newInstance}：反射调用构造器造对象（含 private 构造器破防）
 */
public class ReflectionLab {

    /**
     * 获取对象的类元数据信息。
     *
     * <p>键值说明：
     * <ul>
     *   <li>{@code simpleName} - 类简单名（如 {@code String}）</li>
     *   <li>{@code typeName} - 类完整名（{@code getName()}，数组返回 {@code [I} 等特殊语法）</li>
     *   <li>{@code classLoader} - 类加载器名；JDK 核心类返回 {@code "bootstrap"}（引导类加载器非 Java 对象）</li>
     *   <li>{@code module} - 所属模块名；无名模块返回 {@code "unnamed"}</li>
     * </ul>
     *
     * @param obj 任意对象（不能为 {@code null}）
     * @return 含上述四个键的 {@code Map}
     */
    public static Map<String, String> classInfo(Object obj) {

        Class<?> clazz = obj.getClass();
        ClassLoader cl = clazz.getClassLoader(); // JDK 核心类返回 null
        Module mod = clazz.getModule(); // JDK 9+ 模块系统

        Map<String, String> map = new HashMap<>();
        map.put("simpleName", clazz.getSimpleName());
        map.put("typeName", clazz.getName());
        map.put("classLoader", cl != null ? cl.getName() : "bootstrap");
        map.put("module", Objects.requireNonNullElse(mod.getName(), "unnamed"));

        return map;
    }

    /**
     * 反射读取对象指定字段值（含 private 字段）。
     *
     * <p>流程：{@code getDeclaredField} -> {@code setAccessible(true)} -> {@code get(obj)}。
     * 基本类型字段值会自动装箱（如 {@code int} -> {@code Integer}）。
     *
     * @param obj       目标对象（不能为 {@code null}，否则 {@code getClass()} 抛 NPE）
     * @param fieldName 字段名
     * @return 字段值（基本类型自动装箱为对应包装类）
     * @throws NoSuchFieldException   字段不存在时抛出
     * @throws IllegalAccessException setAccessible 后理论上不会发生
     */
    public static Object readField(Object obj, String fieldName)
            throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field fx = obj.getClass().getDeclaredField(fieldName);
        fx.setAccessible(true);
        return fx.get(obj);
    }

    /**
     * 包装类 -> 基本类型的映射（用于参数类型推断）。
     *
     * <p>反射调用 {@code getDeclaredMethod} 找方法时，参数类型必须严格匹配：
     * 基本类型方法签名（如 {@code int.class}）和包装类签名（如 {@code Integer.class}）不可互换。
     * 但 {@code Object... args} 中基本类型实参会自动装箱，需要此方法还原。
     *
     * @param type 包装类（如 {@code Integer.class}）
     * @return 对应基本类型（如 {@code int.class}）；非包装类原样返回
     */
    private static Class<?> unwrap(Class<?> type) {
        if (type == Integer.class)
            return int.class;
        if (type == Long.class)
            return long.class;
        if (type == Double.class)
            return double.class;
        if (type == Float.class)
            return float.class;
        if (type == Boolean.class)
            return boolean.class;
        if (type == Byte.class)
            return byte.class;
        if (type == Short.class)
            return short.class;
        if (type == Character.class)
            return char.class;
        return type;
    }

    /**
     * 反射调用对象指定方法（含 private、支持重载）。
     *
     * <p>流程：从 {@code args} 推断参数类型（经 {@link #unwrap} 还原基本类型）->
     * {@code getDeclaredMethod} -> {@code setAccessible(true)} -> {@code invoke}。
     *
     * <p><b>已知限制（学习阶段简化方案）：</b>
     * <ul>
     *   <li>不查继承：{@code getDeclaredMethod} 只看本类，{@code Object.toString()} 等找不到</li>
     *   <li>不支持 {@code null} 参数：{@code args[i].getClass()} 触发 NPE</li>
     *   <li>重载只匹配基本类型签名版本：包装类签名（如 {@code foo(Integer)}）找不到</li>
     * </ul>
     *
     * @param obj        目标对象
     * @param methodName 方法名
     * @param args       实参（不支持 {@code null}）
     * @return 方法返回值（void 方法返回 {@code null}）
     * @throws NoSuchMethodException     方法名/参数类型对不上时抛出
     * @throws IllegalAccessException    setAccessible 后理论上不会发生
     * @throws InvocationTargetException 被调方法内部抛异常时包装抛出，用 {@code getCause()} 解包
     */
    public static Object invokeMethod(Object obj, String methodeName, Object... args)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Class<?> clazz = obj.getClass();
        Class<?>[] paramTypes = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            paramTypes[i] = unwrap(args[i].getClass());
        }

        Method method = clazz.getDeclaredMethod(methodeName, paramTypes);
        method.setAccessible(true);
        return method.invoke(obj, args);
    }

    /**
     * 反射调用构造器造对象（含 private 构造器、支持重载）。
     *
     * <p>流程：从 {@code args} 推断参数类型 -> {@code getDeclaredConstructor} ->
     * {@code setAccessible(true)} -> {@code newInstance}。
     *
     * <p>典型应用：Spring 根据 {@code <bean class="...">} 造 Bean、JSON 反序列化造对象。
     * 私有构造器可破防（单例模式的反射攻击根因）。
     *
     * @param clazz 目标类
     * @param args  构造器实参（不支持 {@code null}）
     * @param <T>   目标类型
     * @return 新造的对象
     * @throws NoSuchMethodException     构造器不存在时抛出
     * @throws InstantiationException    抽象类/接口/数组类无法实例化时抛出
     * @throws IllegalAccessException    setAccessible 后理论上不会发生
     * @throws InvocationTargetException 构造器内部抛异常时包装抛出，用 {@code getCause()} 解包
     */
    public static <T> T newInstance(Class<T> clazz, Object... args) throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Class<?>[] paramTypes = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            paramTypes[i] = unwrap(args[i].getClass());
        }
        Constructor<T> constructor = clazz.getDeclaredConstructor(paramTypes);
        constructor.setAccessible(true);
        return constructor.newInstance(args);
    }

}
