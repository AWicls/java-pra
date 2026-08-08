# Java 学习笔记 - Day 9（2026-08-08）

> 学习项目：java-pra（JDK 25 + Gradle 9.6.1 + JUnit Jupiter 6.0.1）
> 进度：第十二课（Optional 深入）-- 全 5 节完成
> 代码：[app/src/main/java/learning/pra/optional/OptionalLab.java](app/src/main/java/learning/pra/optional/OptionalLab.java)
> 测试：[app/src/test/java/learning/pra/optional/OptionalLabTest.java](app/src/test/java/learning/pra/optional/OptionalLabTest.java)
> 总计：18 个测试全绿

---

## 一、第十二课：Optional 深入

### 12.1 Optional 为什么存在

#### 12.1.1 核心问题

- 方法返回 `User` 时，可能返回 null，但**签名看不出"可能没有"**
- 调用方靠"自觉"判空，忘判一次就 NPE（运行时才炸，编译期拦不住）
- `Optional` 把"可能没有"语义显式化，返回值契约的一部分

| 对比 | 返回 `User` | 返回 `Optional<User>` |
|------|-----------|---------------------|
| 是否可能为 null | 可能（但签名看不出来）| 语义显式 |
| 调用方是否强制处理 | 靠自觉，忘了就 NPE | 强制面对"有没有" |
| 编译器能否拦截 | ❌ | 部分拦截（`get()` 前 IDE 警告）|

#### 12.1.2 关键认识

- `Optional` 是**容器**，不是数据：要么"有值"（`of`），要么"空"（`empty`）
- 不直接操作里面的 `User`，而是通过方法消费它
- `Optional.of(null)` 抛 `NullPointerException`：`of` 只接受非 null（"我确定有值"）
- `Optional.ofNullable(null)` 不抛异常，变空（"可能没有"）

#### 12.1.3 三种创建方式

| 方法 | 传 null 时 | 语义 |
|------|-----------|------|
| `Optional.of(x)` | 抛 NPE | 我确定有值 |
| `Optional.ofNullable(x)` | 变空，不抛 | 可能没有 |
| `Optional.empty()` | - | 明确空 |

### 12.2 消费 Optional -- 取出值

#### 12.2.1 别用 `get()` 直接取

- `get()` 空时抛 `NoSuchElementException`（运行时炸），**不推荐**
- 用 4 个"给空值一个去处"的方法

| 方法 | 空的时候 | 返回值 | 适用 |
|------|---------|--------|------|
| `orElse(default)` | 返回 `default` | 立即算出 | default 计算便宜 |
| `orElseGet(supplier)` | 调用 lambda 取默认 | 延迟算出 | default 计算昂贵/有副作用 |
| `orElseThrow(ex)` | 抛自定义异常 | 不返回 | 空是程序错误 |
| `ifPresent(consumer)` | 什么都不做 | void | 有值才做某事 |

#### 12.2.2 `orElse` vs `orElseGet` 关键区别

```java
String a = empty.orElse(expensive());         // expensive() 立即执行（浪费）
String b = empty.orElseGet(() -> expensive()); // 空时才执行
String c = full.orElse(expensive());          // 有值了还执行 expensive()——白花钱！
```

- `orElse(默认值)` 参数是**已算好的值**，无论空不空都先算
- `orElseGet(Supplier)` 延迟，**只有空才调**
- 默认值计算昂贵或有副作用（查库、打日志）时用 `orElseGet`

### 12.3 链式变换 -- map / flatMap / filter

#### 12.3.1 三个方法

| 方法 | 作用 | lambda 返回 | 结果 |
|------|------|-----------|------|
| `map(fn)` | 值存在就变换 | `T -> R`（普通值）| `Optional<R>` |
| `flatMap(fn)` | 值存在就变换 | `T -> Optional<R>` | `Optional<R>`（不嵌套）|
| `filter(pred)` | 值存在且满足条件才保留 | `T -> boolean` | `Optional<T>` |

#### 12.3.2 map vs flatMap（核心难点）

```java
Optional<Integer> len = opt.map(s -> s.length());                    // 普通值 -> 一层
Optional<Optional<Integer>> bad = opt.map(s -> Optional.of(s.length())); // 两层！
Optional<Integer> good = opt.flatMap(s -> Optional.of(s.length()));  // 拆一层，一层
```

- `map`：lambda 返回普通值 `R`，包一层
- 若 lambda 本身返回 `Optional`，用 `map` 会双重嵌套；`flatMap` 摊平，始终一层
- 生活类比：柜里东西自己也是个盒子时，`flatMap` 把内层盒子也摊平

#### 12.3.3 filter

- lambda 返回 `boolean`，满足保留，否则变空
- **不改值类型**（还是 `Optional<String>`）

#### 12.3.4 链式示例

```java
Optional<String> result = Optional.of("hello")
        .map(String::toUpperCase)          // "HELLO"
        .filter(s -> s.contains("LL"))     // 保留
        .flatMap(s -> Optional.of(s + "!")); // "HELLO!"
```

- 与 Stream 互通：语义一致，Optional 操作 0 或 1 个元素，Stream 操作 0 到 n 个

### 12.4 反模式 -- Optional 不该用在哪

> 核心原则：`Optional` 只用作**方法返回值**（声明"可能没有"）。不是数据容器。

**四大反模式**：

| # | 反模式 | 规范改法 |
|---|------|---------|
| 1 | 当字段 `Optional<Address> address` | 直接 `Address address`，null 表达"没有" |
| 2 | 包装集合 `Optional<List<String>>` | 空集合表达"没有" |
| 3 | 当方法参数 `Optional<String> name` | 直接传（null 或重载）|
| 4 | 基本类型 `Optional<Integer>` | 用 `OptionalInt`/`OptionalLong`/`OptionalDouble`（避免装箱）|

**一句话记忆**：`Optional` 是返回值的语义声明，不是数据容器。只在方法返回值上用；字段、参数、集合、基本类型都用不上它。

### 12.5 实战 -- 嵌套对象安全取值

#### 12.5.1 场景

`User -> Address -> city` 三层嵌套，任意一层可能为空。

#### 12.5.2 老写法 vs Optional 链

```java
// 深 V 判空（能跑但丑）
if (user != null) {
    if (user.getAddress() != null) {
        city = user.getAddress().getCity();
    }
}

// Optional 链（全程不判空）
Optional<String> city = Optional.ofNullable(user)   // 兜住 user 为 null
        .map(User::getAddress)                       // 中间返回 null 自动变空
        .map(Address::getCity)
        .orElse("unknown");                          // 兜底值
```

- 深 V 判空要写 `2^n` 层嵌套；`Optional` 链是 `n` 行平铺
- 层数越多，`Optional` 优势越大
- 若中间 getter 返回 `Optional`，用 `flatMap`（避免嵌套）

---

## 二、本课踩坑速记（5 条）

1. **误用 Guava 的 `Optional`** -- `import com.google.common.base.Optional`（Guava）vs `java.util.Optional`（JDK）是完全不同的类。项目有 Guava 依赖所以能编译过，但本课及大多数业务代码用 JDK 版。写 `Optional` 优先敲 `java.util.`
2. **方法名拼写 `orElseDefaul` 缺 t** -- 少写末尾字符导致"找不到符号"，测试按契约名找方法。命名要精确（呼应第十一课 `addBusinessDays`）
3. **`Optional.of(null)` 抛 NPE** -- `of` 只接受非 null（语义"我确定有值"），不确定就 `ofNullable`
4. **`Optional` 不是数据容器** -- 别当字段/参数/集合/基本类型用，只用于方法返回值
5. **非静态内部类 `new OptionalLab().new Address(...)`** -- `Address`/`User` 不依赖外部实例，应声明为 `static` 内部类，构造才清爽（`new OptionalLab.User(...)`）

---

## 三、OptionalLab 完整方法清单

| 方法 | 知识点 | 测试覆盖 |
|------|------|------|
| `isPresent(opt)` | 三种创建方式 + `isPresent` | ✅ |
| `orElseDefault(opt)` | `orElse` 空值兜底 | ✅ |
| `orElseThrow(opt)` | `orElseThrow` 空抛异常 | ✅ |
| `mapLength(opt)` | `map` + `orElse` 链式 | ✅ |
| `flatMapWrap(opt)` | `flatMap` 避免嵌套 | ✅ |
| `filterLong(opt)` | `filter` 条件保留 | ✅ |
| `safeGetCity(user)` | 嵌套安全取值 + 防 NPE | ✅ |

---

## 四、待补强基础库

- `Optional` 全套方法 ✅ 本课已补（of/ofNullable/empty/orElse/orElseGet/orElseThrow/ifPresent/map/flatMap/filter）
- `Collectors` 全套 / `Collections` 工具类 / `Arrays` 工具类
- `String` 全套方法 / `Function`-`Predicate`-`Consumer`-`Supplier` 全套

---

## 五、下一步

- 第十三课：泛型进阶实战（PECS 实战补强）
- 或第十四课：Stream 进阶（`groupingBy` / `flatMap` / 自定义收集器）
- 或第十六课：现代特性（Records / Sealed / Pattern Matching）
