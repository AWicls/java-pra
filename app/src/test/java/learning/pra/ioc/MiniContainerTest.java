package learning.pra.ioc;

import java.lang.reflect.Field;
import java.util.Map;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * MiniContainer 行为测试——覆盖懒加载、单例/非单例、注册过滤、依赖注入、循环依赖。
 *
 * <p>内嵌 6 个测试用类作为"实验对象"：
 * <ul>
 *   <li>{@code UserService}：@Component 非单例 → 验证每次 getBean 都新建</li>
 *   <li>{@code UnregisteredService}：@Component 但从不 register → 验证未登记拒绝</li>
 *   <li>{@code SingletonOnly}：只标 @Singleton 不标 @Component → 验证非 @Component 不缓存</li>
 *   <li>{@code PlainClass}：无任何注解 → 验证 register 静默忽略</li>
 *   <li>{@code ServiceA}/{@code ServiceB}：互相注入 → 验证循环依赖检测</li>
 * </ul>
 */
class MiniContainerTest {

    // 测试内嵌的 @Component 非单例 Bean（等价于主代码里的 UserService）
    @Component
    public static class UserService {
    }

    // 标了 @Component 但从不 register 的类型（验证 getBean 必须拒绝未登记类型）
    @Component
    public static class UnregisteredService {
    }

    // 只标 @Singleton 不标 @Component 的类型（验证非 @Component 一律不缓存）
    @Singleton
    public static class SingletonOnly {
    }

    // 无任何注解的普通类（验证 register 静默忽略）
    public static class PlainClass {
    }

    // 互相注入的两个类（验证循环依赖检测）
    @Component
    public static class ServiceA {
        @Inject
        private ServiceB b;
    }

    @Component
    public static class ServiceB {
        @Inject
        private ServiceA a;
    }

    /** 单例 Bean：两次 getBean 返回同一实例（懒加载缓存生效）。 */
    @Test
    void 单例Bean多次获取同一实例() throws Exception {
        MiniContainer container = new MiniContainer();
        container.register(OrderService.class, OrderRepository.class);
        OrderService a = container.getBean(OrderService.class);
        OrderService b = container.getBean(OrderService.class);
        // 防 null 假通过：getBean 必须真的产出实例
        assertNotNull(a);
        // 单例：两次拿到同一实例
        assertSame(a, b);
    }

    /** 非单例 Bean：两次 getBean 返回不同实例（每次新建、不缓存）。 */
    @Test
    void 非单例Bean每次获取新实例() throws Exception {
        MiniContainer container = new MiniContainer();
        container.register(UserService.class);
        assertNotSame(container.getBean(UserService.class), container.getBean(UserService.class));
    }

    /** 非 @Component 的普通类：register 静默忽略、不抛异常。 */
    @Test
    void 非Component类注册被忽略() {
        MiniContainer container = new MiniContainer();
        assertDoesNotThrow(() -> container.register(PlainClass.class));
    }

    /** 完全未登记的类型（如 String）：getBean 抛 IllegalArgumentException。 */
    @Test
    void 未登记类型取Bean抛异常() {
        MiniContainer container = new MiniContainer();
        assertThrows(IllegalArgumentException.class, () -> container.getBean(String.class));
    }

    /** 标了 @Component 但从未 register 的类型：同样被拒绝（判断的是登记表，不是注解）。 */
    @Test
    void 标Component但未register的类型取Bean抛异常() {
        MiniContainer container = new MiniContainer();
        assertThrows(IllegalArgumentException.class, () -> container.getBean(UnregisteredService.class));
    }

    /** 只标 @Singleton 未标 @Component 的类型：register 后不得进入单例缓存（反射窥视私有字段验证）。 */
    @Test
    void 只标Singleton未标Component的类型不会被缓存() throws Exception {
        MiniContainer container = new MiniContainer();
        container.register(SingletonOnly.class);
        // 反射拿私有字段（第八课）
        Field f = MiniContainer.class.getDeclaredField("singletons");
        f.setAccessible(true);
        Map<?, ?> singletons = (Map<?, ?>) f.get(container);
        // 非 @Component 一律不进缓存
        assertTrue(singletons.isEmpty());
    }

    /** 注入成功：单例 Bean 的 @Inject 字段被容器塞入了依赖实例（反射读字段验证非 null）。 */
    @Test
    void 单例Bean注入了依赖字段() throws Exception {
        MiniContainer container = new MiniContainer();
        container.register(OrderService.class, OrderRepository.class);
        OrderService service = container.getBean(OrderService.class);
        Field f = OrderService.class.getDeclaredField("repository");
        f.setAccessible(true);
        // 依赖不是 null = 注入发生
        assertNotNull(f.get(service));
    }

    /** 单例注入的依赖是同一实例：两次 getBean(OrderService) 拿到的 repository 指向同一对象。 */
    @Test
    void 单例注入的依赖是同一实例() throws Exception {
        MiniContainer container = new MiniContainer();
        container.register(OrderService.class, OrderRepository.class);
        Field f = OrderService.class.getDeclaredField("repository");
        f.setAccessible(true);
        OrderService a = container.getBean(OrderService.class);
        OrderService b = container.getBean(OrderService.class);
        assertSame(f.get(a), f.get(b));
    }

    /** 循环依赖：A↔B 互相注入，getBean 抛清晰的 IllegalStateException 而非 StackOverflowError。 */
    @Test
    void 循环依赖抛清晰异常而非栈溢出() throws Exception {
        MiniContainer container = new MiniContainer();
        container.register(ServiceA.class, ServiceB.class);
        assertThrows(IllegalStateException.class, () -> container.getBean(ServiceA.class));
    }

    /** 黑盒验收：注入的依赖真的能用——调 createOrder 返回"已保存:咖啡"。 */
    @Test
    void 注入的依赖能被真正调用() throws Exception {
        MiniContainer container = new MiniContainer();
        container.register(OrderService.class, OrderRepository.class);
        OrderService service = container.getBean(OrderService.class);
        assertEquals("已保存:咖啡", service.createOrder("咖啡"));
    }
}
