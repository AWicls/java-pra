package learning.pra.ioc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 单例标记注解：标在类上，表示"这个 Bean 容器只创建一个实例，之后复用"。
 *
 * <p>{@link MiniContainer#getBean(Class)} 首次创建标了本注解的 Bean 后，
 * 会把实例缓存进 {@code singletons} 表；之后每次 getBean 都返回同一实例。
 * 没标本注解的 Bean 则每次 getBean 都新建（非单例）。
 *
 * <p>{@code @Target(TYPE)} + {@code @Retention(RUNTIME)}，无成员。
 *
 * @see Component
 * @see MiniContainer
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Singleton {

}
