package learning.pra.ioc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 组件标记注解：标在类上，表示"这个类由容器管理"（相当于 Spring 的 {@code @Component}）。
 *
 * <p>容器 {@link MiniContainer#register(Class...)} 会用
 * {@code isAnnotationPresent(Component.class)} 扫描标了本注解的类并收编进登记表，
 * 之后才能被 {@link MiniContainer#getBean(Class)} 产出和注入。
 *
 * <ul>
 *   <li>{@code @Target(TYPE)}：只能标在类/接口上，标到字段上会编译报错</li>
 *   <li>{@code @Retention(RUNTIME)}：保留到运行期，反射才能读到（默认 CLASS 级别反射读不到）</li>
 * </ul>
 *
 * @see Inject
 * @see Singleton
 * @see MiniContainer
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Component {

}
