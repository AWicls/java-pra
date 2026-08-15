package learning.pra.ioc;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MiniContainer {
    private Map<Class<?>, Object> singletons = new HashMap<>();
    private Set<Class<?>> components = new HashSet<>();

    public void register(Class<?>... type) throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
        for (Class<?> clazz : type) {
            boolean equalsComponent = clazz.isAnnotationPresent(Component.class);
            boolean equalsSingleton = clazz.isAnnotationPresent(Singleton.class);

            if (equalsComponent) {
                components.add(clazz);
                if (equalsSingleton) {
                    singletons.put(clazz, clazz.getDeclaredConstructor().newInstance());
                }
            }
        }
    }

    public <T> T getBean(Class<T> type) throws InstantiationException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException {
        if (!components.contains(type)) {
            throw new IllegalArgumentException();
        }

        // boolean ComponentPresent = type.isAnnotationPresent(Component.class);
        boolean SingletonPresent = type.isAnnotationPresent(Singleton.class);

        if (SingletonPresent) {
            return (T) singletons.get(type);
        } else {
            return type.getDeclaredConstructor().newInstance();
        }
    }
}
