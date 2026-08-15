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

    @Test
    void 单例Bean多次获取同一实例() throws Exception {
        MiniContainer container = new MiniContainer();
        container.register(OrderService.class);
        assertSame(container.getBean(OrderService.class), container.getBean(OrderService.class));
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
        assertDoesNotThrow(() -> container.register(OrderRepository.class));
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
}
