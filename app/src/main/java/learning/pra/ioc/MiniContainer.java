package learning.pra.ioc;

import java.lang.reflect.Field;
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
                // if (equalsSingleton) {
                // singletons.put(clazz, clazz.getDeclaredConstructor().newInstance());
                // }
            }
        }
    }

    public <T> T getBean(Class<T> type) throws InstantiationException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException {
        if (!components.contains(type)) {
            throw new IllegalArgumentException();
        }
        if (singletons.containsKey(type)) {
            return (T) singletons.get(type);
        }
        T bean = type.getDeclaredConstructor().newInstance();
        injectDependencies(bean);
        if (type.isAnnotationPresent(Singleton.class)) {
            singletons.put(type, bean);
        }
        return bean;
    }

    private void injectDependencies(Object bean)
            throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            boolean hasInject = field.isAnnotationPresent(Inject.class);
            if (hasInject) {
                field.setAccessible(true);
                field.set(bean, getBean(field.getType()));
            }
        }
    }
}
