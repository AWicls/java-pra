# Java 学习总览 + 学习路线（2026-08-15：18 课全部完成 ✅）

> 项目：java-pra（JDK 25 + Gradle 9.6.1 + JUnit Jupiter 6.0.1）
> 周期：2026-07-31 → 2026-08-15 ｜ 16 天 / 18 课 / 14 个主题包 / **300 测试全绿**
> 读码速查：[java-reading-guide.md](java-reading-guide.md) ｜ 每日笔记：`docs/dayN-learning-notes.md`

---

## 一、旅程总览（16 天）

| 时间 | 课 | 主题 | 代表成果 |
|------|----|------|---------|
| Day1（07-31）| 1-4 | 集合 / 泛型基础 / Stream / 现代特性入门 | ListLab / GenericsLab / StreamLab / ModernLab |
| Day2（08-01）| 5 | 并发与多线程 | ConcurrencyLab |
| Day3（08-02）| 6 | 异常体系 | ExceptionLab + ConfigException |
| Day4（08-03）| 7 | IO 流体系 | IoLab |
| Day5（08-04）| 8 | 反射 | ReflectionLab |
| Day6（08-05）| 9 | 注解 + 反射实战 | AnnotationsLab + ValidatorLab（表单校验器）|
| Day7（08-06）| 10 | 枚举 | EnumLab（策略模式/单例/EnumSet/EnumMap）|
| Day8（08-07）| 11 | java.time | DateLab（账单计算器）|
| Day9（08-08）| 12 | Optional | OptionalLab（防 NPE 链式取值）|
| Day10（08-09）| 13 | 泛型进阶实战 | GenericPecsLab（PECS/泛型数组/泛型 DAO）|
| Day11（08-10）| 14 | Stream 进阶 | StreamAdvLab（groupingBy/自定义 Collector）|
| Day12（08-11）| 15 | NIO 与现代 IO | NioLab（Path/Files/ByteBuffer）|
| Day13（08-12）| 16 | 现代特性（Records/Sealed/Pattern）| VehicleLab + ModernLab |
| Day14-15（08-13~14）| 17 | 并发进阶 | ReentrantLock/Condition/CF/线程池/虚拟线程 4 Lab |
| Day16（08-15）| 18 | **综合实战：迷你 IoC 框架** | 注解三件套 + MiniContainer + 循环依赖检测 |

---

## 二、已完成课程清单（18 课 ✅）

### 基础篇（Day 1-4：集合 / 泛型 / Stream / 现代特性）

| 课 | 主题 | 代码 | 测试 | 关键掌握 |
|:---:|------|------|:---:|---------|
| 1 | 集合框架 | [ListLab.java](app/src/main/java/learning/pra/collections/ListLab.java) | 11 | List/Set/Map 选型、不可变集合、fail-fast、频次统计 |
| 2 | 泛型基础 | [GenericsLab.java](app/src/main/java/learning/pra/generics/GenericsLab.java) | 13 | 类型参数、泛型方法/上界、PECS 入门、类型擦除 |
| 3 | Stream | [StreamLab.java](app/src/main/java/learning/pra/stream/StreamLab.java) | 11 | 中间/终结操作、Collectors 全套、reduce、groupingBy/partitioningBy/flatMap、自定义 Collector、并行流 |
| 4 | 现代特性入门 | [ModernLab.java](app/src/main/java/learning/pra/modern/ModernLab.java) | — | lambda / 方法引用 / 函数式接口（测试并入第 16 课）|

### 工程基础篇（Day 2-7：并发 / 异常 / IO / 反射 / 注解 / 枚举）

| 课 | 主题 | 代码 | 测试 | 关键掌握 |
|:---:|------|------|:---:|---------|
| 5 | 并发与多线程 | [ConcurrencyLab.java](app/src/main/java/learning/pra/concurrent/ConcurrencyLab.java) | 15 | 线程、synchronized、volatile、Atomic、wait/notify、生产者消费者 |
| 6 | 异常体系 | [ExceptionLab.java](app/src/main/java/learning/pra/exceptions/ExceptionLab.java) | 13 | 家族树、受检/非受检、try-with-resources、finally 陷阱、异常链 |
| 7 | IO 流体系 | [IoLab.java](app/src/main/java/learning/pra/io/IoLab.java) | 15 | 字节/字符流、装饰器、缓冲流、try-with-resources |
| 8 | 反射 | [ReflectionLab.java](app/src/main/java/learning/pra/reflection/ReflectionLab.java) | 24 | Class 获取、Field/Method/Constructor、setAccessible、异常解包 |
| 9 | 注解 | [AnnotationsLab.java](app/src/main/java/learning/pra/annotations/AnnotationsLab.java) + [ValidatorLab.java](app/src/main/java/learning/pra/annotations/ValidatorLab.java) | 20 | 元注解、自定义注解、注解 + 反射实战（校验器）|
| 10 | 枚举 | [EnumLab.java](app/src/main/java/learning/pra/enums/EnumLab.java) | 56 | 本质、字段/构造器、抽象方法、EnumSet/EnumMap、单例三防线、策略模式 |

### 进阶篇（Day 8-13：时间 / Optional / 泛型实战 / Stream 进阶 / NIO / 现代特性）

| 课 | 主题 | 代码 | 测试 | 关键掌握 |
|:---:|------|------|:---:|---------|
| 11 | java.time | [DateLab.java](app/src/main/java/learning/pra/time/DateLab.java) | 13 | LocalDate/Time/DateTime、时区、Duration/Period、DateTimeFormatter |
| 12 | Optional | [OptionalLab.java](app/src/main/java/learning/pra/optional/OptionalLab.java) | 18 | 创建/消费/链式 map·flatMap·filter、反模式 |
| 13 | 泛型进阶 | [GenericPecsLab.java](app/src/main/java/learning/pra/generics/GenericPecsLab.java) | 17 | PECS、泛型数组 workaround、泛型 DAO |
| 14 | Stream 进阶 | [StreamAdvLab.java](app/src/main/java/learning/pra/stream/StreamAdvLab.java) | 23 | groupingBy/partitioningBy/flatMap、自定义 Collector、并行流 |
| 15 | NIO | [NioLab.java](app/src/main/java/learning/pra/nio/NioLab.java) | 8 | Path/Files、ByteBuffer flip、Channel |
| 16 | 现代特性速览 | [ModernLab.java](app/src/main/java/learning/pra/modern/ModernLab.java) + [VehicleLab.java](app/src/main/java/learning/pra/modern/VehicleLab.java) | 15 | Record/Sealed/Pattern Matching/Switch Pattern/Text Blocks |

### 进阶与收尾（Day 14-16：并发进阶 / IoC 实战）

| 课 | 主题 | 代码 | 测试 | 关键掌握 |
|:---:|------|------|:---:|---------|
| 17 | 并发进阶 | [ReentrantLockLab](app/src/main/java/learning/pra/concurrent/ReentrantLockLab.java) / [ConditionLab](app/src/main/java/learning/pra/concurrent/ConditionLab.java) / [AsyncAggregateLab](app/src/main/java/learning/pra/concurrent/AsyncAggregateLab.java) / [ExecutorPoolLab](app/src/main/java/learning/pra/concurrent/ExecutorPoolLab.java) / [VirtualThreadLab](app/src/main/java/learning/pra/concurrent/VirtualThreadLab.java) | 13 | ReentrantLock、Condition、CompletableFuture、线程池、虚拟线程 |
| 18 | **综合实战·迷你 IoC** | [MiniContainer.java](app/src/main/java/learning/pra/ioc/MiniContainer.java) + 注解三件套 + [OrderService](app/src/main/java/learning/pra/ioc/OrderService.java) | 14 | IoC/DI、懒加载、依赖注入、循环依赖检测、对照 Spring |

> 测试数合计 **300**（含 App 冒烟测试 1 个）。

---

## 三、知识体系地图（14 个主题包）

| 主题 | 关键内容 | 测试数 |
|------|---------|:---:|
| 集合 | List/Set/Map 选型、不可变集合、fail-fast、频次统计 | 11 |
| 泛型 | 类型参数、泛型方法/上界、PECS、类型擦除、泛型数组、泛型 DAO | 30 |
| Stream | 中间/终结操作、Collectors 全套、reduce、groupingBy/partitioningBy/flatMap、自定义 Collector、并行流 | 34 |
| 现代特性 | lambda/方法引用、Record、Sealed、Pattern Matching、Switch Pattern、Text Blocks | 15 |
| 并发基础 | 线程、synchronized、volatile、Atomic、wait/notify、生产者消费者 | 15 |
| 异常 | 家族树、受检/非受检、try-with-resources、finally 陷阱、异常链 | 13 |
| IO | 字节/字符流、装饰器、缓冲流、try-with-resources | 15 |
| 反射 | Class 获取、Field/Method/Constructor、setAccessible、异常解包 | 24 |
| 注解 | 元注解、自定义注解、注解 + 反射实战（校验器）| 20 |
| 枚举 | 本质、字段/构造器、抽象方法、EnumSet/EnumMap、单例三防线、策略模式 | 56 |
| java.time | LocalDate/Time/DateTime、时区、Duration/Period、DateTimeFormatter | 13 |
| Optional | 创建/消费/链式 map·flatMap·filter、反模式 | 18 |
| NIO | Path/Files、ByteBuffer flip、Channel | 8 |
| 并发进阶 | ReentrantLock、Condition、CompletableFuture、线程池、虚拟线程 | 13 |
| IoC 框架 | 注解三件套、懒加载、依赖注入、循环依赖检测、对照 Spring | 14 |

---

## 四、最终知识掌握度

| 主题 | 掌握度 | 状态 |
|------|:---:|------|
| 集合 / 异常 / IO / NIO / 反射 / 注解 / 枚举 / Stream / 泛型 / 时间 / Optional / 现代特性 | ⭐⭐⭐⭐ | 全部实战完成 |
| 并发（基础 + 进阶） | ⭐⭐⭐⭐ | 锁/条件/CF/线程池/虚拟线程全覆盖 |
| 设计模式 | ⭐⭐⭐ | 会用（单例/工厂/策略）不熟命名 —— 待补强 |
| JVM / 内存模型 | ⭐⭐ | 大空缺 —— 待深入 |

---

## 五、现在能做什么（能力盘点）

1. **看懂 Spring Boot 核心机制**：IoC 容器原理、`@Component`/`@Inject`/`@Autowired`、反射 + 注解驱动的设计（Day16 亲手复刻简化版）
2. **独立写中等复杂 Java 项目**：集合/Stream/异常/IO/并发/时间/Optional 全套工程 API 都实操过
3. **读开源库源码**：JUnit 怎么扫 `@Test`、Hibernate Validator 怎么校验——"注解 + 反射"心智模型已建立
4. **调试与测试习惯**：300 个测试、假通过防坑、断言边界、`gradle test` 验证闭环
5. **踩坑方法论**：16 天沉淀 200+ 条真实坑，形成"先概念 → 再实战 → 踩坑 → 沉淀"的学习循环

---

## 六、学习沉淀（方法 + 习惯）

- **教学法**：概念具体先行（生活例子 + 对比表 + 最小代码）→ 独立实现 → 测试校验 → 坑点整理 → Javadoc 补全
- **测试驱动**：每课写覆盖测试，校验时防"绿灯假象"（断言非 null、看 XML 明细）
- **现代特性优先**：JDK 25 环境始终展示 Records/Sealed/Pattern/Virtual Threads 现代写法
- **笔记按天拆分**：day1~day16 详尽笔记 + 每日自评，便于回看
- **读码工具**：词汇 + Javadoc + 报错速查见 [java-reading-guide.md](java-reading-guide.md)

---

## 七、学习目标达成情况

| 目标（第十八课规划） | 达成 |
|---------------------|:---:|
| 看懂 Spring Boot 核心机制（IoC / AOP / Validation）| ✅ 亲手复刻简化 IoC 容器 |
| 独立写中等复杂度 Java 项目（含并发、IO、注解驱动）| ✅ 300 测试验证 |
| 阅读开源库源码（JUnit 扫 @Test、Hibernate Validator 校验）| ✅ 已具备"注解 + 反射"心智模型 |
| 顺利过渡到 Spring Boot 学习阶段 | 见"未来方向" |

---

## 八、踩坑沉淀入口

- 仓库记忆：`/memories/repo/java-pra-traps.md` —— 200+ 条真实踩坑（含教学方式校准）
- 每日笔记：`docs/dayN-learning-notes.md` —— 概念 + 代码 + 坑 + 自评

---

## 九、未来学习方向（A-G 自选）

| 方向 | 内容 | 推荐序 |
|:---:|------|:---:|
| **A. Spring Boot 过渡** | IoC/AOP/事务/MVC/MyBatis，做 REST 接口 | ⭐ 主线 |
| **B. 补薄弱点** | 泛型通配符、并行流陷阱、虚拟线程 pin、设计模式命名 | ⭐ 穿插 |
| **C. 读开源源码** | JUnit / Spring / Hibernate 源码 | 中高级 |
| **D. JVM 深入** | 内存模型 / GC / 类加载 | 面试加分 |
| **E. 并发深入** | AQS / ThreadLocal / 线程池调优 | 面试高频 |
| **F. 工程化** | Maven / Git CI / Mockito / 设计模式 | 提升效率 |
| **G. 综合实战** | 完整项目（记账 App / 博客系统）| 最扎实 |

> 我的建议顺序：**A（主线）→ B（穿插补漏）→ 按兴趣选 D/E/C/F/G**。想直接开始哪个，说方向字母即可。

---

## 十、下一步

18 课路线已全部完成 ✅。说方向字母（A-G）即开始对应阶段学习。
