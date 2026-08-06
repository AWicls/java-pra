package learning.pra.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;

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

    @Nested
    @DisplayName("§10.3 Operation: 每常量独立实现抽象方法（策略模式雏形）")
    class OperationTest {
        @Test
        @DisplayName("PLUS 加法")
        void plus() {
            assertEquals(5.0, EnumLab.Operation.PLUS.apply(2, 3));
        }

        @Test
        @DisplayName("MINUS 减法")
        void minus() {
            assertEquals(3.0, EnumLab.Operation.MINUS.apply(5, 2));
        }

        @Test
        @DisplayName("TIMES 乘法")
        void times() {
            assertEquals(12.0, EnumLab.Operation.TIMES.apply(3, 4));
        }

        @Test
        @DisplayName("DIVIDE 除法（正常）")
        void divide() {
            assertEquals(5.0, EnumLab.Operation.DIVIDE.apply(10, 2));
        }

        @Test
        @DisplayName("DIVIDE 浮点除零返回 Infinity（Java 浮点不抛异常）")
        void divideByZeroReturnsInfinity() {
            double result = EnumLab.Operation.DIVIDE.apply(1, 0);
            assertTrue(Double.isInfinite(result),
                    "1.0/0.0 应返回 Infinity，实际: " + result);
        }

        @Test
        @DisplayName("DIVIDE 零除零返回 NaN")
        void zeroDividedByZeroReturnsNaN() {
            double result = EnumLab.Operation.DIVIDE.apply(0, 0);
            assertTrue(Double.isNaN(result),
                    "0.0/0.0 应返回 NaN，实际: " + result);
        }

        @Test
        @DisplayName("多态调用：Operation 引用调 apply，无需 switch")
        void polymorphicDispatch() {
            EnumLab.Operation op = EnumLab.Operation.PLUS;
            assertEquals(2.0, op.apply(1, 1),
                    "父类引用调 apply，运行期分到 PLUS 实现");
        }

        @Test
        @DisplayName("遍历所有运算，每个都正常返回")
        void iterateAll() {
            EnumLab.Operation[] ops = EnumLab.Operation.values();
            assertEquals(4, ops.length, "应有 4 个运算");
            for (EnumLab.Operation op : ops) {
                double result = op.apply(6, 3);
                assertTrue(Double.isFinite(result) || Double.isInfinite(result),
                        op.name() + " apply 应返回有效数值");
            }
        }
    }

    @Nested
    @DisplayName("§10.4 EnumSet / EnumMap")
    class EnumSetEnumMapTest {
        @Test
        @DisplayName("adminPermissions 含全部 4 个权限")
        void adminHasAllPermissions() {
            EnumSet<EnumLab.Permission> admin = EnumLab.adminPermissions();
            assertEquals(4, admin.size());
            assertTrue(admin.contains(EnumLab.Permission.READ));
            assertTrue(admin.contains(EnumLab.Permission.WRITE));
            assertTrue(admin.contains(EnumLab.Permission.DELETE));
            assertTrue(admin.contains(EnumLab.Permission.EXECUTE));
        }

        @Test
        @DisplayName("guestPermissions 只含 READ")
        void guestHasOnlyRead() {
            EnumSet<EnumLab.Permission> guest = EnumLab.guestPermissions();
            assertEquals(1, guest.size());
            assertTrue(guest.contains(EnumLab.Permission.READ));
            assertFalse(guest.contains(EnumLab.Permission.WRITE));
            assertFalse(guest.contains(EnumLab.Permission.DELETE));
            assertFalse(guest.contains(EnumLab.Permission.EXECUTE));
        }

        @Test
        @DisplayName("hasPermission: 访客没 WRITE 权限")
        void guestHasNoWritePermission() {
            assertFalse(EnumLab.hasPermission(EnumLab.guestPermissions(), EnumLab.Permission.WRITE));
            assertTrue(EnumLab.hasPermission(EnumLab.guestPermissions(), EnumLab.Permission.READ));
        }

        @Test
        @DisplayName("hasPermission: 管理员有全部权限")
        void adminHasAllPermissionChecks() {
            EnumSet<EnumLab.Permission> admin = EnumLab.adminPermissions();
            for (EnumLab.Permission p : EnumLab.Permission.values()) {
                assertTrue(EnumLab.hasPermission(admin, p), "管理员应有 " + p);
            }
        }

        @Test
        @DisplayName("permissionDescriptions.get(DELETE) 返回 删除")
        void permissionDescriptionsMapping() {
            EnumMap<EnumLab.Permission, String> desc = EnumLab.permissionDescriptions();
            assertEquals("读取", desc.get(EnumLab.Permission.READ));
            assertEquals("写入", desc.get(EnumLab.Permission.WRITE));
            assertEquals("删除", desc.get(EnumLab.Permission.DELETE));
            assertEquals("执行", desc.get(EnumLab.Permission.EXECUTE));
        }

        @Test
        @DisplayName("EnumMap 大小 == 枚举值数")
        void enumMapSizeMatchesEnumCount() {
            assertEquals(4, EnumLab.permissionDescriptions().size());
        }

        @Test
        @DisplayName("grantPermission 返回新 Set，原 Set 不变")
        void grantPermissionDoesNotMutateOriginal() {
            EnumSet<EnumLab.Permission> guest = EnumLab.guestPermissions();
            EnumSet<EnumLab.Permission> granted = EnumLab.grantPermission(guest, EnumLab.Permission.WRITE);

            // 新 Set 应含 READ + WRITE
            assertEquals(2, granted.size());
            assertTrue(granted.contains(EnumLab.Permission.WRITE));
            // 原 Set 仍只有 READ（拷贝验证）
            assertEquals(1, guest.size());
            assertFalse(guest.contains(EnumLab.Permission.WRITE));
        }

        @Test
        @DisplayName("EnumSet 迭代顺序 = 枚举声明顺序（非插入顺序）")
        void enumSetIterationIsDeclarationOrder() {
            EnumSet<EnumLab.Permission> custom = EnumSet.of(
                    EnumLab.Permission.EXECUTE,   // 故意倒序插入
                    EnumLab.Permission.READ,
                    EnumLab.Permission.DELETE,
                    EnumLab.Permission.WRITE
            );
            // 迭代应按 ordinal 顺序: READ(0), WRITE(1), DELETE(2), EXECUTE(3)
            java.util.List<EnumLab.Permission> iter = new java.util.ArrayList<>(custom);
            assertEquals(EnumLab.Permission.READ,    iter.get(0));
            assertEquals(EnumLab.Permission.WRITE,   iter.get(1));
            assertEquals(EnumLab.Permission.DELETE,  iter.get(2));
            assertEquals(EnumLab.Permission.EXECUTE, iter.get(3));
        }

        @Test
        @DisplayName("EnumSet.of 顺序无关，containsAll 一致")
        void enumSetOrderIndependent() {
            EnumSet<EnumLab.Permission> a = EnumSet.of(EnumLab.Permission.READ, EnumLab.Permission.WRITE);
            EnumSet<EnumLab.Permission> b = EnumSet.of(EnumLab.Permission.WRITE, EnumLab.Permission.READ);
            assertEquals(a, b, "EnumSet 不关心插入顺序，内容相等即相等");
        }
    }
}
