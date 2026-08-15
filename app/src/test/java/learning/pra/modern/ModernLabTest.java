package learning.pra.modern;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ModernLab 现代特性（Records / Sealed Classes / Pattern Matching）的单元测试（第十六课）。
 *
 * <p>覆盖 Record 自动方法、紧凑构造器校验、sealed 分层 area、Pattern Matching describe
 * 的各类分支（整数/字符串/List/null/其他）。
 *
 * @see ModernLab
 */
class ModernLabTest {

    // ========== Point ==========

    @Test
    @DisplayName("Point_Record自动生成equals和toString")
    void point_自动方法() {
        ModernLab.Point p1 = new ModernLab.Point(3, 4);
        ModernLab.Point p2 = new ModernLab.Point(3, 4);
        assertEquals(p1, p2);
        assertEquals("Point[x=3, y=4]", p1.toString());
        assertEquals(3, p1.x());
        assertEquals(4, p1.y());
    }

    // ========== Age ==========

    @Test
    @DisplayName("Age_正常值创建成功")
    void age_正常() {
        ModernLab.Age age = new ModernLab.Age(18);
        assertEquals(18, age.value());
    }

    @Test
    @DisplayName("Age_负数抛IllegalArgumentException")
    void age_负数抛异常() {
        assertThrows(IllegalArgumentException.class, () -> new ModernLab.Age(-1));
    }

    // ========== area ==========

    @Test
    @DisplayName("area_圆面积=πr²")
    void area_圆() {
        // r=2，面积 = π * 4 ≈ 12.566
        ModernLab.Circle circle = new ModernLab.Circle(2.0);
        double result = ModernLab.area(circle);
        assertEquals(Math.PI * 4, result, 0.001);
    }

    @Test
    @DisplayName("area_矩形面积=width*height")
    void area_矩形() {
        // 3 × 4 = 12
        ModernLab.Rectangle rect = new ModernLab.Rectangle(3.0, 4.0);
        double result = ModernLab.area(rect);
        assertEquals(12.0, result, 0.001);
    }

    // ========== describe ==========

    @Test
    @DisplayName("describe_Integer返回整数描述")
    void describe_整数() {
        assertEquals("整数：42", ModernLab.describe(42));
    }

    @Test
    @DisplayName("describe_String返回字符串描述")
    void describe_字符串() {
        assertEquals("字符串: hello", ModernLab.describe("hello"));
    }

    @Test
    @DisplayName("describe_List返回列表描述")
    void describe_列表() {
        assertEquals("列表: 3个元素", ModernLab.describe(List.of(1, 2, 3)));
    }

    @Test
    @DisplayName("describe_null返回空")
    void describe_null() {
        assertEquals("空", ModernLab.describe(null));
    }

    @Test
    @DisplayName("describe_其他类型返回未知")
    void describe_其他() {
        assertEquals("未知类型", ModernLab.describe(3.14));
    }

    // ========== describeShapes ==========

    @Test
    @DisplayName("describeShapes_混合形状返回描述列表")
    void describeShapes_混合() {
        List<ModernLab.Shape> shapes = List.of(
            new ModernLab.Circle(2.0),
            new ModernLab.Rectangle(3.0, 4.0)
        );
        List<String> result = ModernLab.describeShapes(shapes);
        assertEquals(2, result.size());
        assertTrue(result.get(0).startsWith("圆形 面积="));
        assertTrue(result.get(1).startsWith("矩形 面积="));
    }

    @Test
    @DisplayName("describeShapes_空列表返回空List")
    void describeShapes_空列表() {
        List<String> result = ModernLab.describeShapes(List.of());
        assertTrue(result.isEmpty());
    }
}
