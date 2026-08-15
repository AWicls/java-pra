package learning.pra.ioc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AnnotationLabTest {

    @Test
    void 三个注解都是RUNTIME保留() {
        assertEquals(RetentionPolicy.RUNTIME, Component.class.getAnnotation(Retention.class).value());
        assertEquals(RetentionPolicy.RUNTIME , Inject.class.getAnnotation(Retention.class).value());
        assertEquals(RetentionPolicy.RUNTIME , Singleton.class.getAnnotation(Retention.class).value());
    }

    @Test
    void Target作用目标正确() {
        ElementType[] compTargets = Component.class.getAnnotation(Target.class).value();
        assertTrue(Arrays.asList(compTargets ).contains(ElementType.TYPE));
        ElementType[] injectTargets = Inject.class.getAnnotation(Target.class).value();
        assertTrue(Arrays.asList(injectTargets ).contains(ElementType.FIELD));
    }

    @Test
    void OrderService挂了组件注解() {
        assertNotNull(OrderService.class.getAnnotation(Component.class));
        assertNotNull(OrderService.class.getAnnotation(Singleton.class));
    }

    @Test
    void repository字段挂了Inject() throws Exception {
        assertNotNull(OrderService.class.getDeclaredField("repository").getAnnotation(Inject.class));
    }
}
