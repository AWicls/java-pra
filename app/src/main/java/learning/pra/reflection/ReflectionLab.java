package learning.pra.reflection;

import java.lang.reflect.Field;
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
}
