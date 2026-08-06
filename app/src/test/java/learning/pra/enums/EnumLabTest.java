package learning.pra.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import learning.pra.enums.EnumLab.AppConfig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EnumMap;
import java.util.EnumSet;
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

    @Nested
    @DisplayName("§10.5 枚举单例 + 反射攻击防御")
    class EnumSingletonTest {

        @Test
        @DisplayName("INSTANCE 是单例：两次取引用 == 相等")
        void instanceIsSingleton() {
            assertSame(AppConfig.INSTANCE, AppConfig.INSTANCE,
                    "枚举常量天生单例，两次访问必须是同一对象");
        }

        @Test
        @DisplayName("AppConfig get/set 基本功能")
        void configGetSetWorks() {
            AppConfig cfg = AppConfig.INSTANCE;
            assertEquals("java-pra", cfg.get("app.name"), "构造器初始化的值应能读到");
            assertEquals("1.0", cfg.get("app.version"));
            cfg.set("custom.key", "custom-value");
            assertEquals("custom-value", cfg.get("custom.key"));
        }

        @Test
        @DisplayName("防线 1: 反射 newInstance 攻击 -> 抛 IllegalArgumentException")
        void reflectAttackFails() throws Exception {
            // 枚举编译器自动加 (String name, int ordinal) 构造器（呼应 §10.1 字节码）
            Constructor<AppConfig> c = AppConfig.class.getDeclaredConstructor(String.class, int.class);
            c.setAccessible(true);                // 破防尝试
            // JVM 层面硬编码：枚举类的 newInstance 直接拒绝
            assertThrows(IllegalArgumentException.class,
                    () -> c.newInstance("FAKE", 0),
                    "枚举构造器 newInstance 必须抛 IllegalArgumentException");
        }

        @Test
        @DisplayName("防线 2: clone() 被 protected + 模块系统双重保护，外部无法调用")
        void cloneFails() throws Exception {
            // Enum.clone() 是 protected final，从 java.lang.Enum 继承
            // 1) AppConfig 自己没声明 clone（继承父类的）-> getDeclaredMethod 本类找不到
            assertThrows(NoSuchMethodException.class,
                    () -> AppConfig.class.getDeclaredMethod("clone"),
                    "AppConfig 没自己声明 clone，是从 Enum 继承的");

            // 2) clone 是 protected，跨包不可见 -> getMethod（只查 public）也找不到
            assertThrows(NoSuchMethodException.class,
                    () -> AppConfig.class.getMethod("clone"),
                    "clone 是 protected，跨包测试类不可见，getMethod 拿不到");

            // 3) 即使用反射强拿 Enum.clone()，JDK 9+ 模块系统会拦 setAccessible
            //    （java.base 不 opens java.lang 给 unnamed module -> InaccessibleObjectException）
            //    这正是 Enum 源码里 clone() final + 抛 CloneNotSupportedException 的双重保险：
            //    即使模块放开，调 clone 也会抛 CloneNotSupportedException
            Method cloneMethod = Enum.class.getDeclaredMethod("clone");
            assertThrows(java.lang.reflect.InaccessibleObjectException.class,
                    () -> cloneMethod.setAccessible(true),
                    "JDK 9+ 模块系统拦截：java.base 不 opens java.lang 给外部模块");
        }

        @Test
        @DisplayName("防线 3: 反序列化后仍是同一实例（不走 readObject 创建新对象）")
        void serializationKeepsSingleton() throws Exception {
            AppConfig original = AppConfig.INSTANCE;
            // 序列化到内存字节流（用 ByteArrayOutputStream 避开沙箱 /tmp 只读陷阱）
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(original);
            }
            byte[] bytes = baos.toByteArray();

            // 反序列化
            try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
                AppConfig deserialized = (AppConfig) ois.readObject();
                // 关键：反序列化后 == 仍成立（JVM 调 valueOf 返回已存在实例，不 new）
                assertSame(original, deserialized,
                        "枚举反序列化必须返回同一实例，不能创建新对象");
            }
        }

        @Test
        @DisplayName("对照: 普通类的反射不拒绝创建多实例（反证枚举防御的价值）")
        void normalClassReflectAttackSucceeds() throws Exception {
            // 用 ArrayList 做对照：反射能 new 出两个不同实例
            // 对照点：枚举的 newInstance 被 JVM 硬编码拒绝，普通类不拒绝
            Constructor<java.util.ArrayList<Object>> c =
                    (Constructor<java.util.ArrayList<Object>>) (Constructor<?>) java.util.ArrayList.class.getDeclaredConstructor();
            c.setAccessible(true);
            java.util.ArrayList<Object> a = c.newInstance();
            java.util.ArrayList<Object> b = c.newInstance();
            assertNotSame(a, b, "普通类反射能创建不同实例 -> 枚举的免疫才是特殊的");
        }
    }

    @Nested
    @DisplayName("§10.6 策略模式: 支付方式")
    class PaymentStrategyTest {

        @Test
        @DisplayName("ALIPAY 支付宝: 1% 手续费")
        void alipayFee() {
            EnumLab.PayResult r = EnumLab.PaymentMethod.ALIPAY.pay(100);
            assertTrue(r.success());
            assertEquals(99.0, r.actualAmount(), 0.001);   // 100 * (1 - 0.01)
            assertTrue(r.message().contains("支付宝"));
            assertTrue(r.message().contains("即时到账"));
        }

        @Test
        @DisplayName("WECHAT 微信: 0.5% 手续费")
        void wechatFee() {
            EnumLab.PayResult r = EnumLab.PaymentMethod.WECHAT.pay(100);
            assertTrue(r.success());
            assertEquals(99.5, r.actualAmount(), 0.001);   // 100 * (1 - 0.005)
            assertTrue(r.message().contains("微信"));
        }

        @Test
        @DisplayName("BANK_CARD 银行卡: 固定 2 元手续费")
        void bankCardFixedFee() {
            EnumLab.PayResult r = EnumLab.PaymentMethod.BANK_CARD.pay(100);
            assertTrue(r.success());
            assertEquals(98.0, r.actualAmount(), 0.001);   // 100 - 2
            assertTrue(r.message().contains("T+1"));
        }

        @Test
        @DisplayName("CREDIT_CARD 信用卡: 3% 手续费")
        void creditCardFee() {
            EnumLab.PayResult r = EnumLab.PaymentMethod.CREDIT_CARD.pay(100);
            assertTrue(r.success());
            assertEquals(97.0, r.actualAmount(), 0.001);   // 100 * (1 - 0.03)
            assertTrue(r.message().contains("T+3"));
        }

        @Test
        @DisplayName("限额边界: 刚好等于限额 -> 成功")
        void atLimitSucceeds() {
            EnumLab.PayResult r = EnumLab.PaymentMethod.ALIPAY.pay(50000);
            assertTrue(r.success(), "刚好等于限额应成功");
        }

        @Test
        @DisplayName("限额边界: 超出限额 -> 失败")
        void overLimitFails() {
            EnumLab.PayResult r = EnumLab.PaymentMethod.ALIPAY.pay(50001);
            assertFalse(r.success(), "超出限额应失败");
            assertEquals(0.0, r.actualAmount(), 0.001, "失败时 actualAmount=0");
            assertTrue(r.message().contains("超过单笔限额"), "应提示限额信息");
        }

        @Test
        @DisplayName("各方式限额不同：信用卡 2 万最低")
        void differentLimits() {
            assertEquals(50000, EnumLab.PaymentMethod.ALIPAY.getLimit());
            assertEquals(30000, EnumLab.PaymentMethod.WECHAT.getLimit());
            assertEquals(100000, EnumLab.PaymentMethod.BANK_CARD.getLimit());
            assertEquals(20000, EnumLab.PaymentMethod.CREDIT_CARD.getLimit());
        }

        @Test
        @DisplayName("上下文切换策略: 同一 context 切 method 后行为变化")
        void contextSwitchesStrategy() {
            EnumLab.PaymentContext ctx = new EnumLab.PaymentContext(EnumLab.PaymentMethod.ALIPAY);
            EnumLab.PayResult r1 = ctx.checkout(100);
            assertEquals(99.0, r1.actualAmount(), 0.001);

            ctx.setMethod(EnumLab.PaymentMethod.BANK_CARD);
            EnumLab.PayResult r2 = ctx.checkout(100);
            assertEquals(98.0, r2.actualAmount(), 0.001, "切到银行卡后手续费逻辑变了");
        }

        @Test
        @DisplayName("多态遍历: 每个方式都能处理支付")
        void polymorphicIteration() {
            for (EnumLab.PaymentMethod m : EnumLab.PaymentMethod.values()) {
                EnumLab.PayResult r = m.pay(1000);
                assertNotNull(r);
                assertTrue(r.success(), m.name() + " 支付 1000 应成功（在限额内）");
                assertTrue(r.actualAmount() > 0, m.name() + " 应有正的到账金额");
            }
        }

        @Test
        @DisplayName("record 不可变性: success/message/actualAmount 都是 final")
        void recordImmutable() {
            EnumLab.PayResult r = new EnumLab.PayResult(true, "test", 100.0);
            assertTrue(r.success());
            assertEquals("test", r.message());
            assertEquals(100.0, r.actualAmount(), 0.001);
            // record 没有 setter，访问器只读 -> 不可变
        }
    }
}
