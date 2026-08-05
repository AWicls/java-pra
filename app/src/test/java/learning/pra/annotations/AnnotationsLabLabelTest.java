package learning.pra.annotations;

import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AnnotationsLabLabelTest {

    @Test
    void label_is_runtime_retention() {
        // 反射读取 @Label 上的 @Retention 元注解，验证是 RUNTIME（否则反射读不到）
        Retention r = AnnotationsLab.Label.class.getAnnotation(Retention.class);
        assertNotNull(r, "@Label 必须贴 @Retention 元注解");
        assertEquals(RetentionPolicy.RUNTIME, r.value(),
                "@Label 必须是 RUNTIME 保留才能被反射读到");
    }

    @Test
    void label_target_is_field_only() {
        // 反射读取 @Label 上的 @Target 元注解，验证只能贴在字段上
        Target t = AnnotationsLab.Label.class.getAnnotation(Target.class);
        assertNotNull(t, "@Label 必须贴 @Target 元注解");
        ElementType[] types = t.value();
        boolean hasField = false;
        for (ElementType et : types) {
            if (et == ElementType.FIELD) hasField = true;
        }
        assertTrue(hasField, "@Target 必须包含 FIELD");
    }

    @Test
    void readLabels_returns_fieldname_to_labelvalue() {
        Map<String, String> map = AnnotationsLab.readLabels(AnnotationsLab.User.class);
        assertEquals("用户名", map.get("name"), "name 字段的 Label 值应为 '用户名'");
        assertEquals("年龄", map.get("age"), "age 字段的 Label 值应为 '年龄'");
    }

    @Test
    void readLabels_handles_field_without_label() {
        // 边界：类有字段但没贴 @Label 时，readLabels 不应该 NPE 或收集该字段
        class NoLabel {
            String notAnnotated;
        }
        Map<String, String> map = AnnotationsLab.readLabels(NoLabel.class);
        // 没有 @Label 字段，map 应为空
        assertTrue(map.isEmpty(), "没贴 @Label 的字段不应被收集，map 应为空");
    }
}
