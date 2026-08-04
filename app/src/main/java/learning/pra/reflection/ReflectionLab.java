package learning.pra.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ReflectionLab {

    public static Map<String, String> classInfo(Object obj) {

        Class<?> clazz = obj.getClass();
        ClassLoader cl = clazz.getClassLoader();        // JDK 核心类返回 null
        Module mod = clazz.getModule();                 // JDK 9+ 模块系统

        Map<String, String> map = new HashMap<>();
        map.put("simpleName", clazz.getSimpleName());
        map.put("typeName", clazz.getName());
        map.put("classLoader", cl != null ? cl.getName() : "bootstrap");
        map.put("module", Objects.requireNonNullElse(mod.getName(), "unnamed"));

        return map;
    }

    public static Object readField(Object obj, String fieldName) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field fx = obj.getClass().getDeclaredField(fieldName);
        fx.setAccessible(true);
        return fx.get(obj);
    }

    private static Class<?> unwrap(Class<?> type) {
        if (type == Integer.class) return int.class;
        if (type == Long.class) return long.class;
        if (type == Double.class) return double.class;
        if (type == Float.class) return float.class;
        if (type == Boolean.class) return boolean.class;
        if (type == Byte.class) return byte.class;
        if (type == Short.class) return short.class;
        if (type == Character.class) return char.class;
        return type;
    }

    public static Object invokeMethod(Object obj, String methodeName, Object... args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Class<?> clazz = obj.getClass();
        Class<?>[] paramTypes = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            paramTypes[i] = unwrap(args[i].getClass());
        }

        Method method = clazz.getDeclaredMethod(methodeName, paramTypes);

        method.setAccessible(true);

        return method.invoke(obj, args);

    }
}
