# Java 学习总览 + 学习路线（2026-08-15 更新：18 课全部完成 ✅）

> 项目：java-pra（JDK 25 + Gradle 9.6.1 + JUnit Jupiter 6.0.1）
> 周期：2026-07-31 → 2026-08-15 ｜ 16 天 / 18 课 / 14 个主题包 / **300 测试全绿**
> 详细总结：[learning-summary.md](learning-summary.md) ｜ 每日笔记：`docs/dayN-learning-notes.md`

---

## 一、已完成课程清单（18 课 ✅）

### 基础篇（Day 1-4：集合 / 泛型 / Stream / 现代特性）

| 课 | 主题 | 代码 | 测试 | 关键掌握 |
|:---:|------|------|:---:|---------|
| 1 | 集合框架 | [ListLab.java](app/src/main/java/learning/pra/collections/ListLab.java) | 11 | List/Set/Map 选型、不可变集合、fail-fast、频次统计 |
| 2 | 泛型基础 | [GenericsLab.java](app/src/main/java/learning/pra/generics/GenericsLab.java) | 13 | 类型参数、泛型方法/上界、PECS 入门、类型擦除 |
| 3 | Stream | [StreamLab.java](app/src/main/java/learning/pra/stream/StreamLab.java) | 11 | 中间/终结操作、Collectors、reduce、Optional |
| 4 | 现代特性入门 | [ModernLab.java](app/src/main/java/learning/pra/modern/ModernLab.java) | — | lambda / 方法引用 / 函数式接口（测试并入第 16 课）|

### 工程基础篇（Day 2-7：并发 / 异常 / IO / 反射 / 注解 / 枚举）

| 课 | 主题 | 代码 | 测试 | 关键掌握 |
|:---:|------|------|:---:|---------|
| 5 | 并发与多线程 | [ConcurrencyLab.java](app/src/main/java/learning/pra/concurrent/ConcurrencyLab.java) | 15 | 线程、synchronized、volatile、Atomic、wait/notify、生产者消费者 |
| 6 | 异常体系 | [ExceptionLab.java](app/src/main/java/learning/pra/exceptions/ExceptionLab.java) | 13 | 家族树、受检/非受检、try-with-resources、finally 陷阱、异常链 |
| 7 | IO 流体系 | [IoLab.java](app/src/main/java/learning/pra/io/IoLab.java) | 15 | 字节/字符流、装饰器、缓冲流、try-with-resources |
| 8 | 反射 | [ReflectionLab.java](app/src/main/java/learning/pra/reflection/ReflectionLab.java) | 24 | Class 三获取、Field/Method/Constructor、setAccessible、异常解包 |
| 9 | 注解 | [AnnotationsLab.java](app/src/main/java/learning/pra/annotations/AnnotationsLab.java) + [ValidatorLab.java](app/src/main/java/learning/pra/annotations/ValidatorLab.java) | 20 | 元注解、自定义注解、注解 + 反射实战（表单校验器）|
| 10 | 枚举 | [EnumLab.java](app/src/main/java/learning/pra/enums/EnumLab.java) | 56 | 本质、字段/构造器、抽象方法、EnumSet/EnumMap、单例三防线、策略模式 |

### 进阶篇（Day 8-13：时间 / Optional / 泛型实战 / Stream 进阶 / NIO / 现代特性）

| 课 | 主题 | 代码 | 测试 | 关键掌握 |
|:---:|------|------|:---:|---------|
| 11 | java.time | [DateLab.java](app/src/main/java/learning/pra/time/DateLab.java) | 13 | LocalDate/Time/DateTime、时区、Duration/Period、DateTimeFormatter |
| 12 | Optional | [OptionalLab.java](app/src/main/java/learning/pra/optional/OptionalLab.java) | 18 | 创建/消费/链式 map·flatMap·filter、防 NPE、反模式 |
| 13 | 泛型进阶 | [GenericPecsLab.java](app/src/main/java/learning/pra/generics/GenericPecsLab.java) | 17 | PECS、泛型数组 workaround、泛型 DAO |
| 14 | Stream 进阶 | [StreamAdvLab.java](app/src/main/java/learning/pra/stream/StreamAdvLab.java) | 23 | groupingBy/partitioningBy/flatMap、自定义 Collector、并行流 |
| 15 | NIO | [NioLab.java](app/src/main/java/learning/pra/nio/NioLab.java) | 8 | Path/Files/ByteBuffer flip |
| 16 | 现代特性速览 | [ModernLab.java](app/src/main/java/learning/pra/modern/ModernLab.java) + [VehicleLab.java](app/src/main/java/learning/pra/modern/VehicleLab.java) | 15 | Record/Sealed/Pattern Matching/Switch Pattern/Text Blocks |

### 进阶与收尾（Day 14-16：并发进阶 / IoC 实战）

| 课 | 主题 | 代码 | 测试 | 关键掌握 |
|:---:|------|------|:---:|---------|
| 17 | 并发进阶 | [ReentrantLockLab](app/src/main/java/learning/pra/concurrent/ReentrantLockLab.java) / [ConditionLab](app/src/main/java/learning/pra/concurrent/ConditionLab.java) / [AsyncAggregateLab](app/src/main/java/learning/pra/concurrent/AsyncAggregateLab.java) / [ExecutorPoolLab](app/src/main/java/learning/pra/concurrent/ExecutorPoolLab.java) / [VirtualThreadLab](app/src/main/java/learning/pra/concurrent/VirtualThreadLab.java) | 13 | ReentrantLock、Condition、CompletableFuture、线程池、虚拟线程 |
| 18 | **综合实战·迷你 IoC** | [MiniContainer.java](app/src/main/java/learning/pra/ioc/MiniContainer.java) + 注解三件套 + [OrderService](app/src/main/java/learning/pra/ioc/OrderService.java) | 14 | IoC/DI、懒加载、依赖注入、循环依赖检测、对照 Spring |

> 测试数合计 **300**（含 App 冒烟测试 1 个）。

---

## 二、最终知识掌握度（2026-08-15 结课）

| 主题 | 掌握度 | 状态 |
|------|:---:|------|
| 集合 / 异常 / IO / NIO / 反射 / 注解 / 枚举 / Stream / 泛型 / 时间 / Optional / 现代特性 | ⭐⭐⭐⭐ | 全部实战完成 |
| 并发（基础 + 进阶） | ⭐⭐⭐⭐ | 锁/条件/CF/线程池/虚拟线程全覆盖 |
| 设计模式 | ⭐⭐⭐ | 会用（单例/工厂/策略）不熟命名 —— 待补强 |
| JVM / 内存模型 | ⭐⭐ | 大空缺 —— 待深入 |

> 详细矩阵见 [learning-summary.md](learning-summary.md#三掌握度矩阵综合各日自评)

---

## 三、学习目标达成情况

| 目标（第十八课规划） | 达成 |
|---------------------|:---:|
| 看懂 Spring Boot 核心机制（IoC / AOP / Validation）| ✅ 亲手复刻简化 IoC 容器 |
| 独立写中等复杂度 Java 项目（含并发、IO、注解驱动）| ✅ 300 测试验证 |
| 阅读开源库源码（JUnit 扫 @Test、Hibernate Validator 校验）| ✅ 已具备"注解 + 反射"心智模型 |
| 顺利过渡到 Spring Boot 学习阶段 | 见"未来方向" |

---

## 四、踩坑沉淀入口

- 仓库记忆：`/memories/repo/java-pra-traps.md` —— 200+ 条真实踩坑（含教学方式校准）
- 每日笔记：`docs/dayN-learning-notes.md` —— 概念 + 代码 + 坑 + 自评

---

## 五、未来学习方向（整合 learning-summary.md）

| 方向 | 内容 | 推荐序 |
|:---:|------|:---:|
| **A. Spring Boot 过渡** | IoC/AOP/事务/MVC/MyBatis，做 REST 接口 | ⭐ 主线 |
| **B. 补薄弱点** | 泛型通配符、并行流陷阱、虚拟线程 pin、设计模式命名 | ⭐ 穿插 |
| **C. 读开源源码** | JUnit / Spring / Hibernate 源码 | 中高级 |
| **D. JVM 深入** | 内存模型 / GC / 类加载 | 面试加分 |
| **E. 并发深入** | AQS / ThreadLocal / 线程池调优 | 面试高频 |
| **F. 工程化** | Maven / Git CI / Mockito / 设计模式 | 提升效率 |
| **G. 综合实战** | 完整项目（记账 App / 博客系统）| 最扎实 |

> 完整说明见 [learning-summary.md](learning-summary.md#六未来学习方向可选其一或组合)

---

## 六、下一步

18 课路线已全部完成 ✅。说方向字母（A-G）即开始对应阶段学习。
