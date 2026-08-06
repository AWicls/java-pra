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
        @DisplayName("返回 [红灯, 绿灯, 黄灯]（allNames 用 getDisplayName）")
        void returnsAllInDeclarationOrder() {
            List<String> names = EnumLab.allNames();
            assertEquals(3, names.size(), "应正好三个常量");
            assertEquals("红灯", names.get(0));
            assertEquals("绿灯", names.get(1));
            assertEquals("黄灯", names.get(2));
        }

        @Test
        @DisplayName("用 contains 验证三个中文名都在")
        void containsAllNames() {
            List<String> names = EnumLab.allNames();
            assertTrue(names.contains("红灯"));
            assertTrue(names.contains("绿灯"));
            assertTrue(names.contains("黄灯"));
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
        @DisplayName("toString 已被覆盖，返回 displayName（不再是 name）")
        void toStringOverriddenToDisplayName() {
            assertEquals("红灯", EnumLab.TrafficLight.RED.toString());
            assertEquals("绿灯", EnumLab.TrafficLight.GREEN.toString());
            assertEquals("黄灯", EnumLab.TrafficLight.YELLOW.toString());
        }

        @Test
        @DisplayName("name() 仍是英文名（不受 toString 覆盖影响）")
        void nameStillEnglish() {
            assertEquals("RED", EnumLab.TrafficLight.RED.name());
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

    @Nested
    @DisplayName("§10.2 字段/构造器/方法")
    class FieldsAndConstructorTest {
        @Test
        @DisplayName("每个常量携带 displayName 字段")
        void carriesDisplayName() {
            assertEquals("红灯", EnumLab.TrafficLight.RED.getDisplayName());
            assertEquals("绿灯", EnumLab.TrafficLight.GREEN.getDisplayName());
            assertEquals("黄灯", EnumLab.TrafficLight.YELLOW.getDisplayName());
        }

        @Test
        @DisplayName("每个常量携带 durationSeconds 字段")
        void carriesDurationSeconds() {
            assertEquals(30, EnumLab.TrafficLight.RED.getDurationSeconds());
            assertEquals(25, EnumLab.TrafficLight.GREEN.getDurationSeconds());
            assertEquals(5, EnumLab.TrafficLight.YELLOW.getDurationSeconds());
        }

        @Test
        @DisplayName("name() 与 getDisplayName() 独立：name 不受字段影响")
        void nameIndependentOfDisplayName() {
            assertNotEquals(EnumLab.TrafficLight.RED.name(),
                    EnumLab.TrafficLight.RED.getDisplayName(),
                    "name() 返回 RED，getDisplayName() 返回 红灯，两者不同");
        }
    }

    @Nested
    @DisplayName("§10.2 byDuration: 按秒数反查枚举")
    class ByDurationTest {
        @Test
        @DisplayName("byDuration(30) 返回 RED")
        void findsRedByDuration() {
            assertSame(EnumLab.TrafficLight.RED, EnumLab.TrafficLight.byDuration(30));
        }

        @Test
        @DisplayName("byDuration(5) 返回 YELLOW")
        void findsYellowByDuration() {
            assertSame(EnumLab.TrafficLight.YELLOW, EnumLab.TrafficLight.byDuration(5));
        }

        @Test
        @DisplayName("byDuration 找不到返回 null（不抛异常）")
        void returnsNullWhenNotFound() {
            assertNull(EnumLab.TrafficLight.byDuration(999));
            assertNull(EnumLab.TrafficLight.byDuration(0));
        }
    }

    @Nested
    @DisplayName("§10.2 allNames 行为：用 getDisplayName 返回中文名（已合并到 AllNamesTest）")
    class AllNamesAfterToStringOverrideTest {
        @Test
        @DisplayName("allNames 因 getDisplayName 返回中文名，顺序与声明一致")
        void returnsDisplayNamesInOrder() {
            List<String> names = EnumLab.allNames();
            assertEquals("红灯", names.get(0));
            assertEquals("绿灯", names.get(1));
            assertEquals("黄灯", names.get(2));
        }
    }
}
