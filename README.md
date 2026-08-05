# java-pra

> Java 从入门到精通 · 个人学习仓库
>
> 已有基础语法底子，重点进修进阶特性（集合 / 泛型 / 并发 / 现代特性等）。
> 学一课、写一课、测一课、归档一课。

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
├── generics/                 # 泛型地基：类型参数 / 边界 / PECS / 类型擦除
│   └── GenericsLab.java
├── stream/                   # 函数式 / Stream：filter/map/collect/groupingBy/Optional
│   └── StreamLab.java
├── modern/                   # 现代特性：Records / Sealed / Pattern Matching switch
│   └── ModernLab.java
├── concurrent/               # 并发：Thread / synchronized / Atomic / volatile / BlockingQueue / 虚拟线程 / CompletableFuture
│   └── ConcurrencyLab.java
├── exceptions/               # 异常体系：受检/非受检 / try-with-resources / 异常链 / 自定义异常
│   ├── ExceptionLab.java
│   └── ConfigException.java
├── io/                       # IO 流：字符流 / 字节流 / 装饰器 / Scanner·PrintWriter / NIO.2
│   └── IoLab.java
├── reflection/               # 反射：Class / Field / Method / Constructor 三剑客
│   └── ReflectionLab.java
└── annotations/              # 注解：内置 / 元注解 / 自定义 / 注解+反射实战
    ├── AnnotationsLab.java
    └── ValidatorLab.java

app/src/test/java/learning/pra/   # 测试与源码同包（各包同构）
├── collections/ListLabTest.java
├── concurrent/ConcurrencyLabTest.java
├── exceptions/ExceptionLabTest.java
├── io/IoLabTest.java
├── reflection/ReflectionLabTest.java
└── annotations/
    ├── AnnotationsLabTest.java
    ├── AnnotationsLabLabelTest.java
    └── ValidatorLabTest.java

docs/                         # 每日学习笔记（知识点 + 真实坑 + 待补强项）
├── 00-learning-roadmap.md   # 全程学习路线总览
├── day1-learning-notes.md ~ day6-learning-notes.md
gradle/libs.versions.toml     # 依赖版本集中管理
```

## 学习路线

完整路线见 [docs/00-learning-roadmap.md](docs/00-learning-roadmap.md)。当前进度：第 9 课已完成，下一课为第 10 课「枚举」。

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

### 已完成（9 课）

- [x] 第一课：集合框架（[ListLab](app/src/main/java/learning/pra/collections/ListLab.java)）- 去重 / 反转 / 频率 / max / 不可变集合
- [x] 第二课：泛型（[GenericsLab](app/src/main/java/learning/pra/generics/GenericsLab.java)）- 类型参数 / 上界 / PECS / 类型擦除 / Stack 泛型类
- [x] 第三课：Stream（[StreamLab](app/src/main/java/learning/pra/stream/StreamLab.java)）- filter/map/collect/groupingBy/Optional
- [x] 第四课：现代特性入门（[ModernLab](app/src/main/java/learning/pra/modern/ModernLab.java)）- lambda / 方法引用 / 函数式接口
- [x] 第五课：并发基础（[ConcurrencyLab](app/src/main/java/learning/pra/concurrent/ConcurrencyLab.java)）- Thread / synchronized / volatile / wait·notify，7 测试
- [x] 第六课：异常体系（[ExceptionLab](app/src/main/java/learning/pra/exceptions/ExceptionLab.java) / [ConfigException](app/src/main/java/learning/pra/exceptions/ConfigException.java)）- 受检/非受检 / try-with-resources / 异常链，11 测试
- [x] 第七课：IO 流（[IoLab](app/src/main/java/learning/pra/io/IoLab.java)）- 字节/字符流 / 装饰器 / Scanner·PrintWriter，15 测试
- [x] 第八课：反射（[ReflectionLab](app/src/main/java/learning/pra/reflection/ReflectionLab.java)）- Class / Field / Method / Constructor 三剑客，24 测试
- [x] 第九课：注解（[AnnotationsLab](app/src/main/java/learning/pra/annotations/AnnotationsLab.java) / [ValidatorLab](app/src/main/java/learning/pra/annotations/ValidatorLab.java)）- 内置注解 / 元注解 / 自定义注解 / 表单校验器实战，20 测试

### 下一阶段（10-18 课，详见 [学习路线](docs/00-learning-roadmap.md)）

- [ ] 第十课：枚举（enum）
- [ ] 第十一课：`java.time` 日期时间
- [ ] 第十二课：`Optional` 深入
- [ ] 第十三课：泛型进阶实战（PECS 实战 / 类型擦除 / 泛型数组）
- [ ] 第十四课：Stream 进阶（groupingBy / flatMap / 自定义 Collector）
- [ ] 第十五课：NIO 与现代 IO
- [ ] 第十六课：现代特性速览（Records / Sealed / Pattern Matching）
- [ ] 第十七课：并发进阶（ReentrantLock / CompletableFuture / Virtual Threads）
- [ ] 第十八课：综合实战 -- 微型 IoC 框架

## 学习笔记

详细的每日学习总结（知识点 + 真实坑 + 待补强项）见 [docs/](docs/) 目录：

| 笔记 | 日期 | 内容 |
|------|------|------|
| [00 路线总览](docs/00-learning-roadmap.md) | 08-05 | 全程学习路线 + 薄弱点识别 + 节奏建议 |
| [day1](docs/day1-learning-notes.md) | 07-31 | 集合 / 泛型 / Stream / 现代特性 |
| [day2](docs/day2-learning-notes.md) | 08-01 | 并发与多线程 |
| [day3](docs/day3-learning-notes.md) | 08-02 | 异常体系 |
| [day4](docs/day4-learning-notes.md) | 08-03 | IO 流体系 |
| [day5](docs/day5-learning-notes.md) | 08-04 | 反射 |
| [day6](docs/day6-learning-notes.md) | 08-05 | 注解（含表单校验器实战）|

## 备注

- 包名使用 `learning.pra`，**不要用 `java.*` 开头**（JDK 9+ 模块系统会抛 `Prohibited package name`）。
- 沙箱 `/tmp` 只读，测试需临时文件时写到 workspace 内 `build/tmp/`（见测试代码）。
