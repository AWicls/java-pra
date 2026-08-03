package learning.pra.modern;

import java.util.*;

public final class ModernLab {
    private ModernLab() {
    }

    // ========== 1. Record ==========

    /** 一个表示二维点的 Record */
    public record Point(int x, int y) {
    }

    /** 一个表示年龄的 Record，构造时校验非负 */
    public record Age(int value) {
        // 紧凑构造器：value < 0 抛 IllegalArgumentException
        public Age {
            if (value < 0)
                throw new IllegalArgumentException();
        }
    }

    // ========== 2. Sealed + Record 实现 Shape ==========

    /** 密封接口 Shape，permits Circle, Rectangle */
    public sealed interface Shape permits Circle, Rectangle {
    }

    public record Circle(double radius) implements Shape {
    }

    public record Rectangle(double width, double height) implements Shape {
    }

    /**
     * 用 Pattern Matching for switch 计算 Shape 面积
     * Circle: πr²，Rectangle: width * height
     * 不需要 default（Sealed 保证穷尽）
     */
    public static double area(Shape shape) {
        return switch (shape) {
            case null -> {throw new NoSuchElementException();}
            case Circle r -> Math.PI * r.radius() * r.radius();
            case Rectangle rect -> rect.width() * rect.height();
        };
    }

    // ========== 3. Pattern Matching for instanceof ==========

    /**
     * 根据对象类型返回描述字符串
     * Integer -> "整数: x"
     * String -> "字符串: s"
     * {@code List<?>} -> "列表: size个元素"
     * null -> "空"
     * 其他 -> "未知类型"
     */
    public static String describe(Object obj) {
        return switch (obj) {
            case null -> "空";
            case Integer i -> "整数：" + i;
            case String s -> "字符串: " + s;
            case List<?> list -> "列表: " + list.size() + "个元素";
            default -> "未知类型";
        };
    }

    // ========== 4. 综合应用 ==========

    /**
     * 给定一组 Shape，用 Stream + Pattern Matching 返回每个形状的描述
     * 例 "[Circle(r=2.0), Rectangle(3.0x4.0)]" -> ["圆形 面积=12.57", "矩形 面积=12.0"]
     */
    public static List<String> describeShapes(List<Shape> shapes) {
        return shapes.stream().map(shape -> switch (shape) {
            case Circle c -> "圆形 面积=" + area(c);          // c 是 Circle
            case Rectangle r -> "矩形 面积=" + area(r);        // r 是 Rectangle
        })
        .toList();
    }
}
