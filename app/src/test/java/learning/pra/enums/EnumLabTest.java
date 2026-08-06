package learning.pra.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EnumLabTest {

    @Nested
    @DisplayName("action: switch 表达式按颜色返回动作")
    class ActionTest {
        @Test
        @DisplayName("红灯 -> 停")
        void redStops() {
            assertEquals("停", EnumLab.action(EnumLab.TrafficLight.RED));
        }

        @Test
        @DisplayName("绿灯 -> 行")
        void greenGoes() {
            assertEquals("行", EnumLab.action(EnumLab.TrafficLight.GREEN));
        }

        @Test
        @DisplayName("黄灯 -> 慢")
        void yellowSlows() {
            assertEquals("慢", EnumLab.action(EnumLab.TrafficLight.YELLOW));
        }
    }

    @Nested
    @DisplayName("allNames: values() 顺序即声明顺序")
    class AllNamesTest {
        @Test
        @DisplayName("返回 [RED, GREEN, YELLOW]")
        void returnsAllInDeclarationOrder() {
            List<String> names = EnumLab.allNames();
            assertEquals(3, names.size(), "应正好三个常量");
            assertEquals("RED", names.get(0));
            assertEquals("GREEN", names.get(1));
            assertEquals("YELLOW", names.get(2));
        }

        @Test
        @DisplayName("用 contains 验证三个名字都在")
        void containsAllNames() {
            List<String> names = EnumLab.allNames();
            assertTrue(names.contains("RED"));
            assertTrue(names.contains("GREEN"));
            assertTrue(names.contains("YELLOW"));
        }
    }

    @Nested
    @DisplayName("parse: valueOf 包裹，找不到返回 null")
    class ParseTest {
        @Test
        @DisplayName("parse(\"RED\") 返回 RED 常量")
        void parsesValidName() {
            EnumLab.TrafficLight result = EnumLab.parse("RED");
            assertNotNull(result);
            assertSame(EnumLab.TrafficLight.RED, result);
        }

        @Test
        @DisplayName("parse 找不到的名字返回 null（不抛异常）")
        void parsesInvalidNameReturnsNull() {
            // 你当前 parse 没写 try-catch，这行会抛 IllegalArgumentException 让测试失败
            assertNull(EnumLab.parse("BLUE"), "无效名字应返回 null，不应抛异常");
        }

        @Test
        @DisplayName("parse 返回的常量 == 静态常量（验证单例）")
        void parsedInstanceIsSameSingleton() {
            assertSame(EnumLab.TrafficLight.RED, EnumLab.parse("RED"));
            assertSame(EnumLab.TrafficLight.GREEN, EnumLab.parse("GREEN"));
            assertSame(EnumLab.TrafficLight.YELLOW, EnumLab.parse("YELLOW"));
        }
    }

    @Nested
    @DisplayName("对比: valueOf 抛异常 vs parse 返回 null")
    class ValueOfVsParseTest {
        @Test
        @DisplayName("valueOf(\"BLUE\") 抛 IllegalArgumentException")
        void valueOfThrowsOnInvalidName() {
            assertThrows(IllegalArgumentException.class,
                    () -> EnumLab.TrafficLight.valueOf("BLUE"));
        }

        @Test
        @DisplayName("parse(\"BLUE\") 不抛异常，返回 null")
        void parseDoesNotThrowOnInvalidName() {
            assertDoesNotThrow(() -> EnumLab.parse("BLUE"));
            assertNull(EnumLab.parse("BLUE"));
        }
    }

    @Nested
    @DisplayName("Enum 自带方法验证（呼应 §10.1）")
    class BuiltinMethodsTest {
        @Test
        @DisplayName("name() 返回常量名字字符串")
        void nameReturnsString() {
            assertEquals("RED", EnumLab.TrafficLight.RED.name());
            assertEquals("GREEN", EnumLab.TrafficLight.GREEN.name());
        }

        @Test
        @DisplayName("ordinal() 是声明顺序下标，从 0 起")
        void ordinalIsDeclarationIndex() {
            assertEquals(0, EnumLab.TrafficLight.RED.ordinal());
            assertEquals(1, EnumLab.TrafficLight.GREEN.ordinal());
            assertEquals(2, EnumLab.TrafficLight.YELLOW.ordinal());
        }

        @Test
        @DisplayName("toString 默认同 name()")
        void toStringDefaultsToName() {
            assertEquals(EnumLab.TrafficLight.RED.name(),
                    EnumLab.TrafficLight.RED.toString());
        }

        @Test
        @DisplayName("枚举约定用 == 比较（与 equals 等价）")
        void equalsIsSameAsReferenceEquality() {
            EnumLab.TrafficLight a = EnumLab.TrafficLight.RED;
            EnumLab.TrafficLight b = EnumLab.TrafficLight.RED;
            assertSame(a, b, "== 应为 true（同一单例）");
            assertEquals(a, b, "equals 也应为 true");
        }

        @Test
        @DisplayName("compareTo 按 ordinal 比较")
        void compareToByOrdinal() {
            assertTrue(EnumLab.TrafficLight.RED.compareTo(EnumLab.TrafficLight.GREEN) < 0,
                    "RED 在 GREEN 前，compareTo 返回负数");
            assertTrue(EnumLab.TrafficLight.YELLOW.compareTo(EnumLab.TrafficLight.GREEN) > 0,
                    "YELLOW 在 GREEN 后，compareTo 返回正数");
        }
    }
}
