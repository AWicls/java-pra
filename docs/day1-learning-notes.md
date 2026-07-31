# Java 学习笔记 - Day 1（2026-07-31）

> 学习项目：java-pra（JDK 25 + Gradle 9.6.1 + JUnit Jupiter 6.0.1）
> 进度：第一课（集合）→ 第二课（泛型）→ 第三课（Stream）→ 第四课（现代特性）

---

## 一、四课知识点速览

### 第一课：集合框架

| 主题 | 要点 |
|------|------|
| 三大族 | `Collection`（List/Set/Queue/Deque）+ `Map`（独立根，不继承 Collection）|
| List 实现选型 | `ArrayList`（随机访问 O(1)）/ `LinkedList`（头尾插入 O(1)）|
| Set 三种 | `HashSet`（无序 O(1)）/ `LinkedHashSet`（保插入序）/ `TreeSet`（自动排序 O(log n)）|
| Map 同理 | `HashMap` / `LinkedHashMap` / `TreeMap`，并发用 `ConcurrentHashMap`（勿用 Hashtable）|
| 不可变集合 | `List.of()` / `Set.of()` / `Map.of()` / `List.copyOf()`（JDK 10+）|
| 迭代器安全删除 | `Iterator.remove()` 或 `removeIf()`，for-each 中直接 `remove` 会 fail-fast |
| 频率统计惯用法 | `map.getOrDefault(k, 0) + 1` |

### 第二课：泛型

| 主题 | 要点 |
|------|------|
| 类型参数命名 | `T`/`E`/`K`/`V`/`R`/`S` |
| 三种用法 | 泛型方法 `<T>` / 泛型类 `Stack<T>` / 泛型接口 |
| 上界 | `<T extends Number>` / 多边界 `<T extends Number & Comparable<T>>` |
| PECS | Producer `? extends T`（只读）/ Consumer `? super T`（只写）|
| 类型擦除 | 运行期 `List<String>` 和 `List<Integer>` 是同一个 class（`getClass()` 相等）|
| 对象比较 | 泛型不能用 `>`，必须 `T extends Comparable<T>` + `t.compareTo(max)` |

### 第三课：函数式与 Stream

| 主题 | 要点 |
|------|------|
| Lambda | `(参数) -> {方法体}`，单参省括号、单行省 return 和 {} |
| 函数式接口 | `Function<T,R>` / `Predicate<T>` / `Consumer<T>` / `Supplier<T>`（都只有一个抽象方法）|
| 方法引用 | `Math::abs`（静态）/ `String::length`（实例）/ `ArrayList::new`（构造）|
| Stream 三段 | 数据源 → 中间操作（filter/map/sorted/distinct，懒执行）→ 终端操作（collect/sum/forEach）|
| 不可变收集 | `.toList()`（JDK 16+，不可变）vs `.collect(Collectors.toList())`（可变）|
| 分组统计 | `groupingBy(classifier)` → `Map<K, List<V>>`；`groupingBy(classifier, counting())` → `Map<K, Long>` |
| Optional | `findFirst()` / `max()` 返回 Optional，用 `orElse` / `orElseThrow` / `ifPresent` 处理 |

### 第四课：现代特性

| 主题 | 要点 |
|------|------|
| Record（JDK 16）| `record Point(int x, int y) {}` 自动生成构造/访问器/equals/hashCode/toString |
| 紧凑构造器 | `public Age { if (value<0) throw ...; }` 无参数列表，用于校验 |
| 访问器命名 | `point.x()` 不是 `getX()` |
| Sealed（JDK 17）| `sealed interface Shape permits Circle, Rectangle` 限定子类白名单 |
| 子类三选一 | 必须是 `final` / `sealed` / `non-sealed` 之一 |
| instanceof 模式（JDK 16）| `if (obj instanceof String s)` 一步判断+绑定 |
| switch 类型模式（JDK 21）| `case Integer i -> ...`，支持 `case null`，不需要强转 |
| Sealed + switch | 编译器知道所有子类，可不写 default（穷尽性检查）|

---

## 二、真实遇到的坑（非理解错误，是实际编程陷阱）

### 第一课真实坑

#### 坑 1.1：`List.reversed()` 返回值丢失
```java
// ❌ 错误：reversed() 返回新视图，不改原 List
List<Integer> reversed = new ArrayList<>(source);
reversed.reversed();        // 返回值丢了
return reversed;            // 返回的是原顺序

// ✅ 正确
return reversed.reversed();  // 或 Collections.reverse(reversed);
```
**根因**：`List.reversed()`（JDK 21+）返回一个**新的不可修改视图**，不修改原 List。

#### 坑 1.2：方法自调用导致 `StackOverflowError`
```java
public static int max(List<Integer> source) {
    int max = max(source);   // ← 自己调自己，无限递归
    return max;
}
```
**根因**：方法名相同，没意识到是递归调用。任何输入都会 StackOverflow。

#### 坑 1.3：`assertThrows` 不能先调用再断言
```java
// ❌ 错误：第一行就直接抛异常，走不到 assertThrows
int result = ListLab.max(List.of());
assertThrows(NoSuchElementException.class, result);   // 还编译错误

// ✅ 正确：用 lambda 包裹，让 assertThrows 执行它
assertThrows(NoSuchElementException.class, () -> ListLab.max(List.of()));
```
**根因**：`assertThrows(Class, Executable)` 第二参数是函数式接口，不是变量。被测代码必须延迟到 lambda 内执行。

#### 坑 1.4：用 `List.of()` 验证"不修改原 List"无效
```java
// ❌ List.of() 不可变，reverseCopy 没机会修改它，验证无意义
List<Integer> input = List.of(1,3,2);
ListLab.reverseCopy(input);
assertIterableEquals(List.of(1,3,2), input);   // 没改不是因为方法对

// ✅ 用 ArrayList 包一层，让"原 List 没变"成为真实证明
List<Integer> input = new ArrayList<>(List.of(1,3,2));
```
**根因**：`List.of()` 不可变，调任何修改方法都抛 `UnsupportedOperationException`，无法验证副作用。

### 第二课真实坑

#### 坑 2.1：泛型对象不能用 `>` 比较
```java
// ❌ 编译错误：> 只能用于原始类型
if (t > max) { ... }

// ✅ 用 compareTo
if (t.compareTo(max) > 0) { ... }
```
**根因**：泛型擦除后 `T` 变成 `Object`（或边界类型），没有 `>` 运算符。必须 `T extends Comparable<T>` 保证运行期能调 `compareTo`。

#### 坑 2.2：空 `List.of()` 推断成 `List<Object>` 不满足 Comparable 约束
```java
// ❌ 编译错误：List.of() 推断成 List<Object>，Object 没实现 Comparable
assertThrows(NoSuchElementException.class, () -> GenericsLab.max(List.of()));

// ✅ 显式声明类型
List<Integer> empty = List.of();
GenericsLab.max(empty);
```
**根因**：`List.of()` 无元素时无法从元素推断类型，编译器选最宽泛的 `Object`，不满足 `T extends Comparable<T>` 上界。

### 第三课真实坑

#### 坑 3.1：`Collectors.toList()` 返回可变 List，与 `.toList()` 混淆
```java
// ❌ 返回可变 ArrayList，任务要求不可变
.collect(Collectors.toList())

// ✅ JDK 16+，返回不可变 List
.toList()
```
**根因**：两个 API 语义不同，容易混。`Collectors.toList()` 是 JDK 8 老接口（可变），`.toList()` 是 JDK 16+ 新方法（不可变）。

#### 坑 3.2：`Collectors.groupingBy()` 必须传参数
```java
// ❌ 编译错误：groupingBy() 不能无参
.collect(Collectors.groupingBy())

// ✅ 至少传分类函数
.collect(Collectors.groupingBy(s -> s.length()))
```

### 第四课真实坑

#### 坑 4.1：`case null -> throw` 语法错误
```java
// ❌ throw 是语句，不能直接用 ->
case null -> throw new NoSuchElementException();

// ✅ 用花括号包，或干脆不处理 null
case null -> { throw new NoSuchElementException(); }
```
**根因**：switch 的 `->` 后要跟表达式或块，`throw` 是语句不是表达式。

#### 坑 4.2：`throw new X()` 漏 `new`
```java
throw NoSuchElementException();     // ❌
throw new NoSuchElementException(); // ✅
```

#### 坑 4.3：`case Circle r` 中 `r` 是对象不是字段
```java
// ❌ r 是 Circle 对象，不是 radius
case Circle r -> Math.PI * r * r;

// ✅ r 是对象，调访问器取字段
case Circle c -> Math.PI * c.radius() * c.radius();
```
**根因**：`case Circle r` 的 `r` 绑定的是**整个对象**，要访问字段必须调访问器（Record 自动生成的 `radius()`）。

#### 坑 4.4：Record 解构语法不稳定
```java
// ❌ (a, b) 不是合法语法（JDK 21+ 预览，不稳定）
case Rectangle (a, b) -> a * b;

// ✅ 用稳定的类型模式 + 访问器
case Rectangle r -> r.width() * r.height();
```

#### 坑 4.5：switch 表达式末尾分号
```java
// ❌ return 语句未结束
return switch (shape) { ... }

// ✅ switch 是表达式，作为 return 值要分号结尾
return switch (shape) { ... };
```

### 通用真实坑

#### 坑 0.1：`java.*` 包名被禁止
JDK 9+ 模块系统严格校验，`java.pra` 会抛 `Prohibited package name`。已迁移到 `learning.pra`。

#### 坑 0.2：沙箱环境下 `./gradlew` 失败
`~/.gradle` 只读，wrapper 无法下载发行版。改用：
```bash
gradle test --no-daemon --gradle-user-home="$TMPDIR/gradle-home"
```

#### 坑 0.3：Gradle 模板的 `mainClass` 与实际包名不一致
模板默认 `org.example.App`，实际包名不同会导致 `./gradlew run` 失败。需同步 `build.gradle.kts`。

---

## 三、待补强的基础库清单

学习中发现对以下标准库 API 不熟悉，需专项补强：

### Stream/Collector 相关
- [ ] `Collectors` 全套：`toList` / `toSet` / `toMap` / `joining` / `counting` / `groupingBy` / `partitioningBy` / `reducing`
- [ ] `Collectors.toUnmodifiableList()` / `toUnmodifiableMap()`（JDK 10+）
- [ ] `Stream` 终端操作全集：`reduce` / `collect` / `toArray` / `min` / `max` / `anyMatch` / `allMatch` / `noneMatch`
- [ ] `IntStream` / `LongStream` / `DoubleStream` 原始类型流
- [ ] `flatMap` 展平嵌套结构

### 集合工具
- [ ] `Collections` 工具类：`max` / `min` / `sort` / `reverse` / `shuffle` / `frequency` / `synchronizedList`
- [ ] `Arrays` 工具类：`asList` / `sort` / `binarySearch` / `copyOf` / `fill`
- [ ] `List.copyOf` / `Map.copyOf` / `Set.copyOf`（JDK 10+）
- [ ] `SequencedCollection`（JDK 21+）：`getFirst` / `getLast` / `addFirst` / `addLast` / `reversed`

### 字符串
- [ ] `String` 全套方法：`format` / `join` / `repeat` / `strip` / `indent` / `transform`
- [ ] `StringBuilder` vs `StringBuffer`
- [ ] `StringJoiner`

### 函数式接口
- [ ] `Function` / `BiFunction` / `UnaryOperator` / `BinaryOperator`
- [ ] `Predicate` / `BiPredicate`
- [ ] `Consumer` / `BiConsumer`
- [ ] `Supplier`
- [ ] `Optional` 全套：`map` / `flatMap` / `filter` / `ifPresentOrElse` / `or`

### 异常体系
- [ ] `Exception` vs `Error` vs `RuntimeException`
- [ ] 受检异常 vs 非受检异常
- [ ] `try-with-resources`
- [ ] 自定义异常设计

### 并发（第五课待学）
- [ ] `Thread` / `Runnable`
- [ ] `synchronized` 三种用法
- [ ] `volatile` 可见性
- [ ] `AtomicInteger` / `AtomicReference`
- [ ] `wait` / `notify` / `notifyAll`

---

## 四、今日学习心得

### 做得好的
1. **测试驱动**：TDD 红绿循环确实能快速暴露 bug（第一课 reverseCopy、第三课 filter 漏写都是测试发现的）
2. **概念分层**：把抽象概念（PECS / Sealed）拆成对比表格 + 代码示例，理解更扎实
3. **不跳过地基**：即使想学进阶，地基（集合+泛型）打牢后再推进，避免后期返工

### 需改进的
1. **基础库不熟**：很多标准库 API（如 `Collectors` 全套、`String` 全套方法）要专项补强，不能只用时再查
2. **细节易错**：`return ... ;` 漏分号、`throw new` 漏 new 这类语法细节需通过 IDE 提示养成习惯
3. **测试用例覆盖**：倾向写完代码直接跑，少考虑边界（空列表、null、单元素），应养成"先想边界再写测试"的习惯

### 明天计划
- 第五课：并发与多线程（Thread / synchronized / Atomic / volatile）
- 补强 `Collectors` 和 `Collections` 工具类的常用方法
