# Java 学习总结（2026-07-31 → 2026-08-15）

> 项目：java-pra ｜ JDK 25 + Gradle 9.6.1 + JUnit Jupiter 6.0.1
> 成果：16 天 / 18 课 / 14 个主题包 / **300 个测试全绿** / 8 个自研 Lab 系列
> 本文档是全部 day 笔记 + roadmap 的一次总整理，配合 [00-learning-roadmap.md](00-learning-roadmap.md) 与各日笔记使用。

---

## 一、旅程总览

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

## 二、知识体系地图（14 个主题包）

| 主题 | 关键内容 | 测试数 |
|------|---------|:---:|
| **集合** | List/Set/Map 选型、不可变集合、迭代器 fail-fast、频次统计 | 11 |
| **泛型** | 类型参数、泛型方法/上界、PECS、类型擦除、泛型数组、泛型 DAO | 30 |
| **Stream** | 中间/终结操作、Collectors 全套、reduce、groupingBy/partitioningBy/flatMap、自定义 Collector、并行流 | 34 |
| **现代特性** | lambda/方法引用、Record、Sealed、Pattern Matching、Switch Pattern、Text Blocks | 15 |
| **并发基础** | 线程、synchronized、volatile、Atomic、wait/notify、生产者消费者 | 15 |
| **异常** | 家族树、受检/非受检、try-with-resources、finally 陷阱、异常链 | 13 |
| **IO** | 字节/字符流、装饰器、缓冲流、PrintWriter、try-with-resources | 15 |
| **反射** | Class 获取、Field/Method/Constructor、setAccessible、异常解包 | 24 |
| **注解** | 元注解、自定义注解、注解 + 反射实战（校验器）| 20 |
| **枚举** | 本质、字段/构造器、抽象方法、EnumSet/EnumMap、单例三防线、策略模式 | 56 |
| **java.time** | LocalDate/Time/DateTime、时区、Duration/Period、DateTimeFormatter | 13 |
| **Optional** | 创建/消费/链式 map·flatMap·filter、反模式 | 18 |
| **NIO** | Path/Files、ByteBuffer flip、Channel | 8 |
| **并发进阶** | ReentrantLock、Condition、CompletableFuture、线程池、虚拟线程 | 13 |
| **IoC 框架** | 注解三件套、懒加载、依赖注入、循环依赖检测、对照 Spring | 14 |

---

## 三、掌握度矩阵（综合各日自评）

| 主题 | 掌握度 | 状态 |
|------|:---:|------|
| 集合 | ⭐⭐⭐⭐ | 够用；缺 `Collections.unmodifiable` vs `List.of` 细节 |
| 泛型 | ⭐⭐⭐⭐ | PECS 已实战；缺 `?` 通配符边界深入 |
| Stream | ⭐⭐⭐⭐ | 收集器实战完成；缺 并行流陷阱实测 |
| 并发 | ⭐⭐⭐⭐ | 锁/条件/CF/线程池/虚拟线程全覆盖；缺 `lockInterruptibly`/饱和策略实战 |
| 异常 | ⭐⭐⭐⭐ | 已够用 |
| IO / NIO | ⭐⭐⭐⭐ | 流式 + 块式都过；缺 Selector 网络编程 |
| 反射 | ⭐⭐⭐⭐ | 已够用（IoC 里又实战一轮）|
| 注解 | ⭐⭐⭐⭐ | **本课程第二次深入**（IoC 里定义+读取+应用）|
| 枚举 | ⭐⭐⭐⭐ | 全景完成 |
| java.time | ⭐⭐⭐ | 缺 ZonedDateTime 夏令时深入 |
| Optional | ⭐⭐⭐ | 缺 反模式实操 |
| 现代特性 | ⭐⭐⭐⭐ | Record/Sealed/Pattern 实战完成 |
| **设计模式** | ⭐⭐⭐ | 会用（单例/工厂/策略）不熟命名——待补强 |
| **JVM / 内存模型** | ⭐⭐ | 只接触 volatile/线程安全概念——大空缺 |

---

## 四、现在能做什么（能力盘点）

1. **看懂 Spring Boot 核心机制**：IoC 容器原理、`@Component`/`@Inject`/`@Autowired`、反射 + 注解驱动的设计（Day16 亲手复刻了简化版）
2. **独立写中等复杂 Java 项目**：集合/Stream/异常/IO/并发/时间/Optional 全套工程 API 都实操过
3. **读开源库源码**：JUnit 怎么扫 `@Test`、Hibernate Validator 怎么校验——这套"注解 + 反射"心智模型已建立
4. **调试与测试习惯**：300 个测试、假通过防坑、断言边界、`gradle test` 验证闭环
5. **踩坑方法论**：16 天沉淀 200+ 条真实坑（仓库记忆 `java-pra-traps.md`），形成"先概念→再实战→踩坑→沉淀"的学习循环

---

## 五、学习沉淀（方法 + 习惯）

- **教学法**：概念具体先行（生活例子 + 对比表 + 最小代码）→ 独立实现 → 测试校验 → 坑点整理 → Javadoc 补全
- **测试驱动**：每课写覆盖测试，agent 校验时防"绿灯假象"（断言非 null、看 XML 明细）
- **现代特性优先**：JDK 25 环境始终展示 Records/Sealed/Pattern/Virtual Threads 现代写法
- **笔记按天拆分**：day1~day16 详尽笔记 + 每日自评，便于回看

---

## 六、未来学习方向（可选其一或组合）

| 方向 | 内容 | 前置 | 适合你吗 |
|------|------|------|---------|
| **A. Spring Boot 过渡**（推荐）| IoC/AOP/事务/MVC/MyBatis，做 REST 接口 | 已具备（IoC 已懂）| ✅ 最顺的路，把理论变生产力 |
| **B. 补薄弱点** | 泛型通配符、并行流陷阱、虚拟线程 pin、设计模式命名 | 无 | ✅ 建议穿插在 A 之间做 |
| **C. 源码阅读** | 读 JUnit/Spring/Hibernate 源码（已有反射+注解心智模型）| 需要 A 打底 | 中高级 |
| **D. JVM 深入** | 内存模型、GC、类加载（呼应当前 volatile/反射理解）| 无 | 面试加分、理解并发本质 |
| **E. 并发深入** | AQS、ThreadLocal、锁优化、线程池调优 | 已具备（并发进阶）| 面试高频、偏难 |
| **F. 工程化** | Maven/Git 分支/CI、Mockito 测试、设计模式 | 无 | 提升写码效率 |
| **G. 综合实战** | 用所学做一个完整项目（如记账 App/博客系统）| 已具备 | 巩固知识最扎实的方式 |

> 我的建议顺序：**A（主线）→ B（穿插补漏）→ 按兴趣选 D/E/C/F/G**。想直接开始哪个，说方向字母即可。
