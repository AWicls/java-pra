package learning.pra.ioc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 注解三件套的定义验证——断言 @Component/@Inject/@Singleton 的元注解配置正确。
 *
 * <p>覆盖三类检查（概念点 1）：
 * <ul>
 *   <li>@Retention 都是 RUNTIME：注解活到运行期，反射才能读到</li>
 *   <li>@Target 作用目标正确：@Component→类、@Inject→字段</li>
 *   <li>OrderService 类及其 repository 字段正确挂了注解</li>
 * </ul>
 */
class AnnotationLabTest {

    /** @Retention 决定注解能活多久：RUNTIME 才可被反射读取（无 RUNTIME，IoC 无从谈起）。 */
    @Test
    void 三个注解都是RUNTIME保留() {
        assertEquals(RetentionPolicy.RUNTIME, Component.class.getAnnotation(Retention.class).value());
        assertEquals(RetentionPolicy.RUNTIME , Inject.class.getAnnotation(Retention.class).value());
        assertEquals(RetentionPolicy.RUNTIME , Singleton.class.getAnnotation(Retention.class).value());
    }

    /** @Target 限定注解能标在哪：标错位置（如 @Inject 标到类上）会编译报错。 */
    @Test
    void Target作用目标正确() {
        ElementType[] compTargets = Component.class.getAnnotation(Target.class).value();
        // @Component 只能标类
        assertTrue(Arrays.asList(compTargets ).contains(ElementType.TYPE));
        ElementType[] injectTargets = Inject.class.getAnnotation(Target.class).value();
        // @Inject 只能标字段
        assertTrue(Arrays.asList(injectTargets ).contains(ElementType.FIELD));
    }

    /** 业务类上确实挂了 @Component 和 @Singleton（容器据此登记 + 缓存）。 */
    @Test
    void OrderService挂了组件注解() {
        assertNotNull(OrderService.class.getAnnotation(Component.class));
        assertNotNull(OrderService.class.getAnnotation(Singleton.class));
    }

    /** repository 字段上确实挂了 @Inject（容器据此注入依赖）。 */
    @Test
    void repository字段挂了Inject() throws Exception {
        assertNotNull(OrderService.class.getDeclaredField("repository").getAnnotation(Inject.class));
    }
}
