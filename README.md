# java-pra

> Java 从入门到精通 · 个人学习仓库
>
> 已有基础语法底子，重点进修进阶特性（集合 / 泛型 / 并发 / 现代特性等）。
> 学一课、写一课、测一课、归档一课。
> **状态：18 课全部完成 ✅（16 天 / 14 个主题包 / 300 测试全绿）**

## 技术栈

| 项 | 版本 |
|----|------|
| Java 工具链 | JDK 25 |
| Gradle | 9.6.1（Kotlin DSL + version catalog）|
| 测试框架 | JUnit Jupiter 6.0.1 |
| 第三方库 | Guava 33.5.0-jre |

## 目录结构

```
app/src/main/java/learning/pra/
├── App.java                  # 入口（Gradle init 模板）
├── collections/              # 集合地基：List / Set / Map / 不可变集合
│   └── ListLab.java
├── generics/                 # 泛型：类型参数 / 边界 / PECS / 擦除 / 泛型进阶实战
│   ├── GenericsLab.java
│   └── GenericPecsLab.java
├── stream/                   # Stream：filter/map/collect/groupingBy / 进阶收集器
│   ├── StreamLab.java
│   └── StreamAdvLab.java
├── modern/                   # 现代特性：Records / Sealed / Pattern Matching switch
│   ├── ModernLab.java
│   └── VehicleLab.java       # 第十六课：Sealed 三层层级
├── concurrent/               # 并发：锁 / 条件 / 异步 / 线程池 / 虚拟线程
│   ├── ConcurrencyLab.java      # 第五课：Thread / synchronized / wait·notify
│   ├── ReentrantLockLab.java    # 第十七课：ReentrantLock 手动锁 + 超时
│   ├── ConditionLab.java        # 第十七课：Condition 有界阻塞队列
│   ├── AsyncAggregateLab.java   # 第十七课：CompletableFuture 并发聚合
│   ├── ExecutorPoolLab.java     # 第十七课：线程池复用 + Future 取结果
│   └── VirtualThreadLab.java    # 第十七课：虚拟线程万级任务
├── exceptions/               # 异常体系：受检/非受检 / try-with-resources / 异常链
│   ├── ExceptionLab.java
│   └── ConfigException.java
├── io/                       # IO：字符/字节流 / 装饰器 / Scanner·PrintWriter
│   └── IoLab.java
├── nio/                      # NIO 块式 IO：Path / Files / ByteBuffer
│   └── NioLab.java
├── reflection/               # 反射：Class / Field / Method / Constructor 三剑客
│   └── ReflectionLab.java
├── annotations/              # 注解：内置 / 元注解 / 自定义 / 反射实战
│   ├── AnnotationsLab.java
│   └── ValidatorLab.java
├── enums/                    # 枚举：本质 / EnumSet·EnumMap / 策略模式实战
│   └── EnumLab.java
├── time/                     # java.time：LocalDate / ZonedDateTime / 格式化
│   └── DateLab.java
├── optional/                 # Optional 深入：map/flatMap/orElse 链式取值
│   └── OptionalLab.java
└── ioc/                      # 第十八课：迷你 IoC 容器（注解 + 反射 + 泛型 + 设计模式）
    ├── MiniContainer.java    # 容器：登记 / 懒加载 / 依赖注入 / 循环依赖检测
    ├── Component.java        # @Component 注解
    ├── Inject.java           # @Inject 注解
    ├── Singleton.java        # @Singleton 注解
    ├── OrderService.java     # 依赖方 Bean（@Component @Singleton）
    └── OrderRepository.java  # 被依赖方 Bean（@Component）

app/src/test/java/learning/pra/   # 测试与源码同包（各包同构）
├── collections/ListLabTest.java
├── generics/GenericsLabTest.java
├── generics/GenericPecsLabTest.java
├── stream/StreamLabTest.java
├── stream/StreamAdvLabTest.java
├── modern/ModernLabTest.java
├── concurrent/ConcurrencyLabTest.java
├── concurrent/ReentrantLockLabTest.java
├── concurrent/ConditionLabTest.java
├── concurrent/AsyncAggregateLabTest.java
├── concurrent/ExecutorPoolLabTest.java
├── concurrent/VirtualThreadLabTest.java
├── exceptions/ExceptionLabTest.java
├── io/IoLabTest.java
├── nio/NioLabTest.java
├── reflection/ReflectionLabTest.java
├── annotations/AnnotationsLabTest.java
├── annotations/AnnotationsLabLabelTest.java
├── annotations/ValidatorLabTest.java
├── enums/EnumLabTest.java
├── time/DateLabTest.java
├── optional/OptionalLabTest.java
├── ioc/AnnotationLabTest.java
└── ioc/MiniContainerTest.java

docs/                         # 学习文档（路线 / 总结 / 每日笔记）
├── 00-learning-roadmap.md   # 总览 + 路线 + 总结（18 课全部完成 + 未来方向 A-G）
├── java-reading-guide.md    # 读码速查：词汇 / Javadoc / 报错
├── day1-learning-notes.md ~ day16-learning-notes.md   # 每日详尽笔记
gradle/libs.versions.toml     # 依赖版本集中管理
```

## 学习路线

完整总览与未来方向见 [docs/00-learning-roadmap.md](docs/00-learning-roadmap.md)，读码速查见 [docs/java-reading-guide.md](docs/java-reading-guide.md)。
**18 课全部完成 ✅**，下一步从未来方向 A-G 中自选（A Spring Boot / B 补薄弱 / C 读源码 / D JVM / E 并发深入 / F 工程化 / G 综合实战）。

```mermaid
graph LR
  A[1. 集合] --> B[2. 泛型]
  B --> C[3. Stream]
  C --> D[4. 现代特性入门]
  D --> E[5. 并发基础]
  E --> F[6. 异常]
  F --> G[7. IO 流]
  G --> H[8. 反射]
  H --> I[9. 注解]
  I --> J[10. 枚举]
  J --> K[11. java.time]
  K --> L[12. Optional]
  L --> M[13. 泛型进阶实战]
  M --> N[14. Stream 进阶]
  N --> O[15. NIO]
  O --> P[16. 现代特性速览]
  P --> Q[17. 并发进阶]
  Q --> R[18. 综合实战 IoC]
```

## 常用命令

```bash
./gradlew test                                  # 运行全部测试
./gradlew test --tests <Class>                  # 运行单个测试类
./gradlew run                                   # 运行 App
./gradlew build                                 # 完整构建
./gradlew javadoc                               # 生成 javadoc 文档
./gradlew --version                             # 查看 wrapper / JDK 版本
```

> 沙箱环境若 `~/.gradle` 只读导致 wrapper 失败，可用系统 `gradle`：
> ```bash
> gradle test --no-daemon --gradle-user-home="$TMPDIR/gradle-home"
> ```

## 学习约定

- **教学模式**：每个主题先讲概念 -> 自己写实现 -> 自己写测试 -> 用 `gradle test` 校验。
- **一课一包**：每个主题独立成包（`collections/`、`generics/`…），不互相依赖。
- **现代特性优先**：JDK 25 环境下，遇到 Records / Pattern Matching / Sealed Classes / Virtual Threads / Switch Pattern 等，优先展示现代写法。
- **显式类型**：优先显式类型声明（`List<Thread> threads = ...`），不用 `var`，强化泛型/集合认知。

## 进度

### 已完成（18 课 ✅）

- [x] 第一课：集合框架（[ListLab](app/src/main/java/learning/pra/collections/ListLab.java)）- 去重 / 反转 / 频率 / max / 不可变集合
- [x] 第二课：泛型（[GenericsLab](app/src/main/java/learning/pra/generics/GenericsLab.java)）- 类型参数 / 上界 / PECS / 类型擦除 / Stack 泛型类
- [x] 第三课：Stream（[StreamLab](app/src/main/java/learning/pra/stream/StreamLab.java)）- filter/map/collect/groupingBy/Optional
- [x] 第四课：现代特性入门（[ModernLab](app/src/main/java/learning/pra/modern/ModernLab.java)）- lambda / 方法引用 / 函数式接口
- [x] 第五课：并发基础（[ConcurrencyLab](app/src/main/java/learning/pra/concurrent/ConcurrencyLab.java)）- Thread / synchronized / volatile / wait·notify，7 测试
- [x] 第六课：异常体系（[ExceptionLab](app/src/main/java/learning/pra/exceptions/ExceptionLab.java) / [ConfigException](app/src/main/java/learning/pra/exceptions/ConfigException.java)）- 受检/非受检 / try-with-resources / 异常链，11 测试
- [x] 第七课：IO 流（[IoLab](app/src/main/java/learning/pra/io/IoLab.java)）- 字节/字符流 / 装饰器 / Scanner·PrintWriter，15 测试
- [x] 第八课：反射（[ReflectionLab](app/src/main/java/learning/pra/reflection/ReflectionLab.java)）- Class / Field / Method / Constructor 三剑客，24 测试
- [x] 第九课：注解（[AnnotationsLab](app/src/main/java/learning/pra/annotations/AnnotationsLab.java) / [ValidatorLab](app/src/main/java/learning/pra/annotations/ValidatorLab.java)）- 内置注解 / 元注解 / 自定义注解 / 表单校验器实战，20 测试
- [x] 第十课：枚举（[EnumLab](app/src/main/java/learning/pra/enums/EnumLab.java)）- 本质 / 字段构造器 / EnumSet·EnumMap / 策略模式实战，56 测试
- [x] 第十一课：`java.time`（[DateLab](app/src/main/java/learning/pra/time/DateLab.java)）- LocalDate / ZonedDateTime / Duration·Period / DateTimeFormatter
- [x] 第十二课：`Optional` 深入（[OptionalLab](app/src/main/java/learning/pra/optional/OptionalLab.java)）- map / flatMap / orElse / 嵌套安全取值
- [x] 第十三课：泛型进阶实战（[GenericPecsLab](app/src/main/java/learning/pra/generics/GenericPecsLab.java)）- PECS / 类型擦除 workaround / 泛型数组
- [x] 第十四课：Stream 进阶（[StreamAdvLab](app/src/main/java/learning/pra/stream/StreamAdvLab.java)）- groupingBy / partitioningBy / 自定义 Collector
- [x] 第十五课：NIO（[NioLab](app/src/main/java/learning/pra/nio/NioLab.java)）- Path / Files / ByteBuffer / 便捷读写，8 测试
- [x] 第十六课：现代特性速览（[VehicleLab](app/src/main/java/learning/pra/modern/VehicleLab.java)）- Records / Sealed / Pattern Matching / Switch Pattern / Text Blocks
- [x] 第十七课：并发进阶（[ReentrantLockLab](app/src/main/java/learning/pra/concurrent/ReentrantLockLab.java) / [ConditionLab](app/src/main/java/learning/pra/concurrent/ConditionLab.java) / [AsyncAggregateLab](app/src/main/java/learning/pra/concurrent/AsyncAggregateLab.java) / [ExecutorPoolLab](app/src/main/java/learning/pra/concurrent/ExecutorPoolLab.java) / [VirtualThreadLab](app/src/main/java/learning/pra/concurrent/VirtualThreadLab.java)）- 锁 / 条件 / 异步聚合 / 线程池 / 虚拟线程，13 测试
- [x] 第十八课：综合实战 · 迷你 IoC 框架（[MiniContainer](app/src/main/java/learning/pra/ioc/MiniContainer.java) + 注解三件套 + [OrderService](app/src/main/java/learning/pra/ioc/OrderService.java)）- IoC/DI / 懒加载 / 依赖注入 / 循环依赖检测 / 对照 Spring，14 测试

### 未来方向（A-G，详见 [00-learning-roadmap.md](docs/00-learning-roadmap.md)）

- **A** Spring Boot 过渡 ｜ **B** 补薄弱点 ｜ **C** 读开源源码 ｜ **D** JVM 深入 ｜ **E** 并发深入 ｜ **F** 工程化 ｜ **G** 综合实战

## 学习笔记

详细的每日学习总结（知识点 + 真实坑 + 待补强项）见 [docs/](docs/) 目录：

| 笔记 | 日期 | 内容 |
|------|------|------|
| [读码速查](docs/java-reading-guide.md) | 08-15 | 词汇 / Javadoc / 报错速查 |
| [00 路线总览](docs/00-learning-roadmap.md) | 08-15 | 全程学习路线（18 课全部完成）|
| [day1](docs/day1-learning-notes.md) | 07-31 | 集合 / 泛型 / Stream / 现代特性 |
| [day2](docs/day2-learning-notes.md) | 08-01 | 并发与多线程 |
| [day3](docs/day3-learning-notes.md) | 08-02 | 异常体系 |
| [day4](docs/day4-learning-notes.md) | 08-03 | IO 流体系 |
| [day5](docs/day5-learning-notes.md) | 08-04 | 反射 |
| [day6](docs/day6-learning-notes.md) | 08-05 | 注解（含表单校验器实战）|
| [day7](docs/day7-learning-notes.md) | 08-06 | 枚举（含策略模式实战）|
| [day8](docs/day8-learning-notes.md) | 08-07 | `java.time` 日期时间 |
| [day9](docs/day9-learning-notes.md) | 08-08 | `Optional` 深入 |
| [day10](docs/day10-learning-notes.md) | 08-09 | 泛型进阶实战 |
| [day11](docs/day11-learning-notes.md) | 08-10 | Stream 进阶 |
| [day12](docs/day12-learning-notes.md) | 08-11 | NIO 与现代 IO |
| [day13](docs/day13-learning-notes.md) | 08-12 | 现代特性速览（Records / Sealed）|
| [day14](docs/day14-learning-notes.md) | 08-13 | 现代特性速览（instanceof / Switch Pattern / Text Blocks）|
| [day15](docs/day15-learning-notes.md) | 08-14 | 并发进阶详尽版（锁 / 条件 / 异步 / 线程池 / 虚拟线程）|
| [day16](docs/day16-learning-notes.md) | 08-15 | 迷你 IoC 框架详尽版（注解+反射深潜 / 懒加载 / 循环依赖 / 对照 Spring）|

## 备注

- 包名使用 `learning.pra`，**不要用 `java.*` 开头**（JDK 9+ 模块系统会抛 `Prohibited package name`）。
- 沙箱 `/tmp` 只读，测试需临时文件时写到 workspace 内 `build/tmp/`（见测试代码）。
