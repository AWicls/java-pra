# Java 学习笔记 - Day 13（2026-08-12）

> 学习项目：java-pra（JDK 25 + Gradle 9.6.1 + JUnit Jupiter 6.0.1）
> 进度：第十六课（现代特性速览 JDK 16-25）-- 概念点 1 Records（总结）
> 说明：Day1 ModernLab 已接触 record/sealed/switch 基础，本课系统化 + 深入

---

## 一、第十六课：现代特性速览（JDK 16-25）

### 概念点 1：Records 知识点总结

**本质**：`record` 是**不可变数据载体**的语法糖，编译器自动生成 `final` 字段 + 规范构造器 + `accessor()` 访问器 + `equals`/`hashCode`/`toString`。

```java
record Point(int x, int y) {}
// 等价手写：final class Point，字段 final，getter 是 x()/y() 不是 getX()
```

#### 三个自动生成的东西
| 自动生成 | 说明 |
|---------|------|
| 字段 | `private final`，不可变 |
| 访问器 | `x()` / `y()`（**无 `get` 前缀**） |
| 样板方法 | `equals`/`hashCode`/`toString`（值语义，非引用语义） |

#### 构造器三形态（本课核心）
| 形态 | 写法 | 用途 |
|------|------|------|
| 默认 | 不写 | 直接赋值 |
| 紧凑构造器 | `public Point { ... }`（无参） | 校验/规范化；参数 `final` **不能赋值** |
| 规范构造器 | `public Point(int x, int y) { ... }` | 重写赋值逻辑，**必须手动给每个字段赋值** |

> 紧凑构造器里参数名 = 字段名；两者不能同时写。

```java
record Age(int value) {
    public Age { if (value < 0) throw new IllegalArgumentException(); }  // 紧凑构造器校验
    static Age of(int v) { return new Age(v); }   // 静态工厂
}
```

#### 可自定义的内容
- 额外的**实例方法**、**静态方法**、**静态工厂**
- 用**紧凑构造器**做参数校验
- 重写访问器（`@Override`）

#### 限制（不可做）
- 字段**必须全 final**，不能加普通可变字段（静态字段可以）
- record 不能继承别的类（隐式继承 `java.lang.Record`）
- 可以 `implements` 接口（常用：配合 `sealed` + `switch`）

#### 常用配合
- **`sealed` 接口**：`sealed interface Shape permits Circle, Rectangle`，子类用 record 实现
- **Pattern Matching / switch**：`case Circle r -> ...` 直接访问 `r.radius()`，`sealed` 保证 switch 穷尽可不写 `default`
- **Stream**：`map` 产出 record，`toList()` 收集

#### 典型用途
- DTO / VO（数据传输对象）
- 多返回值（`record Pair(a, b)`）
- 不可变配置项

#### 一句话记忆
record = "字段全 final + 自动样板方法的不可变载体"，构造时想校验/规范化就用**紧凑构造器**，想重写赋值才用**规范构造器**。

---

## 二、下一步

- 第十六课概念点 2：Sealed Classes 深入（`permits` 子类位置约束 / `non-sealed` / 穷尽性）
