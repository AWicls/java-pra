# Java 学习笔记 - Day 7（2026-08-06）

> 学习项目：java-pra（JDK 25 + Gradle 9.6.1 + JUnit Jupiter 6.0.1）
> 进度：第十课（枚举 enum）-- 全 6 节完成
> 代码：[app/src/main/java/learning/pra/enums/EnumLab.java](app/src/main/java/learning/pra/enums/EnumLab.java)
> 测试：[app/src/test/java/learning/pra/enums/EnumLabTest.java](app/src/test/java/learning/pra/enums/EnumLabTest.java)
> 总计：56 个测试全绿

---

## 一、第十课：枚举（enum）

### 10.1 枚举初识 -- 命名常量的进化

#### 10.1.1 进化三阶段对比

| 维度 | 裸 `int` 常量 | 类型安全枚举模式 | `enum` 关键字 |
|------|-----------|----------------|--------------|
| 类型安全 | ❌ 传 99 不报错 | ✅ | ✅ |
| 样板代码 | 少 | 几十行（手写 equals/hashCode/compareTo）| **零** |
| 可遍历 | ❌ 维护数组 | ❌ 手写 | ✅ `values()` |
| 打印可读 | ❌ 打 `0` | ✅ 打 `RED` | ✅ |
| 单例保证 | ❌ 反射可破 | 部分 | ✅ JVM 保证 |

#### 10.1.2 枚举本质（语法糖）

`enum X { A, B }` 被编译器翻译成：
```java
final class X extends java.lang.Enum<X> {        // 隐式 final，继承 Enum
    public static final X A = new X("A", 0);    // 每常量是 public static final 实例 = 单例
    public static final X B = new X("B", 1);
    private X(String name, int ordinal) { super(name, ordinal); }  // 隐式 private
    public static X[] values() { return $VALUES.clone(); }          // 每次返回克隆
    public static X valueOf(String name) { return Enum.valueOf(X.class, name); }
}
```

**字节码实证**（`javap -p -c` 看到的）：
- 类签名：`public final class EnumLab$TrafficLight extends java.lang.Enum<EnumLab$TrafficLight>`
- 字段：每常量是 `public static final` + 私有 `$VALUES` 数组（缓存所有常量）
- `values()` 内部调 `$VALUES.clone()` -- **每次返回副本**，可放心改
- `valueOf(String)` 委托父类 `Enum.valueOf(Class, String)` -- 找不到抛 `IllegalArgumentException`
- 静态初始化块：`new X("RED", 0)` 创建实例 -- **类加载时执行，JVM 保证线程安全 + 只一次**

#### 10.1.3 四个关键事实

1. 枚举本质是类，继承 `java.lang.Enum`（抽象类）
2. 每个常量是该类的 `public static final` 实例 = 单例（类加载时创建）
3. 枚举类隐式 `final`，不能被继承
4. 构造器隐式 `private`，外部 `new` 不出来

#### 10.1.4 `java.lang.Enum` 自带方法

| 方法 | 作用 | 例 |
|------|------|----|
| `name()` | 常量名字符串（final 不可覆盖）| `RED.name()` -> `"RED"` |
| `ordinal()` | 声明顺序下标（从 0）| `RED.ordinal()` -> `0` |
| `toString()` | 默认同 `name()`，**可覆盖** | 覆盖后可返回中文名 |
| `compareTo(E)` | 按 `ordinal` 比（final）| `RED.compareTo(GREEN)` 负数 |
| `equals(Object)` | 就是 `==`（final）| - |
| 静态 `values()` | 所有常量数组（每次克隆）| `[RED, GREEN, YELLOW]` |
| 静态 `valueOf(String)` | 按名字找，找不到抛 `IllegalArgumentException` | - |

#### 10.1.5 枚举用 `==` 不用 `.equals()`

两者等价（`Enum.equals` 就是 `==`），但 `==` 更优：
- 不会 NPE（`null == RED` 是 `false`）
- 编译期类型检查（不同枚举类型 `==` 编译错）

#### 10.1.6 ⚠️ `ordinal()` 别当业务 ID

`ordinal()` 是声明顺序，重排就乱。要持久化就**显式加字段**：
```java
enum OrderStatus {
    CREATED(1), PAID(2);
    private final int code;
    OrderStatus(int code) { this.code = code; }
}
```

---

### 10.2 枚举的字段、构造器、方法

#### 10.2.1 语法

```java
enum TrafficLight {
    RED("红灯", 30), GREEN("绿灯", 25), YELLOW("黄灯", 5);  // 常量后加 (参数)
    private final String displayName;     // 字段，通常 final
    private final int durationSeconds;
    TrafficLight(String displayName, int durationSeconds) { ... }  // 隐式 private
}
```

#### 10.2.2 关键点

1. 常量声明 `NAME(arg1, arg2)`，参数传给构造器
2. 构造器**隐式 private**（写 `public`/`protected` 编译错）
3. 字段通常 `private final`（枚举常量天生不可变）
4. **常量列表末尾要分号 `;`**（有字段/构造器时必须）

#### 10.2.3 ⚠️ 陷阱：覆盖 `toString` 的连锁反应

覆盖 `toString()` 返回 `displayName` 后：
- `RED.toString()` 从 `"RED"` 变成 `"红灯"`
- 任何依赖 `toString` 的地方（日志、`allNames`、调试）行为都变

**教训**：`name()` 永远是英文名（不变），`toString()` 可覆盖（可变）。想要稳定调 `name()`，想要展示调 `toString()`/`getDisplayName()`。

---

### 10.3 枚举实现接口 + 每常量独立实现抽象方法

#### 10.3.1 两种写法

**写法 A：枚举实现接口**（统一契约，但还是要 switch 分发）
```java
interface Actionable { String action(); }
enum TrafficLight implements Actionable { ... 共用实现 }
```

**写法 B：枚举声明抽象方法，每常量独立实现**（策略模式雏形，**关键语法**）
```java
enum Operation {
    PLUS   { @Override public double apply(double a, double b) { return a + b; } },
    MINUS  { @Override public double apply(double a, double b) { return a - b; } };
    public abstract double apply(double a, double b);    // 枚举能声明 abstract
}
```

#### 10.3.2 关键认知

- 每个常量是枚举类的**匿名子类实例**（编译器生成 `Operation$1` / `$2`）
- 调用 `PLUS.apply()` 是**虚方法分发**，编译期看父类，运行期分到子类
- 编译器强制每常量必须实现抽象方法（漏一个编译错）-- 编译期完整性检查

#### 10.3.3 ⚠️ 陷阱

| 陷阱 | 说明 |
|------|------|
| 常量后要有 `{ }` | `PLUS { @Override ... }`，不是 `PLUS, MINUS` |
| abstract 方法位置 | 必须在常量列表**之后** |
| 每常量必须实现 | 漏一个编译错 |

#### 10.3.4 浮点除零（非坑，是知识）

Java 浮点除零**不抛异常**（与整数除零不同）：
- `1.0 / 0.0` -> `Infinity`
- `-1.0 / 0.0` -> `-Infinity`
- `0.0 / 0.0` -> `NaN`
- `1 / 0`（整数）-> `ArithmeticException`

---

### 10.4 EnumSet / EnumMap -- 高性能枚举集合

#### 10.4.1 EnumSet（位向量实现）

**核心**：每个枚举值占一个 bit，1 表示有，0 表示无。≤64 个枚举值只需一个 `long`。

```
枚举: READ=0, WRITE=1, DELETE=2, EXEC=3
位向量: 0b1010 = 10 -> 有 WRITE 和 DELETE
```

**性能**：所有操作位运算，接近 O(1) 且常数极小，比 HashSet 快约 10 倍。

**工厂方法**（EnumSet 是抽象类，不能用 `new`）：

| 工厂 | 作用 |
|------|------|
| `EnumSet.noneOf(E.class)` | 空集合 |
| `EnumSet.allOf(E.class)` | 全集 |
| `EnumSet.of(E e1, E e2)` | 指定值 |
| `EnumSet.copyOf(EnumSet)` | 拷贝 |
| `EnumSet.complementOf(EnumSet)` | 补集 |

#### 10.4.2 EnumMap（定长数组实现）

**核心**：用 `Object[]` 存储 value，下标 = `ordinal()`。`get(KEY)` 等于 `array[KEY.ordinal()]`。

```java
EnumMap<TrafficLight, String> map = new EnumMap<>(TrafficLight.class);  // 必须传 Class
```

#### 10.4.3 三大对比

| 维度 | HashSet/HashMap | EnumSet | EnumMap |
|------|----------------|---------|---------|
| 适用 | 任意对象 | 仅枚举 | 仅枚举键 |
| 实现 | 哈希表 | 位向量 | 定长数组 |
| 查找 | O(1)（算 hash）| O(1)（位运算）| O(1)（下标）|
| 顺序 | 无 | 按 `ordinal` | 按 `ordinal` |

#### 10.4.4 关键特性（已测试验证）

- **`EnumSet.copyOf` 返回新 Set**，改新 Set 不影响原 Set
- **迭代顺序 = 声明顺序**（非插入顺序）-- `of(EXECUTE, READ)` 迭代仍是 `[READ, WRITE, DELETE, EXECUTE]`
- **顺序无关相等** -- `of(READ, WRITE) == of(WRITE, READ)`

---

### 10.5 枚举单例 + 反射攻击防御

#### 10.5.1 枚举单例写法

```java
enum AppConfig {
    INSTANCE;                          // 只一个常量 = 单例
    private final Map<String, String> settings = new HashMap<>();
    AppConfig() { settings.put("app.name", "java-pra"); }   // 隐式 private
    public String get(String key) { return settings.get(key); }
}
// 调用：AppConfig.INSTANCE.get("app.name")
```

#### 10.5.2 三道防线（JVM 层面）

| 防线 | 拦什么 | 怎么拦 |
|------|--------|--------|
| **构造器私有 + JVM 检查** | `newInstance()` 反射 | `Constructor.newInstance()` 对枚举直接抛 `IllegalArgumentException: Cannot reflectively create enum objects` |
| `clone()` final 抛异常 | `clone()` | `Enum.clone()` 抛 `CloneNotSupportedException` |
| `readObject` 特殊路径 | 反序列化 | JVM 检测枚举调 `valueOf(name)` 返回已存在实例，不创建新对象 |

**对照实验**：普通类（如 `ArrayList`）反射 `newInstance` 能创建多实例，反证枚举防御的特殊性。

#### 10.5.3 防线 2 的三层验证（关键收获）

clone 防御有三重保护：
1. `AppConfig.class.getDeclaredMethod("clone")` -> `NoSuchMethodException` -- AppConfig 没自己声明 clone（继承自 Enum）
2. `AppConfig.class.getMethod("clone")` -> `NoSuchMethodException` -- clone 是 protected，跨包不可见
3. `Enum.class.getDeclaredMethod("clone").setAccessible(true)` -> `InaccessibleObjectException` -- JDK 9+ 模块系统强封装 `java.base`

---

### 10.6 综合实战：策略模式（支付方式）

#### 10.6.1 策略模式三要素

1. **策略接口/抽象方法** -- 统一行为契约
2. **具体策略** -- 每个枚举常量是一个具体策略
3. **上下文** -- 持策略引用，调统一方法

#### 10.6.2 实现

```java
// 结果对象（record，JDK 16+ 不可变）
public record PayResult(boolean success, String message, double actualAmount) {}

// 支付方式枚举（综合运用 §10.2 字段 + §10.3 抽象方法）
public enum PaymentMethod {
    ALIPAY("支付宝", 0.01, 50000, "即时到账") {
        @Override public PayResult pay(double amount) {
            if (amount > getLimit()) return new PayResult(false, "超过限额", 0);
            return new PayResult(true, getDisplayName(), amount * (1 - getFeeRate()));
        }
    },
    ...;
    private final String displayName;
    private final double feeRate;
    private final double limit;
    private final String arrivalTime;
    PaymentMethod(...) { ... }
    public abstract PayResult pay(double amount);
}

// 上下文（持策略引用，可切换）
public static class PaymentContext {
    private PaymentMethod method;
    public PaymentContext(PaymentMethod method) { this.method = method; }
    public void setMethod(PaymentMethod method) { this.method = method; }
    public PayResult checkout(double amount) { return method.pay(amount); }
}
```

#### 10.6.3 综合运用

- §10.2 字段（`displayName`/`feeRate`/`limit`/`arrivalTime`）+ 构造器
- §10.3 抽象方法（每常量独立 `pay` 实现）
- §10.5 record（`PayResult` 不可变结果对象）
- §10.1 单例（每个枚举常量本身就是单例）
- 策略模式（上下文持策略引用，可切换）

---

## 二、本课踩的坑（真实）

### `valueOf` vs `parse` 容错
- `valueOf("BLUE")` 找不到抛 `IllegalArgumentException`（非受检）
- 想容错返回 null，要 try-catch 包裹

### 覆盖 `toString` 连锁反应
- `allNames()` 用 `toString()`/`getDisplayName()`，覆盖 `toString` 后返回值从英文变中文
- 旧测试断言失效（`["RED"]` -> `["红灯"]`）

### `assertThrows` 第二参数是 `Executable`（lambda）
- 不能传 `c.newInstance(...)` 的结果（立即执行，异常到不了 assertThrows）
- 必须 `() -> c.newInstance(...)` 延迟执行
- traps 笔记已记（ListLab 那条），本课再次踩到

### 枚举构造器参数是 `(String, int)`
- 编译器自动加 `name` + `ordinal`（呼应 §10.1 字节码）
- 反射拿构造器：`getDeclaredConstructor(String.class, int.class)`

### `getMethod("clone")` 跨包抛 `NoSuchMethodException`
- `getMethod` 只查 public，protected 方法跨包不可见
- 要拿 protected 方法需 `getDeclaredMethod` 从声明类拿

### JDK 9+ 模块系统拦 `Enum.clone()`
- `java.base` 模块 `opens java.lang` 给 unnamed module 时 `setAccessible` 才放行
- 否则抛 `InaccessibleObjectException`（不是 `IllegalAccessException`，呼应第八课坑）
- 即使模块放开，`Enum.clone()` 源码本身还 `throw CloneNotSupportedException`

### 方法内局部类反射拿构造器抛 `NoSuchMethodException`
- 局部类构造器编译器加合成参数（外部 this 等），无参 `getDeclaredConstructor` 找不到
- 改用 `ArrayList` 等标准类做对照实验

### 浮点比较要用 delta
- `assertEquals(expected, actual, 0.001)` 第三参数允许误差
- IEEE 754 浮点不精确（`0.01 * 100` 可能不等于 99.0）

---

## 三、待补强基础库

- `java.lang.Enum` 全套方法（`name` / `ordinal` / `compareTo` / `getDeclaringClass`）
- `EnumSet` / `EnumMap`（已学基础，缺：`complementOf` / `range` 等高级工厂）
- `java.util.concurrent.TimeUnit`（每常量实现抽象方法的经典案例）
- record（JDK 16+，本课用了但没深入 -- 待第十六课现代特性）

---

## 四、下一步

枚举课完整结束（56 测试全绿）。下一课候选：
- 第十一课：`java.time` 日期时间（工程必备）
- 第十二课：`Optional` 深入（Stream 配套）
- 第十三课：泛型进阶实战（PECS）

说"继续"按路线开始第十一课 `java.time`。
