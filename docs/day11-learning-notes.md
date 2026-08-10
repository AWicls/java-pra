# Java 学习笔记 - Day 11（2026-08-10）

> 学习项目：java-pra（JDK 25 + Gradle 9.6.1 + JUnit Jupiter 6.0.1）
> 进度：第十四课（Stream 进阶）-- 五个概念点 + 收尾待实战
> 代码：[app/src/main/java/learning/pra/stream/StreamAdvLab.java](app/src/main/java/learning/pra/stream/StreamAdvLab.java)
> 测试：[app/src/test/java/learning/pra/stream/StreamAdvLabTest.java](app/src/test/java/learning/pra/stream/StreamAdvLabTest.java)
> 总计：23 个测试全绿（StreamAdvLabTest）

---

## 一、第十四课：Stream 进阶

### 14.1 groupingBy / partitioningBy（分组分区）

`groupingBy` 按分类键把元素归堆，装进 `Map<键, List<元素>>`；`partitioningBy` 固定按 `Boolean` 分成两桶。

| 收集器 | 键类型 | 键数量 | 典型用途 |
|--------|--------|--------|---------|
| `groupingBy(f)` | 任意 | 多个 | 按区域/类别分堆 |
| `partitioningBy(pred)` | `Boolean` | 恰好 2 | 按是否满足条件二分 |

**下游收集器**：`groupingBy` 第二参数可再收集：
```java
Collectors.groupingBy(p -> p.region(), Collectors.counting())    // 每组计数
Collectors.groupingBy(p -> p.gender(), Collectors.maxBy(cmp))    // 每组最大
Collectors.groupingBy(p -> p.region(), Collectors.summingInt(...)) // 每组求和
```

**踩坑（真实）**：`partitioningBy` vs `groupingBy` 关键差异——
- `partitioningBy` **恒建 `true`/`false` 两个 key**（空桶也有空 List）
- `groupingBy` 只在**存在该分组元素**时才建 key
- 所以当某桶为空时，`groupingBy` 产出的 Map **缺那个 key**，`result.get(true)` 返回 `null` → NPE
- 边界条件：`>= threshold` 才是"达标"，`> threshold` 会把恰好等于 threshold 的排除

### 14.2 flatMap（拍平嵌套）

`map` 1 元素→1 元素（维度不变）；`flatMap` 1 元素→多个元素（维度摊平）。

```java
// 每个箱子的标签摊平成一个 List
boxes.stream().flatMap(b -> b.tags().stream()).toList();
// 去重
boxes.stream().flatMap(b -> b.tags().stream()).distinct().toList();
```

**关键**：`flatMap` 参数必须返回 **Stream**（或可拍平的东西）；返回普通值 → 那是 `map` 的活。

### 14.3 joining / mapping / collectingAndThen（收尾类收集器）

| 收集器 | 作用 | 常配 |
|--------|------|------|
| `joining(sep)` | 字符串流拼成一个字符串 | 单独用 |
| `mapping(f, down)` | 分组后先转换再收集 | `groupingBy` 下游 |
| `collectingAndThen(down, f)` | 收集完再变换一次 | `groupingBy` 下游 |

```java
Collectors.joining(", ")                                  // 拼接
Collectors.mapping(Sale::product, Collectors.toList())    // 每组转字段再收集
Collectors.collectingAndThen(Collectors.summingInt(...), Integer::intValue) // 收集后转类型
```

**踩坑（真实）**：`collectingAndThen(down, finisher)` 的 finisher 必须跟 down 产出类型匹配：
- `summingInt` 产 `Integer` → 配 `Integer::intValue`
- `summingLong` 产 `Long` → 配 `Long::intValue`
- 配错类型编译报错（`Long::intValue` 配 `summingInt` 编译失败）

**踩坑**：`mapping` 要映射对字段——`mapping(Sale::product, ...)` 才是商品名，`Sale::region` 是区域名（容易抄错）。

### 14.4 reduce（归约）+ parallelStream

`reduce` 把流元素**折叠成一个值**，靠"累计值"：
```java
nums.stream().reduce(0, (acc, n) -> acc + n)   // 有初始值
nums.stream().reduce(Integer::sum)              // 无初始值 → Optional
nums.parallelStream().reduce(0, Integer::sum, Integer::sum) // 并行：第三参数合并多段
```

**关键**：`reduce` 要求操作满足**结合律**（`(a+b)+c==a+(b+c)`），并行流必须满足否则多段合并错。`reduce` 返回单个值，`collect` 返回可变容器。

**parallelStream 陷阱**：
| 陷阱 | 说明 |
|------|------|
| 有状态/共享可变变量 | lambda 改外部共享变量 → 数据竞争 |
| 顺序不保证 | 要保序用 `forEachOrdered` |
| 数据量小更慢 | 拆分子任务有开销 |
| 阻塞/IO | 占公共 ForkJoinPool 拖垮 JVM |
| reduce 必须结合律 | 否则并行合并错 |

**一句话**：并行流适合"大集合 + 纯计算 + 无共享状态"。

### 14.5 自定义 Collector（造轮子）

`Collector<T, A, R>`：T=流元素，A=中间累加器，R=最终结果。4 个要素都是**函数**：

| 要素 | 类型 | 作用 |
|------|------|------|
| `supplier` | `Supplier<A>` | 造空累加器 |
| `accumulator` | `BiConsumer<A,T>` | 逐个装元素 |
| `combiner` | `BinaryOperator<A>` | 并行合并两个累加器 |
| `finisher` | `Function<A,R>` | 收尾转成结果（可省） |

```java
public static Collector<Integer, ?, Double> averageCollector() {
    return Collector.of(
        () -> new int[] { 0, 0 },                       // supplier
        (int[] acc, Integer t) -> { acc[0]++; acc[1] += t; },  // accumulator
        (a, b) -> { a[0] += b[0]; a[1] += b[1]; return a; },   // combiner
        (acc) -> acc[0] == 0 ? 0.0 : (double) acc[1] / acc[0]  // finisher
    );
}
```

**踩坑（真实，卡了 2 轮）**：
- 4 个参数是**函数**，不是数据。supplier 传 `() -> new int[]{0,0}`（能造数组的 lambda），**不是** `int[] acc = {0,0}`（数组本身）
- 累加器 lambda 体多条语句用**块 `{}` + 分号**，不能用逗号 `,` 分隔
- combiner 要 `return a;`（合并结果返回）
- finisher 空数组防护：`acc[0]==0` 返回 `0.0`，否则 `(double) acc[1] / acc[0]`（强转避免整数除法）
- 四个参数都要写，不能传 `null`

---

## 二、本课踩坑速记（重点）

1. **`partitioningBy` 恒建两个 key，`groupingBy` 只在有元素时建 key** → 空桶时 `groupingBy` 缺 key，`get` 返回 null NPE。分桶用 `partitioningBy`。
2. **边界条件**：`>= threshold` vs `> threshold`，恰好等于 threshold 算不算达标要按需求定。
3. **`collectingAndThen` 的 finisher 类型必须跟 downstream 产出匹配**：`summingInt`→`Integer::intValue`，`summingLong`→`Long::intValue`。
4. **`mapping` 映射字段别抄错**：`Sale::product` 才是商品名。
5. **`Collector.of` 四参数都是函数**：supplier 是"能造容器的 lambda"，不是容器本身。
6. **`reduce` 并行必须满足结合律**；`reduce`（单值）vs `collect`（可变容器）区分。

---

## 三、StreamAdvLab 完整方法清单

| 成员 | 知识点 | 测试覆盖 |
|------|------|------|
| `countByRegion` | groupingBy + counting | ✅ |
| `sumByProduct` | groupingBy + summingInt | ✅ |
| `partitionByAmount` | 分桶 + 边界 | ✅ |
| `flattenTags` / `distinctTags` | flatMap 拍平/去重 | ✅ |
| `joinProducts` | distinct + joining | ✅ |
| `productsByRegion` | groupingBy + mapping | ✅ |
| `totalAmountBuRegion` | groupingBy + collectingAndThen | ✅ |
| `parallelSum` / `serialSum` | reduce 串/并行 | ✅ |
| `parallelSortedDesc` | parallelStream + sorted | ✅ |
| `averageCollector` | 自定义 Collector | ✅ |

---

## 四、下一步

- 第十四课最后环节：**实战——销售数据分析**（综合运用五节收集器，未做）
- 完成后可进入**第十五课：NIO 与现代 IO**（Path/Files/Buffer/Channel）
