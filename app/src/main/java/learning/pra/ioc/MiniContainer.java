package learning.pra.ioc;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 迷你 IoC 容器（简化版 Spring 容器核心原理）。
 *
 * <p>职责一条线：<b>登记</b>标了 {@link Component} 的类 → <b>产出</b>实例 →
 * <b>注入</b>标了 {@link Inject} 的字段 → <b>缓存</b>标了 {@link Singleton} 的单例。
 *
 * <p><b>设计要点——懒加载</b>：{@link #register(Class...)} 只登记、不创建；
 * {@link #getBean(Class)} 首次调用才"创建 + 注入 + 缓存"。依赖靠 getBean 递归解析，
 * 所以 register 的先后顺序无关（饿汉式则会顺序敏感）。
 *
 * <p><b>三道防线</b>：
 * <ol>
 *   <li>未登记的类型 getBean 抛 {@link IllegalArgumentException}</li>
 *   <li>判断依据是登记表 {@code components}，而非"类带不带注解"——标了
 *       {@code @Component} 但没 register 的类型同样被拒绝</li>
 *   <li>互相注入的循环依赖抛 {@link IllegalStateException}（而不是 StackOverflowError）</li>
 * </ol>
 *
 * <p>用到的设计模式：单例模式（singletons 缓存）、工厂模式（getBean 按类型产出）、
 * IoC 控制反转（依赖创建权从类交给容器）。
 *
 * @see Component
 * @see Inject
 * @see Singleton
 * @see OrderService
 */
public class MiniContainer {
    /** 单例成品柜：{@link Singleton} 类的实例缓存，getBean 命中直接返回同一实例。 */
    private Map<Class<?>, Object> singletons = new HashMap<>();
    /** 登记表：所有被收编的 {@code @Component} 类型，getBean 只认这张表。 */
    private Set<Class<?>> components = new HashSet<>();
    /** 创建中集合：正在创建的类在此登记；重复 add 失败 = 循环依赖。 */
    private final Set<Class<?>> creating = new HashSet<>();

    /**
     * 可选演示入口：可在此 register + getBean + 调业务方法，跑通容器全流程。
     *
     * @param args 命令行参数（未使用）
     */
    public static void main(String[] args) {
        // 示例（取消注释即可跑）：
        // MiniContainer container = new MiniContainer();
        // container.register(OrderService.class, OrderRepository.class);
        // OrderService service = container.getBean(OrderService.class);
        // System.out.println(service.createOrder("咖啡"));
    }

    /**
     * 登记一批类：只有标了 {@link Component} 的类才会被收编进登记表。
     *
     * <p>本方法<b>只登记、不创建任何实例</b>——这是懒加载的前提，
     * 也保证依赖可以靠 getBean 递归解析、与登记顺序无关。
     *
     * @param type 待登记的类（可变参数，可一次传多个）
     * @throws InstantiationException  实例化失败（如抽象类/接口）时抛出
     * @throws IllegalAccessException  构造器不可访问时抛出
     * @throws InvocationTargetException 构造器内部抛异常时包装抛出
     * @throws NoSuchMethodException   找不到无参构造器时抛出
     */
    public void register(Class<?>... type) throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
        for (Class<?> clazz : type) {
            // 只有 @Component 才算 Bean；普通类（如工具类）静默忽略、不报错
            if (clazz.isAnnotationPresent(Component.class)) {
                components.add(clazz);
            }
        }
    }

    /**
     * 取一个 Bean 实例（懒加载：首次"创建 + 注入 + 缓存"）。
     *
     * <p>执行流程（对应概念点 3 的流程图）：
     * ① 未登记拒绝 → ② 缓存命中直接返回 → ③ 循环依赖检测 →
     * ④ newInstance 创建空壳 → ⑤ injectDependencies 注入 → ⑥ 单例才缓存 →
     * ⑦ finally 清理创建中标记。
     *
     * @param type 要取的 Bean 类型
     * @param <T>  Bean 的类型参数（由调用方推断）
     * @return 该类型的实例：单例为缓存实例，非单例每次新建
     * @throws IllegalArgumentException 类型未登记时抛出
     * @throws IllegalStateException    检测到循环依赖时抛出
     * @throws InstantiationException  实例化失败时抛出
     * @throws IllegalAccessException  构造器不可访问时抛出
     * @throws InvocationTargetException 构造器内部抛异常时包装抛出
     * @throws NoSuchMethodException   找不到无参构造器时抛出
     */
    public <T> T getBean(Class<T> type) throws InstantiationException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException {
        // ① 登记表里没有 → 拒绝（注意：判断的是"登没登记"，不是"带不带注解"）
        if (!components.contains(type)) {
            throw new IllegalArgumentException("未登记的类型: " + type.getName());
        }
        // ② 单例已缓存 → 直接复用，不再创建
        if (singletons.containsKey(type)) {
            return (T) singletons.get(type);
        }
        // ③ 该类型已在"创建中"集合里又被人要 = 循环依赖（A→B→A）
        if (!creating.add(type)) {
            throw new IllegalStateException("检测到循环依赖: " + type.getName());
        }
        try {
            // ④ 懒：第一次才创建空壳（无参构造器）
            T bean = (T) type.getDeclaredConstructor().newInstance();
            // ⑤ 创建完立刻注入 @Inject 字段（递归：依赖的依赖也注入）
            injectDependencies(bean);
            // ⑥ 只有标 @Singleton 的才入缓存；非单例不缓存、每次新建
            if (type.isAnnotationPresent(Singleton.class)) {
                singletons.put(type, bean);
            }
            return bean;
        } finally {
            // ⑦ 无论成功还是抛异常都要移除，避免污染后续请求（误报循环依赖）
            creating.remove(type);
        }
    }

    /**
     * 注入一个 Bean 的所有 {@code @Inject} 字段（依赖的依赖递归解析）。
     *
     * <p>遍历该类的全部声明字段，标了 {@link Inject} 的就：
     * {@code setAccessible(true)} 破除 private 访问限制（第八课反射），
     * 再用 {@code getBean(字段类型)} 取依赖实例塞进字段。
     *
     * @param bean 要注入的 Bean 实例
     * @throws IllegalAccessException  字段不可访问时抛出
     * @throws InstantiationException  依赖实例化失败时抛出
     * @throws InvocationTargetException 依赖构造器内部抛异常时包装抛出
     * @throws NoSuchMethodException   依赖找不到无参构造器时抛出
     */
    private void injectDependencies(Object bean)
            throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            // 只处理标了 @Inject 的字段；没有 @Inject 字段的类（如 OrderRepository）空转无害
            if (field.isAnnotationPresent(Inject.class)) {
                // 破 private 访问
                field.setAccessible(true);
                // 递归注入：依赖的依赖也注入
                field.set(bean, getBean(field.getType()));
            }
        }
    }
}
