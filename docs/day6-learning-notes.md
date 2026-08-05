# Java 学习笔记 - Day 6（2026-08-05）

> 学习项目：java-pra（JDK 25 + Gradle 9.6.1 + JUnit Jupiter 6.0.1）
> 进度：第九课（注解 Annotation）
> 代码：
> - [app/src/main/java/learning/pra/annotations/AnnotationsLab.java](app/src/main/java/learning/pra/annotations/AnnotationsLab.java)
> - [app/src/main/java/learning/pra/annotations/ValidatorLab.java](app/src/main/java/learning/pra/annotations/ValidatorLab.java)
> 测试：
> - [AnnotationsLabTest.java](app/src/test/java/learning/pra/annotations/AnnotationsLabTest.java)（6 测试）
> - [AnnotationsLabLabelTest.java](app/src/test/java/learning/pra/annotations/AnnotationsLabLabelTest.java)（4 测试）
> - [ValidatorLabTest.java](app/src/test/java/learning/pra/annotations/ValidatorLabTest.java)（10 测试）
> 总计：20 个测试全绿

---

## 一、第九课：注解（Annotation）

### 9.1 注解初识 -- 代码的"便利贴标签"

**核心概念**：注解 = 给代码元素（类/方法/字段/参数）贴的"便利贴"。**不改变代码本身的执行逻辑**，但可以被编译器 / 工具 / 框架读取，做出额外处理。

**关键认知**：注解**自身不会执行任何代码**，它只是"数据"。真正起作用的是**读取注解的一方**：
- 编译器读 `@Override` 报错
- JUnit 读 `@Test` 跑测试
- Spring 读 `@Component` 注册 Bean
- 反射是"读注解"的主要手段（注解 + 反射是一对好搭档）

**注解 vs 注释 vs 接口 对比**：

| 维度 | 注释 `//` 或 `/** */` | 注解 `@Xxx` | 接口 `interface` |
|------|----------------------|-------------|------------------|
| 给谁看 | 人 | 编译器/工具/框架 | 类（要实现）|
| 编译后还在吗 | ❌ 丢弃 | ✅ 看保留策略（见 9.2）| ✅ |
| 能携带数据吗 | ❌ 纯文本 | ✅ `@Xxx(key=value)` | ✅ 方法定义 |
| 会改变行为吗 | ❌ | ❌ 自身不改逻辑（靠读它的人改）| ✅ 调用即执行 |

### 9.1.1 JDK 内置四个常用注解

| 注解 | 贴在 | 作用 | 谁读它 | JDK |
|------|------|------|--------|-----|
| `@Override` | 方法 | 检查是否真的重写父类方法 | 编译器 | 1.5+ |
| `@Deprecated` | 类/方法/字段 | 标记"已过时，建议别用"，调用处划删除线 | 编译器 / IDE | 1.5+ |
| `@SuppressWarnings("xxx")` | 类/方法 | 抑制指定警告（如 `"unchecked"` 抑制泛型警告）| 编译器 | 1.5+ |
| `@FunctionalInterface` | 接口 | 检查接口有且仅有一个抽象方法（可写 lambda）| 编译器 | 8+ |

**`@Deprecated` + `@SuppressWarnings("deprecation")` 经典搭档**：前者制造警告，后者消除警告。

```java
static class Worker {
    @Deprecated                                    // 标记过时
    public String oldMethod() { return "oldMethod"; }

    @SuppressWarnings("deprecation")              // 抑制下面调 oldMethod 的过时警告
    public String useOld() { return oldMethod(); }
}
```

---

### 9.2 元注解 + 自定义注解

#### 9.2.1 元注解 -- "贴在注解上的注解"

**生活例子**：便利贴本身也有规则--"只能贴在书页上"、"要永久保留"。元注解就是规定注解本身规则的注解。

```java
@Target(ElementType.METHOD)          // 元注解 1：规定只能贴在方法上
@Retention(RetentionPolicy.RUNTIME)  // 元注解 2：规定保留到运行期
@interface MyAnnotation {            // @interface 是声明注解的关键字（不是 interface）
    String value();
}
```

#### 9.2.2 `@Target` -- 能贴在哪

`ElementType` 枚举常用值：

| 值 | 能贴在 |
|----|-------|
| `TYPE` | 类 / 接口 / 注解 / 枚举 |
| `METHOD` | 方法 |
| `FIELD` | 字段（含枚举常量）|
| `CONSTRUCTOR` | 构造器 |
| `PARAMETER` | 方法参数 |
| `LOCAL_VARIABLE` | 局部变量 |
| `ANNOTATION_TYPE` | 注解本身（元注解专用）|

多选写法：`@Target({ElementType.METHOD, ElementType.FIELD})`。不写 `@Target` = 所有元素都行。

#### 9.2.3 `@Retention` -- 保留多久（核心！）

`RetentionPolicy` 三个值：

| 策略 | 保留到何时 | 反射能读吗 | 典型例子 |
|------|----------|----------|---------|
| `SOURCE` | 只在源码，编译后丢弃 | ❌ | `@Override`、`@SuppressWarnings` |
| `CLASS` | 编译进 class 文件，但 JVM 不加载 | ❌（默认值，很少用）| 字节码工具用 |
| `RUNTIME` | 保留到运行期 | ✅ | `@Deprecated`、`@FunctionalInterface`、框架自定义注解 |

**口诀**：**反射要读注解，必须 `RUNTIME`**。自定义注解如果想让框架用反射读到，**必须显式写 `@Retention(RUNTIME)`**，因为默认是 `CLASS`（反射读不到）。

**回头验证四个内置注解的保留策略**（用反射读 `@Retention` 元注解）：

```java
// @Override 的定义（JDK 源码）
@Retention(RetentionPolicy.SOURCE)    // 只在源码，编译后丢弃 -> 反射读不到
@Target(ElementType.METHOD)
public @interface Override {}

// @Deprecated 的定义
@Retention(RetentionPolicy.RUNTIME)   // 保留到运行期 -> 反射可读
public @interface Deprecated {}
```

#### 9.2.4 两个元注解对比

| 元注解 | 回答的问题 | 取值类型 | 默认值 |
|--------|----------|---------|--------|
| `@Target` | "这注解能贴在什么元素上" | `ElementType` 枚举 | 不写 = 所有元素都行 |
| `@Retention` | "这注解保留多久" | `RetentionPolicy` 枚举 | 不写 = `CLASS`（默认）|

#### 9.2.5 自定义注解 `@Label` 实战

```java
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@interface Label {
    String value();        // 注解的属性，看起来像方法声明
}
```

**使用**：

```java
static class User {
    @Label("用户名")        // 单属性 value 可省略名字
    String name;
    @Label("年龄")
    int age;
}
```

**反射读取三剑客**（Field / Method / Constructor 通用）：

| API | 作用 | 找不到时 |
|-----|------|---------|
| `isAnnotationPresent(Xxx.class)` | 是否贴了某注解 | 返回 false |
| `getAnnotation(Xxx.class)` | 读指定注解 | 返回 null（要判空）|
| `getAnnotations()` | 读所有注解 | 返回空数组 |

**读取属性值**：`annotation.value()` 调用属性方法。

```java
Label label = field.getAnnotation(Label.class);
if (label != null) {                    // 必须判空！
    String labelText = label.value();   // 调属性方法拿值
}
```

---

### 9.3 实战：注解 + 反射 = 表单校验器

#### 9.3.1 场景与设计思路

**问题**：每字段手写 `if (name.length() < 3) ...` 字段一多就臃肿。

**方案**：校验规则**贴成注解**（声明式），通用 `Validator` 用反射遍历字段读注解做校验。这正是 Spring `@Valid` / Hibernate Validator 的核心原理（Bean Validation 规范）。

**对比**：

| 传统手写 | 注解 + 反射 |
|---------|------------|
| 每个字段写一段 if | 规则贴字段上，Validator 通用复用 |
| 加新字段要改校验方法 | 加新字段只需贴注解，Validator 不动 |
| 规则散落各处难维护 | 规则和字段贴在一起，一目了然 |

#### 9.3.2 多属性注解

```java
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@interface Length {
    int min();                                    // 属性 1，无默认，使用时必须给
    int max();                                    // 属性 2，无默认
    String message() default "长度不合法";         // 属性 3，带默认值
}
```

**关键语法**：

| 语法 | 含义 |
|------|------|
| `int min();` | 声明 int 属性（无默认，必填）|
| `String message() default "xxx";` | 声明带默认值的属性 |
| `@Length(min = 3, max = 20)` | 多属性按名字赋值 |
| `@Length(min = 3, max = 20, message = "名字太短")` | 可覆盖默认值 |
| `@Label("用户名")` | **特例**：只有一个 `value` 属性时才能省略名字 |

**注解属性类型限制**：只能用**编译期常量**--基本类型 / String / Class / 枚举 / 注解 / 上述类型的数组。**不能**放 `List` / `Map` / 任意对象。

#### 9.3.3 实战代码

**三个规则注解**：

```java
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@interface NotNull { }                    // 无属性，仅标记

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@interface Length {
    int min();
    int max();
    String message() default "长度不合法";
}

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@interface Range {
    int min();
    int max();
    String message() default "范围不合法";
}
```

**贴规则的 User 类**：

```java
static class User {
    @NotNull
    String name;

    @Length(min = 3, max = 20, message = "名字长度 3-20")
    String nickname;

    @Range(min = 0, max = 150, message = "年龄 0-150")
    int age;
}
```

**validate 方法（核心模式：遍历字段 → 读注解 → 做校验）**：

```java
public static List<String> validate(Object obj) throws IllegalAccessException {
    List<String> errors = new ArrayList<>();
    Field[] fields = obj.getClass().getDeclaredFields();   // ⚠️ 用 getDeclaredFields 不是 getFields

    for (Field field : fields) {
        field.setAccessible(true);                          // 破防 private
        Object value = field.get(obj);                      // 拿字段值

        // 检查 1：@NotNull
        if (field.isAnnotationPresent(NotNull.class)) {
            if (value == null) {
                errors.add(field.getName() + ": 不能为 null");
            }
        }

        // 检查 2：@Length（null 值跳过，让 @NotNull 管）
        Length length = field.getAnnotation(Length.class);
        if (length != null && value != null) {
            String str = (String) value;
            if (str.length() < length.min() || str.length() > length.max()) {
                errors.add(field.getName() + ": " + length.message());
            }
        }

        // 检查 3：@Range
        Range range = field.getAnnotation(Range.class);
        if (range != null && value != null) {
            int n = (int) value;                            // int 字段会装箱成 Integer
            if (n < range.min() || n > range.max()) {
                errors.add(field.getName() + ": " + range.message());
            }
        }
    }
    return errors;
}
```

---

## 二、真实遇到的坑（非理解错误，是实际编程陷阱）

### 坑 9.1：`@Override` 和 `@SuppressWarnings` 反射读不到

```java
Method m = MyWorker.class.getDeclaredMethod("oldMethod");
m.isAnnotationPresent(Override.class);   // ❌ false！明明贴了
```

**根因**：`@Override` 和 `@SuppressWarnings` 的 `@Retention` 是 `SOURCE`，编译后丢弃，运行期不存在。`@Deprecated` 和 `@FunctionalInterface` 是 `RUNTIME`，反射可读。

**对策**：对 SOURCE 保留的注解，改用"行为校验"（多态调用走子类实现、调用过时方法不报错）而非反射读取。

### 坑 9.2：`getFields()` vs `getDeclaredFields()` 用错

```java
Field[] fields = clazz.getFields();           // ❌ 只返回 public 字段（含继承）
Field[] fields = clazz.getDeclaredFields();  // ✅ 本类所有字段（含 private，不含继承）
```

**根因**：`User` 字段是 package-private（默认访问），`getFields()` 拿不到，结果 `validate` 永远返回空 List。这和反射课 8.2 的规律一致：**`get*` 系列 vs `getDeclared*` 系列完全对称**。

### 坑 9.3：`field.get(obj)` 抛检查异常

```java
Object value = field.get(obj);   // ❌ 抛 IllegalAccessException，没处理编译报错
```

**对策**：方法签名加 `throws IllegalAccessException`，或 try-catch（推荐后者，调用方省心）。

### 坑 9.4：注解属性用错 -- `toString()` vs `value()`

```java
Label annotation = field.getAnnotation(Label.class);
map.put(name, annotation.toString());   // ❌ 拿到 "@Label(value=用户名)" 这种字符串
map.put(name, annotation.value());     // ✅ 调属性方法拿到 "用户名"
```

**根因**：`toString()` 返回注解对象的字符串表示，不是属性值。要拿属性值必须调用对应的属性方法（`value()` / `min()` / `max()` / `message()`）。

### 坑 9.5：`getAnnotation` 返回 null 未判空导致 NPE

```java
Label annotation = field.getAnnotation(Label.class);
String s = annotation.value();   // ❌ 字段没贴 @Label 时 annotation 是 null，NPE
```

**对策**：遍历字段读注解必须判空：

```java
Label annotation = field.getAnnotation(Label.class);
if (annotation == null) {
    continue;   // 没贴 @Label 的字段跳过
}
```

### 坑 9.6：Map key 设计错误导致覆盖

```java
map.put("Worker", oldMethod);    // ❌
map.put("Worker", useOld);        // ❌ 第二次覆盖第一次
```

**根因**：`HashMap` 同 key 后写覆盖前写。题目要求 key 用注解名（`"FunctionalInterface"` / `"Deprecated"` / `"SuppressWarnings"` / `"Override"`），每个注解名唯一，不会覆盖。

### 坑 9.7：`@interface` 写成 `interface`

```java
interface Label { String value(); }     // ❌ 普通接口，不是注解
@interface Label { String value(); }    // ✅ 注解声明
```

**根因**：`@interface` 是声明注解的专用关键字，不是 `interface` 加个 `@`。编译期语法不同。

### 坑 9.8：局部接口贴 `@FunctionalInterface` 不合适

```java
public static Map<String, String> demoBuiltInAnnotations() {
    @FunctionalInterface                       // ❌ 语法上能贴局部接口，但语义不合适
    interface Greeter() {                      // ❌ 接口声明不带 ()，多了括号
        String greet(String name);
    }
}
```

**对策**：函数式接口通常是类层级或嵌套接口才有意义（局部接口没人从外面用它），提到方法外面作为静态内部接口。

### 坑 9.9：import 行混写

```java
import java.lang.reflect.Field;ort java.util.List;   // ❌ 一行混了两个 import
import org.jspecify.annotations.NonNull;              // ❌ 误导入第三方库的 NotNull
```

**对策**：每个 import 独立一行；用 IDE 自动 import 别误选第三方同名注解（自定义 `@NotNull` 不要导入 `org.jspecify.annotations.NonNull`）。

---

## 三、今日代码产出

### `AnnotationsLab.java`

| 元素 | 类别 | 演示点 |
|------|------|--------|
| `@FunctionalInterface interface Greeter` | 内置注解 | 函数式接口 + lambda 简写 |
| `@Deprecated oldMethod()` | 内置注解 | 标记过时 |
| `@SuppressWarnings("deprecation") useOld()` | 内置注解 | 抑制过时警告 |
| `@Override oldMethod()` (MyWorker) | 内置注解 | 检查重写 |
| `@interface Label` | 自定义注解 | 单属性 + `@Target(FIELD)` + `@Retention(RUNTIME)` |
| `User` 类 | 使用注解 | 字段贴 `@Label` |
| `readLabels(Class<?>)` | 反射读注解 | `getDeclaredFields` + `getAnnotation` + `value()` |

### `ValidatorLab.java`

| 元素 | 类别 | 演示点 |
|------|------|--------|
| `@NotNull` | 自定义注解 | 无属性，仅标记 |
| `@Length(min, max, message default)` | 自定义注解 | 多属性 + 默认值 |
| `@Range(min, max, message default)` | 自定义注解 | 多属性 + 默认值 |
| `User` 类 | 使用注解 | 三字段各贴不同规则 |
| `validate(Object)` | 反射校验 | `getDeclaredFields` + 破防 + 多注解检查 |

### 测试覆盖

| 测试文件 | 用例数 | 覆盖点 |
|---------|--------|--------|
| `AnnotationsLabTest` | 6 | 4 注解 key / FunctionalInterface 反射可读 / Deprecated 反射可读 / useOld 调用过时方法 / Override 多态生效 / lambda 简写 |
| `AnnotationsLabLabelTest` | 4 | `@Retention(RUNTIME)` 反射读 / `@Target(FIELD)` 反射读 / readLabels 返回值正确 / 字段没贴 @Label 时空 Map |
| `ValidatorLabTest` | 10 | 全合规 / name=null / 长度过短过长 / 长度边界 / 年龄过大过小 / 边界值合规 / 多字段违规 / null 值不 NPE |

---

## 四、核心知识图谱

```
注解（Annotation）
├── 内置注解（4 个）
│   ├── @Override            [SOURCE, 编译器读]
│   ├── @SuppressWarnings    [SOURCE, 编译器读]
│   ├── @Deprecated          [RUNTIME, 反射可读]
│   └── @FunctionalInterface [RUNTIME, 反射可读]
│
├── 元注解（贴在注解定义上）
│   ├── @Target(ElementType.XXX)    能贴在哪
│   └── @Retention(RetentionPolicy.XXX)  保留多久
│       ├── SOURCE   编译后丢弃（反射读不到）
│       ├── CLASS    class 文件中，JVM 不加载（默认）
│       └── RUNTIME  运行期保留（反射可读）
│
├── 自定义注解
│   ├── @interface 声明
│   ├── 属性（编译期常量：基本类型/String/Class/枚举/注解/数组）
│   ├── 默认值 default
│   └── value 特例（单属性可省略名字）
│
└── 反射读注解三剑客
    ├── isAnnotationPresent(Xxx.class)
    ├── getAnnotation(Xxx.class)   返回 null 要判空
    └── getAnnotations()
```

**核心串联**：注解是"贴标签"，反射是"读标签"，两者组合是 Spring / JUnit / JSON 库等框架的核心机制。

---

## 五、明日计划

### 第十课候选方向（三选一）

1. **泛型进阶**（PECS / 类型擦除 / 泛型方法）-- `generics/` 包目前空着
2. **枚举 + 注解进阶玩法**（枚举实现接口、注解 + 枚举做策略模式）
3. **现代特性速览**（Records / Sealed Classes / Pattern Matching）

### 复习要点

- 注解 = 便利贴，自身不执行逻辑，靠读取方起作用
- `@Retention(RUNTIME)` 是反射能读注解的前提
- `@Target` 限定能贴的元素类型
- 反射读注解三剑客 + 判空习惯
- `getDeclaredFields` 不是 `getFields`（第八课反射的延续）

### 待补强基础库

- [ ] `java.lang.annotation` 包：`Annotation` 接口、`AnnotationFormatError`
- [ ] `AnnotatedElement` 接口（`Class` / `Method` / `Field` 都实现它，是反射读注解的统一入口）
- [ ] JDK 8+ 重复注解 `@Repeatable`
- [ ] JDK 8+ 类型注解（`TYPE_USE`，可贴在泛型参数上如 `List<@NonNull String>`）
