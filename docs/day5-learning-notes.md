# Java 学习笔记 - Day 5（2026-08-04）

> 学习项目：java-pra（JDK 25 + Gradle 9.6.1 + JUnit Jupiter 6.0.1）
> 进度：第八课（反射）
> 代码：[app/src/main/java/learning/pra/reflection/ReflectionLab.java](app/src/main/java/learning/pra/reflection/ReflectionLab.java)
> 测试：[app/src/test/java/learning/pra/reflection/ReflectionLabTest.java](app/src/test/java/learning/pra/reflection/ReflectionLabTest.java)（24 个测试全绿）

---

## 一、第八课：反射（Reflection）

### 8.1 反射初识 -- 程序的"自我观察"能力

**核心概念**：反射 = 运行期动态探查/操作类的能力。普通方式编译期必须知道类型（`new Foo()`），反射方式运行期才现场打开盒子看里面有什么字段/方法、现场调用。

**典型场景**：框架（Spring 扫描 `@Component`、JSON 库反序列化、JUnit 找 `@Test` 方法）-- 框架代码写时根本不知道业务类长啥样，只能运行时反射探查。

**普通方式 vs 反射方式对比**：

| 维度 | 普通方式 `new Foo()` | 反射方式 |
|------|--------------------|----------|
| 何时知道类型 | 编译期 | 运行期 |
| 能否调用私有方法 | ❌ 编译报错 | ✅ `setAccessible(true)` |
| 能否动态加载类 | ❌ 必须硬编码 | ✅ `Class.forName("...")` |
| 性能 | 高（直接调用）| 低（要查元数据 + 安全检查）|
| 类型安全 | 编译器保证 | 运行时才暴露错误 |
| 谁用 | 业务代码 | 框架 / 工具库 |

**核心入口：`Class<?>` 对象** -- JVM 加载每个类时在方法区生成唯一的 `Class<?>`（"类的元数据说明书"）。

**获取 `Class<?>` 的三种方式**（结果同一个对象）：

```java
// 方式 1：类字面量（编译期已知类型，最安全推荐）
Class<String> c1 = String.class;

// 方式 2：实例的 getClass()（运行期才知道实际类型，常用于多态场景）
Class<?> c2 = obj.getClass();

// 方式 3：Class.forName 全限定名（动态加载，常用于框架/配置驱动）
Class<?> c3 = Class.forName("java.lang.String");  // 抛 ClassNotFoundException
```

**三者区别**：

| 方式 | 触发类加载? | 抛检查异常? | 何时用 |
|------|------------|-------------|--------|
| `Xxx.class` | 不触发（编译期常量）| 否 | 编译期已知类型 |
| `obj.getClass()` | 已加载（对象都创建了）| 否 | 多态下看实际运行类型 |
| `Class.forName("...")` | 会触发初始化（执行 static 块）| `ClassNotFoundException` | 配置/框架动态加载 |

**`getName()` vs `getTypeName()` 对比**（普通类相同，数组不同）：

| 方法 | 返回（对 `String`）| 返回（对 `String[]`）| JDK |
|------|------------------|---------------------|-----|
| `getName()` | `java.lang.String` | `[Ljava.lang.String;` | 1.0+ |
| `getTypeName()` | `java.lang.String` | `java.lang.String[]` | 12+ |

### 8.2 反射操作字段 -- 程序的"X 光机"

**三步流程**：获取 `Field` -> `setAccessible(true)` 破防 -> `get`/`set`。

```java
Field f = obj.getClass().getDeclaredField("name");
f.setAccessible(true);             // private 字段破防
Object value = f.get(obj);         // 读
f.set(obj, "新值");                // 写
```

**三个核心 API 对比**：

| 方法 | 作用 | 找不到时 | 能否看 private |
|------|------|---------|---------------|
| `getField(name)` | 找 public 字段（含继承）| `NoSuchFieldException` | ❌ |
| `getDeclaredField(name)` | 找本类任意权限字段（不含继承）| 同上 | ✅（要 `setAccessible(true)`）|
| `getDeclaredFields()` | 列出本类所有字段（不含继承）| 返回空数组 | ✅ |

**`get` vs `getDeclared*` 系列通用规律**（`Field`/`Method`/`Constructor` 完全一致）：

| 带 `Declared` | 不带 `Declared` |
|---------------|-----------------|
| 看本类（含 private，不含继承）| 看 public（含继承）|

**四种访问修饰符的反射可见性**：

| 修饰符 | `getField` | `getDeclaredField` + `setAccessible(true)` |
|--------|-----------|-------------------------------------------|
| `public` | ✅ | ✅ |
| `protected` | ❌ | ✅ |
| 默认（package-private）| ❌ | ✅ |
| `private` | ❌ | ✅ |

**基本类型专用方法**（避免装箱）：`getInt(obj)` / `setInt(obj, 5)` / `getDouble` / `getBoolean` 等。引用类型用通用 `get(obj)` / `set(obj, value)`。

### 8.3 反射调用方法 -- 程序的"遥控器"

**API 与 `Field` 对称**：`getDeclaredMethod(name, 参数类型...)` -> `setAccessible(true)` -> `invoke(obj, args)`。

**最大坑点：方法重载必须用参数类型区分**

```java
// ❌ getDeclaredMethod 只给名字，重载分不清
Method m = clazz.getDeclaredMethod("apply");

// ✅ 必须传参数类型 Class
Method m1 = clazz.getDeclaredMethod("apply", String.class);
Method m2 = clazz.getDeclaredMethod("apply", String.class, int.class);
```

**参数类型清单**（必须用 `Class`，基本类型用 `int.class` 不是 `Integer.class`）：

| 参数类型 | 传什么 |
|---------|--------|
| `String` | `String.class` |
| `int` | `int.class`（不是 `Integer.class`！） |
| `int[]` | `int[].class` |
| `String[]` | `String[].class` |

**invoke 三步流程**：

```java
Method m = clazz.getDeclaredMethod("methodName", String.class, int.class);
m.setAccessible(true);
Object result = m.invoke(obj, "hello", 3);   // obj 是实例，后面是实参
```

**invoke 的两个特殊场景**：

```java
// 静态方法：第一个参数传 null
Object result = m.invoke(null, 3, 5);

// 无参方法：直接 invoke(obj) 或传空数组
Object result = m.invoke("hello");
```

**异常陷阱：双包装**

```java
try {
    Object result = m.invoke(obj, args);
} catch (IllegalAccessException e) {
    // setAccessible(true) 后理论上不会发生
} catch (InvocationTargetException e) {
    // ⚠️ 被调用方法内部抛的异常被包成 InvocationTargetException
    Throwable realCause = e.getCause();   // 真正的异常在里面
    throw new RuntimeException(realCause);   // 框架常解包重抛
}
```

**三种异常对比**：

| 异常 | 何时抛 | 谁负责 |
|------|--------|--------|
| `NoSuchMethodException` | 方法名/参数类型对不上 | 调用方传错参 |
| `IllegalAccessException` | 权限不足（没用 setAccessible）| 调用方忘破防 |
| `InvocationTargetException` | 被调方法内部抛了异常 | 被调方法的 bug |

**参数类型推断简化方案**：从 `Object... args` 推断参数类型时，基本类型实参会装箱（`int` -> `Integer`），需要 `unwrap` 映射回 `int.class`。

```java
private static Class<?> unwrap(Class<?> type) {
    if (type == Integer.class) return int.class;
    if (type == Long.class) return long.class;
    if (type == Double.class) return double.class;
    if (type == Float.class) return float.class;
    if (type == Boolean.class) return boolean.class;
    if (type == Byte.class) return byte.class;
    if (type == Short.class) return short.class;
    if (type == Character.class) return char.class;
    return type;
}
```

### 8.4 反射创建对象 -- 程序的"3D 打印机"

**API 对称性**（三剑客完全对称）：

| 元数据 | 获取 | 操作 |
|--------|------|------|
| `Field` | `getDeclaredField(name)` | `get(obj)` / `set(obj, val)` |
| `Method` | `getDeclaredMethod(name, types)` | `invoke(obj, args)` |
| `Constructor` | `getDeclaredConstructor(types)` | `newInstance(args)` |

**三步流程**：

```java
Constructor<Foo> c = Foo.class.getDeclaredConstructor(String.class, int.class);
c.setAccessible(true);              // private 构造器破防
Foo obj = c.newInstance("hello", 3);
```

**与 `Method.invoke` 的关键差异**：

| 维度 | `Method.invoke` | `Constructor.newInstance` |
|------|-----------------|--------------------------|
| 第一参数 | 实例 `obj`（静态方法传 null）| 不要传（造的就是实例）|
| 返回 | 方法返回值 | 新造的对象 |
| 找元数据用名字 | 要（`methodName`）| 不要（构造器名 = 类名）|
| 异常 | `InvocationTargetException` | 同（被构造器内部抛的异常被包装）|

**私有构造器破防 -- 单例模式的"破解"**

```java
Constructor<Singleton> c = Singleton.class.getDeclaredConstructor();
c.setAccessible(true);
Singleton fakeInstance = c.newInstance();   // 造出第二个实例！单例被破
```

这是为什么单例模式要加 `readResolve` / 枚举单例（防反射攻击）的根因。

**`Class.newInstance()` 已废弃（JDK 9+）**：原因只调无参构造器且直接抛构造器内部异常（不包装），异常处理不一致。推荐 `clazz.getDeclaredConstructor().newInstance()`。

**`Constructor.newInstance` 独有 `InstantiationException`**：实例化抽象类/接口/数组类时抛（`Method.invoke` 没有，因为不需要造对象）。

---

## 二、真实遇到的坑（非理解错误，是实际编程陷阱）

### 坑 8.1：`ClassLoader.getName()` 对 JDK 核心类返回 null

```java
ClassLoader cl = String.class.getClassLoader();
cl.getName();   // ❌ NPE！cl 是 null

// ✅ 先判 cl 再调 getName
String name = cl != null ? cl.getName() : "bootstrap";
```
**根因**：JDK 核心类（String/ArrayList/Object）由引导类加载器加载，引导类加载器不是 Java 对象，返回 null。

### 坑 8.2：无名模块的 `Module` 对象非 null，但 `getName()` 返回 null

```java
Module mod = MyClass.class.getModule();   // 非 null
mod.getName();   // 返回 null（无名模块）

// ✅ 用 requireNonNullElse 处理返回值
String name = Objects.requireNonNullElse(mod.getName(), "unnamed");
```
**根因**：普通 classpath 应用里的自定义类属于无名模块，模块对象存在但 `getName()` 返回 null。对比 `java.base` 模块的 `String` 类，`getName()` 返回 `"java.base"`。

### 坑 8.3：`requireNonNullElse` 用错地方 -- 对象本身为 null 时失效

```java
// ❌ cl 为 null 时，cl.getName() 直接 NPE，走不到 requireNonNullElse
Objects.requireNonNullElse(cl.getName(), "bootstrap");

// ✅ 区分"对象为 null"vs"方法返回值为 null"
// 对象为 null：用三元
cl != null ? cl.getName() : "bootstrap";
// 方法返回值为 null：用 requireNonNullElse
Objects.requireNonNullElse(mod.getName(), "unnamed");
```
**根因**：`requireNonNullElse(x, default)` 只处理"x 表达式求值为 null"，若 x 是 `obj.method()` 且 obj 本身 null，先 NPE 走不到判空。

### 坑 8.4：JDK 9+ 模块系统强封装（InaccessibleObjectException）

```java
// ❌ 反射访问 JDK 核心类（java.util.AbstractList）的非 public 成员
Constructor<?> c = java.util.AbstractList.class.getDeclaredConstructor();
c.setAccessible(true);   // 抛 InaccessibleObjectException（不是 IllegalAccessException）

// ✅ 测试抽象类实例化时，用项目内自定义抽象类
static abstract class AbstractShape {}
```
**根因**：JDK 9+ 模块系统对外部模块反射访问 JDK 核心类的非 public 成员强封装拦截。`InaccessibleObjectException` 是新异常（不是 `IllegalAccessException`）。

### 坑 8.5：`InvocationTargetException` 包装被调方法内部异常

```java
// 被调方法内部抛 IllegalStateException，不会直接传出
m.invoke(obj, args);   // 抛 InvocationTargetException

// ✅ 要用 getCause() 解包
Throwable realCause = e.getCause();   // 拿到真正的 IllegalStateException
```
**根因**：反射设计要区分"反射机制本身出错" vs "被调方法内部出错"，后者统一包装。

### 坑 8.6：反射方法查找的简化方案限制（学习阶段接受）

当前 `invokeMethod` 实现的已知限制（暂未修复）：
- `getDeclaredMethod` 只看本类，不查继承 -> `obj.toString()` 找不到（在 Object 父类）
- `null` 参数 NPE（`args[i].getClass()` 触发）
- `unwrap(Integer.class)->int.class`，但方法签名可能是 `Integer.class`（包装类签名版本找不到）
- 健壮方案：`getDeclaredMethods()` 遍历 + `isAssignable` 匹配（用 `Class.isAssignableFrom` + 包装->基本映射）

### 坑 8.7：变量名 / 键名拼写错误（多次翻车）

```java
map.put("typeNmae", ...);   // ❌ typeNmae -> typeName
String methodeName = ...;    // ❌ methodeName -> methodName（多了 e）
```
**根因**：变量名拼写错误编译器不报错，运行时按正确键名取值取不到。要细心。

### 坑 8.8：import 行语法错误

```java
import java.lang.reflect.Constructor;
import java.lang.reflect.Constructor;java.lang.reflect.InvocationTargetException;  // ❌ 缺 import 关键字

// ✅ 每个 import 独立一行，不重复
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
```

---

## 三、今日代码产出

### `ReflectionLab.java` 方法清单（4 个方法 + 1 个辅助）

| 方法 | 类别 | 演示点 |
|------|------|--------|
| `classInfo(obj)` | Class 入门 | `getClass()` / `getSimpleName` / `getName` / `getClassLoader` / `getModule` |
| `readField(obj, name)` | Field 操作 | `getDeclaredField` + `setAccessible(true)` + `get(obj)` |
| `invokeMethod(obj, name, args)` | Method 操作 | 参数类型推断（unwrap）+ `getDeclaredMethod` + `invoke` |
| `newInstance(clazz, args)` | Constructor 操作 | `getDeclaredConstructor` + `newInstance` 造对象 |
| `unwrap(type)`（private）| 辅助 | 包装类 -> 基本类型映射 |

### `ReflectionLabTest.java` 测试覆盖（24 个测试）

| 分组 | 用例数 | 覆盖点 |
|------|--------|--------|
| `classInfo` | 6 | String/ArrayList/Object/自定义内部类/int 数组/null 入参 |
| `readField` | 5 | public/private/boolean 装箱/字段不存在/null obj |
| `invokeMethod` | 7 | 重载 int 参数/重载 String 参数/private/带参/无参/方法不存在/内部异常解包 |
| `newInstance` | 6 | 无参/public 带参/private/重载顺序区分/构造器不存在/抽象类 InstantiationException |

---

## 四、明日计划

- **第九课：注解（Annotation）**
  - 8.5 注解定义 + 元注解（`@Target` / `@Retention`）
  - 8.6 反射读注解（`isAnnotationPresent` / `getAnnotation`）
  - 8.7 实战：自定义 `@Label` 注解 + 反射扫描字段
- 复习：反射三剑客（Field/Method/Constructor）的 API 对称性
- 可选进阶：修复 `invokeMethod` 的三个已知限制（继承查找/null 参数/包装类签名匹配）
