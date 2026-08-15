package learning.pra.ioc;

import java.lang.reflect.Field;
import java.util.Map;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void 单例Bean多次获取同一实例() throws Exception {
        MiniContainer container = new MiniContainer();
        container.register(OrderService.class, OrderRepository.class);
        OrderService a = container.getBean(OrderService.class);
        OrderService b = container.getBean(OrderService.class);
        assertNotNull(a);
        assertSame(a, b);
    }

    @Test
    void 非单例Bean每次获取新实例() throws Exception {
        MiniContainer container = new MiniContainer();
        container.register(UserService.class);
        assertNotSame(container.getBean(UserService.class), container.getBean(UserService.class));
    }

    @Test
    void 非Component类注册被忽略() {
        MiniContainer container = new MiniContainer();
        assertDoesNotThrow(() -> container.register(PlainClass.class));
    }

    @Test
    void 未登记类型取Bean抛异常() {
        MiniContainer container = new MiniContainer();
        assertThrows(IllegalArgumentException.class, () -> container.getBean(String.class));
    }

    @Test
    void 标Component但未register的类型取Bean抛异常() {
        MiniContainer container = new MiniContainer();
        assertThrows(IllegalArgumentException.class, () -> container.getBean(UnregisteredService.class));
    }

    @Test
    void 只标Singleton未标Component的类型不会被缓存() throws Exception {
        MiniContainer container = new MiniContainer();
        container.register(SingletonOnly.class);
        Field f = MiniContainer.class.getDeclaredField("singletons");
        f.setAccessible(true);
        Map<?, ?> singletons = (Map<?, ?>) f.get(container);
        assertTrue(singletons.isEmpty());
    }

    @Test
    void 单例Bean注入了依赖字段() throws Exception {
        MiniContainer container = new MiniContainer();
        container.register(OrderService.class, OrderRepository.class);
        OrderService service = container.getBean(OrderService.class);
        Field f = OrderService.class.getDeclaredField("repository");
        f.setAccessible(true);
        assertNotNull(f.get(service));
    }

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

    @Test
    void 循环依赖抛清晰异常而非栈溢出() throws Exception {
        MiniContainer container = new MiniContainer();
        container.register(ServiceA.class, ServiceB.class);
        assertThrows(IllegalStateException.class, () -> container.getBean(ServiceA.class));
    }

    @Test
    void 注入的依赖能被真正调用() throws Exception {
        MiniContainer container = new MiniContainer();
        container.register(OrderService.class, OrderRepository.class);
        OrderService service = container.getBean(OrderService.class);
        assertEquals("已保存:咖啡", service.createOrder("咖啡"));
    }
}
