# Java 学习笔记 - Day 13（2026-08-12）

> 学习项目：java-pra（JDK 25 + Gradle 9.6.1 + JUnit Jupiter 6.0.1）
> 进度：第十六课（现代特性速览 JDK 16-25）-- 概念点1 Records ✅ + 概念点2 Sealed ✅（VehicleLab 实战）
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

### 概念点 2：Sealed Classes 深入（VehicleLab 实战）

**生活例子**：`sealed` 像封闭的家庭登记簿——家长列出允许继承的子女名单（`permits`），名单外的人不能自称是这个家族后代。

#### 子类必须"三选一"修饰符

`permits` 列出的每个直接子类，声明时**必须**选一种：

| 修饰符 | 含义 | 后代 |
|--------|------|------|
| `final` | 封闭到底 | 不能再被继承 |
| `sealed` | 继续封闭 | 自己再列 `permits` |
| `non-sealed` | 重新开放 | 任何人都能继承 |

普通 `class` 直接 `implements` sealed 接口**编译错**，必须显式声明三选一。

#### `permits` 位置约束
- 未模块化：直接子类必须在**同一个包**
- 模块化：子类可在同一模块，但需显式 `permits`（跨包无法省略）
- 省略 `permits` 前提：子类与父类**同文件**，编译器自动推断

#### 穷尽性（exhaustiveness）
编译器知道 sealed 全部子类 → switch 覆盖全部分支后**可不写 `default`**，漏分支直接编译错。

#### 实战：`VehicleLab`（三层层级）
```java
sealed interface VehicleLab permits Car, Truck, Bike {   // ① 接口密封
    static String describe(VehicleLab v) {
        return switch (v) {
            case Car c -> "汽车: " + c.brand();
            case Pickup p -> "皮卡: " + p.loadKg();   // 子类型分支放前面
            case Truck t -> "卡车";
            case Bike b -> "单车: " + b.gears();
            // 不加 default：sealed 保证穷尽
        };
    }
}
final record Car(String brand) implements VehicleLab {}     // ② final + record
sealed class Truck implements VehicleLab permits Pickup {}  // ③ sealed 继续封闭
final class Pickup extends Truck { /* final，字段 loadKg */ } // ④ final 普通 class
non-sealed class Bike implements VehicleLab { /* non-sealed 开放 */ } // ⑤
```

#### 踩坑（重要）：穷尽性按 `permits` 直接子类型算
第一版 `describe` 只写 `Car / Pickup / Bike` 三分支，编译报 **"switch 表达式不包含所有可能的输入值"**：
- sealed 穷尽性要求覆盖 `permits` 列出的**直接子类型**（`Car`/`Truck`/`Bike`）
- `Pickup` 是 `Truck` 的**子类**，不能顶替 `Truck` 分支
- 修正：补 `case Truck` 分支，且 `case Pickup`（子类型）放 `case Truck` **之前**先匹配

> 类比：`permits` 是三兄弟（Car/Truck/Bike），Pickup 是 Truck 的儿子——switch 要覆盖三兄弟，不能只认孙子。

#### 完成情况
- `VehicleLabTest`：`describeCar` / `describePickup` / `describeBike` 3 测试全绿
- 代码：[VehicleLab.java](app/src/main/java/learning/pra/modern/VehicleLab.java) / 测试：[VehicleLabTest.java](app/src/test/java/learning/pra/modern/VehicleLabTest.java)

---

## 三、下一步

- 第十六课概念点 3：Pattern Matching for instanceof 深入 → 概念点 4 Switch Pattern 深入 → 概念点 5 Text Blocks → 实战重构表达式树
