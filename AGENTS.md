# AGENTS.md — java-pra 学习项目

## 项目性质
个人 Java 学习项目（从入门到精通）。**非生产代码**，目标是边学边练。
用户已有 Java 基础语法知识（变量/控制流/类与对象/基础集合等），**重点进修进阶特性**，不必再讲解基础语法。

## 技术栈
- 构建工具：Gradle 9.6.1（Kotlin DSL + version catalog）
- Java 工具链：JDK 25（见 [app/build.gradle.kts](app/build.gradle.kts)）
- 测试：JUnit Jupiter 6.0.1
- 依赖：Guava 33.5.0-jre

## 常用命令
```bash
./gradlew test                  # 运行测试
./gradlew run                   # 运行应用
./gradlew build                 # 完整构建
./gradlew --version             # 查看 wrapper / JDK 版本
```

> 沙箱环境提示：`~/.gradle` 在沙箱中只读，`./gradlew` 会因无法下载 wrapper 发行版而失败。
> 应改用系统已安装的 `gradle` 命令并重定向 home：
> `gradle test --no-daemon --gradle-user-home="$TMPDIR/gradle-home"`

## 目录结构
- `app/src/main/java/learning/pra/` — 源码，包名 `learning.pra`
- `app/src/test/java/learning/pra/` — 测试
- `gradle/libs.versions.toml` — 依赖版本集中管理

## 已知问题（陷阱）
- **禁止使用 `java.*` 开头的包名**：JDK 9+ 模块系统严格校验，`java.pra` 会抛 `Prohibited package name`。当前已迁移到 `learning.pra`，新增代码请勿再用 `java.` 前缀。

## 助学约定
- **教学模式（默认）**：agent 扮演教师，先讲概念与基础，再布置任务由用户独立完成实现与测试，agent 仅校验。不要直接给出完整答案代码。
- **可运行验证**：用户写完后，agent 用 `gradle test` 校验，反馈通过/失败原因。
- **特性清单作为学习索引**：用户希望系统性了解 Java 特性再选修，涉及"Java 有哪些特性/列出特性"类请求时，按主题分组给出清单（如 OOP/集合/泛型/并发/IO/反射/注解/现代特性等），并标注难度与适用场景，供用户选修。
- **现代特性优先**：JDK 25 环境下，遇到 Records / Pattern Matching / Sealed Classes / Virtual Threads / Switch Pattern 等新特性，主动展示现代写法。
