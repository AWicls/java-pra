package learning.pra.generics;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenericPecsLab {

    public static <T> void copyAll(List<? extends T> src, List<? super T> dst) {
        for (T object : src) {
            dst.addLast(object);
        }
    }

    public static <T> T[] newArray(Class<T> componentType, int length) {
        return (T[]) Array.newInstance(componentType, length);
    }

    public static <T> T createInstance(Class<T> type) throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
        return type.getDeclaredConstructor().newInstance();
    }

    public interface Named {
        String getName();
    }

    public static <T extends Comparable<T> & Named> String bestName(List<T> list) {
        if (list.isEmpty()) {
            throw new IllegalArgumentException();
        }
        T max = list.getFirst();
        for (T t : list) {
            if (t.compareTo(max) > 0) {
                max = t;
            }
        }
        return max.getName();
    }

}

class Box<T> {
    private T src = null;

    public void set(T src) {
        this.src = src;
    }

    public T get() {
        return src;
    }
}

class Repository<T> {

    private Map<Integer, T> map = new HashMap<>();
    private int keyId = 0;

    public int add(T src) {
        ++keyId;
        map.put(keyId, src);
        return keyId;
    }

    public T findById(int id) {
        return map.get(id);
    }

    public boolean remove(int id) {
        if (map.containsKey(id)) {
            return map.remove(id, map.get(id));
        }
        return false;
    }

    public int count() {
        return map.size();
    }
}
