# Java 学习笔记 - Day 10（2026-08-09）

> 学习项目：java-pra（JDK 25 + Gradle 9.6.1 + JUnit Jupiter 6.0.1）
> 进度：第十三课（泛型进阶实战）-- 全 4 题完成
> 代码：[app/src/main/java/learning/pra/generics/GenericPecsLab.java](app/src/main/java/learning/pra/generics/GenericPecsLab.java)
> 测试：[app/src/test/java/learning/pra/generics/GenericPecsLabTest.java](app/src/test/java/learning/pra/generics/GenericPecsLabTest.java)
> 总计：17 个测试全绿

---

## 一、第十三课：泛型进阶实战

### 13.1 PECS 实战 -- copyAll

**方法签名**：`<T> void copyAll(List<? extends T> src, List<? super T> dst)`

- `src` 只读（**Producer** → `? extends T`）：允许传入子类，读出的元素一定能赋给 `T`
- `dst` 只写（**Consumer** → `? super T`）：允许传入父类桶，`T` 一定能装进去
- 效果：一个方法处理多种类型组合

| 用法 | 编译 | 原因 |
|------|------|------|
| `copyAll(List<Dog>, List<Animal>)` | ✅ | Dog 可读；Animal 桶可装 Dog |
| `copyAll(List<Animal>, List<Dog>)` | ❌ | src 可能是 Cat，Dog 桶装不下 |
| `copyAll(List<String>, List<Object>)` | ✅ | 经典用例 |

**一句话**：`src` 放宽松到 `? extends`，`dst` 放宽松到 `? super`，复用方法处理更多组合。

### 13.2 类型擦除深入 -- newArray / createInstance

**核心事实**：泛型是编译期的。编译后 `T` 被擦成上界（无上界即 `Object`），**运行期没有 `T.class`**，`new T[length]` / `new T()` 都编译报错。

**workaround**：显式把 `Class<T>` 当参数传进去（Spring / Gson / JUnit 都这么干）。

| | `new T[]` / `new T()` | 反射 workaround |
|---|---|---|
| 编译期 | ❌ 被禁止 | ✅ 不碰 `T` |
| 类型信息从哪来 | 想从 `T` 拿，拿不到 | 从 `Class<T>` 参数拿 |
| 运行期造什么 | 不知道 | `Class<T>` 明确告诉它 |

```java
// 造数组：Array.newInstance 运行期照 Class<T> 造真正的 T[]
public static <T> T[] newArray(Class<T> componentType, int length) {
    return (T[]) Array.newInstance(componentType, length);  // (T[]) 是标准惯用法
}

// 造对象：构造器反射
public static <T> T createInstance(Class<T> type) {
    return type.getDeclaredConstructor().newInstance();
}
```

**为什么数组特殊**：数组的运行时类型是死的（`String[]` ≠ `Object[]`），`new T[]` 编译器直接拒绝；`List<T>` 内部用 `Object[]` + 强转"假装"，所以不报。

### 13.3 泛型方法边界 -- 递归边界 + 多上界

**递归类型边界 `<T extends Comparable<T>>`**：不只是"能比较"，而是"能和自己同类型比"。`String implements Comparable<String>`。
- 裸 `Comparable` → `compareTo(Object)` 要强转
- `Comparable<T>` → `compareTo(T)` 参数就是 `T`，**比较不用强转**，类型安全

**多上界 `<T extends A & B>`**：同时满足多个约束。
- 类上界只能一个（单继承），接口可多个，用 `&` 连接
- 类必须在 `&` 之前

```java
public static <T extends Comparable<T> & Named> String bestName(List<T> list)
// T 既能互相比，又有名字 → 方法里既能 compareTo 又能 getName
```

### 13.4 泛型类实战 -- Box<T> + Repository<T>（DAO 模式）

**泛型类把"存储逻辑"和"实体类型"解耦**：逻辑写一次，类型调用时定。

- `Box<T>`：单值类型安全容器，`get()`/`set(T)`
- `Repository<T>`：按自增 id 存取任意类型，`add`/`findById`/`remove`/`count`

```java
class Repository<T> {
    private Map<Integer, T> map = new HashMap<>();
    private int keyId = 0;

    public int add(T src) {
        ++keyId;              // 先自增再用：存的 key == 返回的 id
        map.put(keyId, src);
        return keyId;
    }
    public boolean remove(int id) {
        if (map.containsKey(id)) return map.remove(id, map.get(id));  // remove(key,value) 仅当映射为该值才删
        return false;
    }
}
```

---

## 二、本课踩坑速记（1 条，重点）

**`Repository.add` 自增顺序错位**（本课真实踩坑，自己修复）：
```java
// ❌ 错位：存在自增前的 key(0)，却返回自增后的 id(1) → findById(1) 拿到第二个元素，remove(1) 返回 false
map.put(keyId, src); keyId++; return keyId;

// ✅ 先自增再用：存的 key 和返回的 id 是同一个数
int id = ++keyId; map.put(id, src); return id;
```
**教训**：凡"分配 id 返回给调用方、再用 id 取回"的逻辑，务必保证**存的 key == 返回的 id**。这条通过 3 个失败测试暴露（findById 取错值 / NPE / remove false），比直接给答案印象深刻。

---

## 三、GenericPecsLab 完整方法/类清单

| 成员 | 知识点 | 测试覆盖 |
|------|------|------|
| `copyAll(src, dst)` | PECS（extends + super） | ✅ 子类→父类 / String→Object / 不修改源 / 追加 |
| `newArray(Class<T>, int)` | 类型擦除 + `Array.newInstance` | ✅ 元素类型是 T 而非 Object |
| `createInstance(Class<T>)` | 类型擦除 + 构造器反射 | ✅ instanceof + 强类型免强转 |
| `bestName(List<T>)` | 多上界 `Comparable<T> & Named` | ✅ 分数最高 / 平局首个 / 空抛异常 |
| `Box<T>` | 泛型包装盒 | ✅ 不同类型复用 / 未设值返回 null |
| `Repository<T>` | 泛型 DAO | ✅ 自增 id / findById / remove / count |

---

## 四、待补强基础库

- `Collections` 工具类（`unmodifiableXxx` vs `List.of`、`Collections.copy` 源码对照 PECS）
- `Collectors` 全套（`groupingBy` / `partitioningBy` / `collectingAndThen`）
- `Function`-`Predicate`-`Consumer`-`Supplier` 全套
- `String` 全套方法

---

## 五、下一步

- 第十四课：Stream 进阶（`groupingBy` / `flatMap` / `collectingAndThen` / 自定义 `Collector` / 并行流陷阱）
- 或第十五课：NIO 与现代 IO
- 或第十六课：现代特性（Records / Sealed / Pattern Matching）
