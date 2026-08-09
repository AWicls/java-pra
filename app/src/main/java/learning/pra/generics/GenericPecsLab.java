package learning.pra.generics;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 第十三课：泛型进阶实战（PECS / 类型擦除 / 多上界 / 泛型类 DAO）
 */
public class GenericPecsLab {

    /**
     * PECS 实战：src 只读（Producer → extends），dst 只写（Consumer → super）。
     * 从而一个方法能处理"子类拷到父类桶"等多种类型组合。
     */
    public static <T> void copyAll(List<? extends T> src, List<? super T> dst) {
        for (T object : src) {      // src 元素是 T 或 T 子类，一定能赋给 T
            dst.addLast(object);    // T 一定能装进 T 的父类桶（addLast 是 JDK 21+）
        }
    }

    /**
     * 类型擦除 workaround：不能 new T[length]，改用 Array.newInstance 运行期造真正的 T[]。
     * (T[]) 强转是泛型数组创建的标准惯用法。
     */
    public static <T> T[] newArray(Class<T> componentType, int length) {
        return (T[]) Array.newInstance(componentType, length);
    }

    /**
     * 类型擦除 workaround：没有 T.class，靠传入的 Class<T> 参数 + 构造器反射造对象。
     */
    public static <T> T createInstance(Class<T> type) throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
        return type.getDeclaredConstructor().newInstance();
    }

    /** 多上界示例接口：有名字的东西 */
    public interface Named {
        String getName();
    }

    /**
     * 多上界：T 既要能互相比（递归边界 Comparable<T>），又要有名字（Named）。
     * 递归边界让 compareTo(T) 参数就是 T，比较时不用强转。
     */
    public static <T extends Comparable<T> & Named> String bestName(List<T> list) {
        if (list.isEmpty()) {
            throw new IllegalArgumentException();
        }
        T max = list.getFirst();
        for (T t : list) {
            if (t.compareTo(max) > 0) {   // 平局取第一个：只替换严格大于
                max = t;
            }
        }
        return max.getName();
    }

}

/** 泛型包装盒：一个类型安全的单值容器 */
class Box<T> {
    private T value = null;

    public void set(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }
}

/** 泛型 DAO：按自增 id 存取任意类型 T（一份逻辑，多种实体复用） */
class Repository<T> {

    private Map<Integer, T> map = new HashMap<>();  // id → 实体
    private int keyId = 0;

    /**
     * 先自增再用：存的 key 和返回的 id 是同一个数。
     * 若写成 int id = keyId++ 再 return，存的 key 与返回 id 错位 → findById/remove 全乱。
     */
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
            return map.remove(id, map.get(id));  // remove(key, value) 仅当映射为该值才删
        }
        return false;
    }

    public int count() {
        return map.size();
    }
}
