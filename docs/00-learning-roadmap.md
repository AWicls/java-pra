# Java 学习总览 + 学习路线（2026-08-06 整理）

> 项目：java-pra（JDK 25 + Gradle 9.6.1 + JUnit Jupiter 6.0.1）
> 起始：2026-07-31 ｜ 已完成：7 天 / 10 课 / 8 个 Lab / 156+ 测试


---

## 一、已完成课程清单（9 课）

### Day 1（2026-07-31）｜四课速通

| 课 | 主题 | 代码 | 测试数 | 关键掌握 |
|---|------|------|--------|---------|
| 1 | 集合框架 | [ListLab.java](app/src/main/java/learning/pra/collections/ListLab.java) | - | List/Set/Map 三族选型、不可变集合、迭代器 fail-fast、频率统计惯用法 |
| 2 | 泛型基础 | [GenericsLab.java](app/src/main/java/learning/pra/generics/GenericsLab.java) | - | 类型参数命名、泛型方法/类/接口、上界 `extends`、PECS 入门、类型擦除 |
| 3 | Stream | [StreamLab.java](app/src/main/java/learning/pra/stream/StreamLab.java) | - | 中间操作 / 终结操作、收集器 `Collectors`、`reduce`、`Optional` |
| 4 | 现代特性入门 | [ModernLab.java](app/src/main/java/learning/pra/modern/ModernLab.java) | - | lambda / 方法引用 / 函数式接口 |

### Day 2（2026-08-01）｜并发基础

| 课 | 主题 | 代码 | 测试数 | 关键掌握 |
|---|------|------|--------|---------|
| 5 | 并发与多线程 | [ConcurrencyLab.java](app/src/main/java/learning/pra/concurrent/ConcurrencyLab.java) | 7 | 进程 vs 线程、`Runnable` vs `Thread`、守护线程、`synchronized`、`volatile`、`wait/notify` |

### Day 3（2026-08-02）｜异常

| 课 | 主题 | 代码 | 测试数 | 关键掌握 |
|---|------|------|--------|---------|
| 6 | 异常体系 | [ExceptionLab.java](app/src/main/java/learning/pra/exceptions/ExceptionLab.java) + [ConfigException.java](app/src/main/java/learning/pra/exceptions/ConfigException.java) | 11 | 家族树、受检 vs 非受检、try-with-resources、自定义异常、finally 陷阱 |

### Day 4（2026-08-03）｜IO

| 课 | 主题 | 代码 | 测试数 | 关键掌握 |
|---|------|------|--------|---------|
| 7 | IO 流体系 | [IoLab.java](app/src/main/java/learning/pra/io/IoLab.java) | 15 | 字节/字符流、装饰器模式、缓冲流、`PrintWriter`、try-with-resources |

### Day 5（2026-08-04）｜反射

| 课 | 主题 | 代码 | 测试数 | 关键掌握 |
|---|------|------|--------|---------|
| 8 | 反射 | [ReflectionLab.java](app/src/main/java/learning/pra/reflection/ReflectionLab.java) | 24 | `Class<?>` 三种获取方式、Field/Method/Constructor 三剑客、`setAccessible` 破防、`InvocationTargetException` 解包 |

### Day 6（2026-08-05）｜注解

| 课 | 主题 | 代码 | 测试数 | 关键掌握 |
|---|------|------|--------|---------|
| 9 | 注解 | [AnnotationsLab.java](app/src/main/java/learning/pra/annotations/AnnotationsLab.java) + [ValidatorLab.java](app/src/main/java/learning/pra/annotations/ValidatorLab.java) | 20 | 四个内置注解、`@Target`/`@Retention` 元注解、自定义注解、注解 + 反射实战（表单校验器） |

### Day 7（2026-08-06）｜枚举

| 课 | 主题 | 代码 | 测试数 | 关键掌握 |
|---|------|------|--------|---------|
| 10 | 枚举 | [EnumLab.java](app/src/main/java/learning/pra/enums/EnumLab.java) | 56 | 枚举本质（语法糖/`java.lang.Enum`）、字段/构造器/方法、抽象方法（每常量独立实现/策略模式）、`EnumSet`/`EnumMap`、枚举单例+反射防御、策略模式实战（支付方式） |

---

## 二、知识掌握度自评（重要：识别薄弱点）

| 主题 | 掌握度 | 备注 |
|------|-------|------|
| 集合 | ⭐⭐⭐⭐ | 基础够用，缺：`Collections.unmodifiableXxx` vs `List.of` 区别、`Map.computeIfAbsent` |
| 泛型基础 | ⭐⭐⭐ | 已学 PECS 概念，但**未实战过** -- 待补强 |
| Stream | ⭐⭐⭐ | 缺：`groupingBy` / `partitioningBy` / `flatMap` 实战 |
| 并发基础 | ⭐⭐⭐ | 缺：`ReentrantLock` / `Condition` / `CompletableFuture` / `ExecutorService` |
| 异常 | ⭐⭐⭐⭐ | 已够用 |
| IO 流 | ⭐⭐⭐ | 缺：NIO（`Path`/`Files`/`Channel`）-- 现代项目主流 |
| 反射 | ⭐⭐⭐⭐ | 已够用 |
| 注解 | ⭐⭐⭐⭐ | 已够用 |
| **枚举** | ⭐⭐⭐⭐ | 已学：本质/字段/抽象方法/EnumSet/EnumMap/单例/策略模式；缺：`EnumSet.range` / `EnumMap` 高级用法 |
| 现代特性 | ⭐⭐ | 仅入门 lambda + 接触 record，缺：Records 深入 / Sealed Classes / Pattern Matching / Switch Pattern |
| **未学** | `java.time` 日期时间 | 工程必备 |
| **未学** | `Optional` 深入 | Stream 配套 |
| **未学** | 设计模式 | 用反射+注解+泛型实现，巩固所学 |


---

## 三、调整后的新学习路线（10 课起）

### 设计原则

1. **优先补"工程必备"**（枚举 / `java.time` / `Optional`）-- 这些是日常写代码就用的
2. **进阶补强已学**（泛型 PECS 实战、Stream 收集器、NIO）
3. **现代特性单独成课**（JDK 25 Records / Sealed / Pattern Matching）
4. **综合实战收尾**（用反射+注解+泛型+设计模式做一个小框架）

### 第十课：枚举（enum）✅ 已完成（2026-08-06，56 测试）

- ✅ 枚举本质（继承自 `java.lang.Enum` 的语法糖）
- ✅ 枚举的字段 / 构造器 / 方法
- ✅ 枚举实现接口、枚举的抽象方法（每常量独立实现）
- ✅ `EnumSet` / `EnumMap`（高性能枚举集合）
- ✅ 枚举单例模式（防反射攻击，呼应第八课）
- ✅ **实战**：用枚举实现策略模式（支付方式）

### 第十一课：`java.time` 日期时间

- 旧 API（`Date`/`Calendar`）的坑：可变、月份从 0、线程不安全
- 新 API 三剑客：`LocalDate` / `LocalTime` / `LocalDateTime`
- 时区：`ZonedDateTime` / `Instant`（时间戳）
- 时长与周期：`Duration`（精确秒）vs `Period`（年月日）
- 格式化：`DateTimeFormatter`（线程安全，替代 `SimpleDateFormat`）
- **实战**：账单计算器（输入开始日期+周期，输出每期还款日）

### 第十二课：`Optional` 深入

- `Optional` 的意义（声明返回值可能不存在，强制调用方处理）
- 创建：`Optional.of` / `ofNullable` / `empty`
- 消费：`isPresent` / `ifPresent` / `orElse` / `orElseGet` / `orElseThrow`
- 链式：`map` / `flatMap` / `filter`（与 Stream 互通）
- 反模式：别把 `Optional` 当字段 / 参数（只用作返回值）
- **实战**：嵌套对象安全取值（`user.getAddress().getCity()` 防 NPE）

### 第十三课：泛型进阶实战（补强）

- PECS 实战：写一个 `Collections.copy(src, dst)` 工具
- 类型擦除深入：运行期拿不到 `T.class`，如何 workaround
- 泛型数组：`T[]` 不能直接 `new`，要用 `Array.newInstance`
- 泛型方法边界：`<T extends Comparable<T>>` 实现通用 `max`
- **实战**：实现一个泛型 `Box<T>` + `Repository<T>` DAO 模式

### 第十四课：Stream 进阶

- `groupingBy` / `partitioningBy`（分组分区）
- `flatMap`（拍平嵌套）
- `collectingAndThen` / `mapping` / `joining`
- 并行流 `parallelStream` 的陷阱
- `Collector` 自定义收集器
- **实战**：销售数据分析（按区域分组、TopN、统计）

### 第十五课：NIO 与现代 IO

- 旧 IO（流）vs 新 IO（块 / Buffer）
- `Path` / `Files`（替代 `File`，更现代）
- `Buffer` / `Channel` / `Selector`
- `Files.readAllLines` / `Files.writeString`（一行搞定）
- **实战**：用 NIO 重写 IoLab（对比流式与块式）

### 第十六课：现代特性速览（JDK 16-25）✅ 已完成（2026-08-13，概念点1-5 过完，实战用户自练）

- Records（不可变数据载体，替代 POJO）
- Sealed Classes（密封类，限定继承层级）
- Pattern Matching for instanceof（去掉强转）
- Switch Pattern（JDK 21+，模式匹配 switch）
- Text Blocks（`"""..."""` 多行字符串）
- **实战**：用 Records + Sealed + Switch Pattern 重构表达式树

### 第十七课：并发进阶

- `ReentrantLock` vs `synchronized`（可中断、可超时、公平锁）
- `Condition`（替代 `wait/notify`）
- `CompletableFuture`（异步编程，链式组合）
- `ExecutorService` / `ThreadPoolExecutor`（线程池）
- Virtual Threads（JDK 21+，轻量级线程）
- **实战**：用 CompletableFuture 实现并发聚合接口

### 第十八课：综合实战 -- 小框架

把已学的**反射 + 注解 + 泛型 + 设计模式**串起来做一个微型框架：
- 自定义 `@Inject` / `@Singleton` / `@Component` 注解
- 用反射扫描类、读注解、管理 Bean 生命周期
- 简化版 IoC 容器（Spring 核心原理）
- **目标**：理解 Spring 为啥能"new 出对象、注入依赖"

---

## 四、学习节奏建议

### 当前节奏问题

- 每课代码量偏多（10+ 测试），节奏快但深度消化不足
- 跨课程知识点串联少（如反射+注解+泛型一起用）

### 新节奏建议

| 阶段 | 课数 | 节奏 |
|------|------|------|
| 工程补强 | 10-12（枚举/time/Optional）| 快速过，每课 1-2 小时，重点 API 熟悉 |
| 进阶补强 | 13-15（泛型/Stream/NIO）| 中速，每课 3-5 个测试，重视实战 |
| 现代特性 | 16（Records/Sealed/Pattern）| 中速，每特性独立小练习 |
| 并发进阶 | 17 | 慢速，并发本来就难，多测试多断言 |
| 综合实战 | 18 | 慢速，把之前所有知识串起来 |

### 每课固定流程

1. **概念讲解**（具体先行：生活例子 + 对比表 + 最小代码）
2. **独立实现**（你写代码 + 测试）
3. **校验**（我跑测试 + 反馈）
4. **坑点整理**（真实遇到的，记进笔记）
5. **Javadoc 补全**（学完即加，养成习惯）

---

## 五、学习目标

学完这 18 课后，你应能：
- 看懂 Spring Boot 项目的核心机制（IoC / AOP / Validation）
- 独立写一个中等复杂度的 Java 项目（含并发、IO、注解驱动）
- 阅读开源库源码（如看 JUnit 怎么扫描 `@Test`、Hibernate Validator 怎么校验）
- 顺利过渡到 Spring Boot 学习阶段

---

## 六、下一步

说"继续"按新路线开始 **第十课：枚举**。
或说"跳到 X 课"切换主题（如已经熟悉 enum 想直接学 NIO）。
