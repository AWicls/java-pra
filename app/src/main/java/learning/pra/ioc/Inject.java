package learning.pra.ioc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 依赖注入标记注解：标在字段上，表示"这个字段的依赖由容器注入"（相当于 Spring 的 {@code @Autowired}）。
 *
 * <p>{@link MiniContainer} 创建完 Bean 后，会调用注入逻辑遍历标了本注解的字段，
 * 用反射 {@code field.setAccessible(true)} + {@code field.set(bean, 依赖实例)} 把依赖塞进去。
 *
 * <p>{@code @Target(FIELD)}：只能标在字段上，标到类上会编译报错；
 * {@code @Retention(RUNTIME)}：反射能读到。
 *
 * @see Component
 * @see MiniContainer
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Inject {

}
