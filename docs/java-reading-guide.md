# Java 读码速查（词汇 + Javadoc + 报错）

> 目的：看注释、看 API 文档、看报错都不用反复复制翻译。**先背词汇通用层**，再按需深入。
> 用法：生词 → 查第一部分词汇表；API 看不懂 → 看第二部分读法；报错 → 看第三部分套路。
> 关联：[00-learning-roadmap.md](00-learning-roadmap.md) ｜ [day16-learning-notes.md](day16-learning-notes.md)

---

## 一、编程单词清单（分级速查）

### 1.1 通用核心（必修，先背这层）

**关键字（Java 保留字，无法当变量名）**

| 单词 | 中文 |
|------|------|
| class | 类 |
| interface | 接口 |
| enum | 枚举 |
| record | 记录（JDK 16+）|
| extends | 继承 |
| implements | 实现（接口）|
| new | 新建 |
| public / private / protected | 公开 / 私有 / 受保护 |
| static | 静态（属于类，不属于实例）|
| final | 不可变 / 不可继承 / 不可重写 |
| void | 无返回值 |
| return | 返回 |
| this / super | 本对象 / 父类 |
| if / else | 如果 / 否则 |
| switch / case | 分支 / 情形 |
| for / while / do | 循环 |
| break / continue | 跳出 / 继续 |
| try / catch / finally | 尝试 / 捕获 / 最终 |
| throw / throws | 抛（动作）/ 声明抛 |
| instanceof | 是…的实例 |
| package / import | 包 / 导入 |
| synchronized | 同步（加锁）|
| volatile | 易变的（可见性）|
| abstract | 抽象的 |
| default | 默认（接口默认方法）|
| boolean | 布尔 |

**数据类型**

| 单词 | 中文 | 单词 | 中文 |
|------|------|------|------|
| int | 整数 | long | 长整型 |
| double | 双精度浮点 | float | 单精度浮点 |
| char | 字符 | String | 字符串 |
| byte / short | 字节 / 短整 | boolean | 布尔 |
| Array | 数组 | List | 列表 |
| Set | 集合（去重）| Map | 映射（键值对）|

**常用动词（方法名高频，看懂这组就懂一半注释）**

| 单词 | 中文 | 单词 | 中文 |
|------|------|------|------|
| get | 取 | set | 设 |
| add | 增加 | remove | 移除 |
| put | 放入 | contains | 包含 |
| isEmpty | 是否为空 | size | 大小 / 个数 |
| length | 长度 | append | 追加 |
| insert | 插入 | split | 分割 |
| join | 拼接 | replace | 替换 |
| trim | 去首尾空格 | toUpperCase | 转大写 |
| toLowerCase | 转小写 | find / search | 查找 |
| sort | 排序 | reverse | 反转 |
| distinct | 去重 | count | 计数 |
| sum | 求和 | create / build | 创建 / 构建 |
| parse | 解析 | format | 格式化 |
| compareTo | 比较 | toString / equals / hashCode | 转字符串 / 相等 / 哈希 |

**高频术语（注释里最常出现）**

| 单词 | 中文 | 单词 | 中文 |
|------|------|------|------|
| method | 方法 | field | 字段 / 属性 |
| variable | 变量 | parameter | 参数 |
| return type | 返回类型 | instance | 实例 |
| object | 对象 | reference | 引用 |
| null | 空 | statement | 语句 |
| expression | 表达式 | loop | 循环 |
| condition | 条件 | compile | 编译 |
| run | 运行 | debug | 调试 |
| exception | 异常 | error | 错误 |
| warning | 警告 | thread | 线程 |
| process | 进程 | default | 默认值 |

### 1.2 面向对象 + 泛型（必修）

| 单词 | 中文 | 单词 | 中文 |
|------|------|------|------|
| encapsulation | 封装 | inheritance | 继承 |
| polymorphism | 多态 | abstraction | 抽象 |
| override | 重写（覆盖父类）| overload | 重载（同名不同参）|
| constructor | 构造器 | signature | 方法签名 |
| superclass / subclass | 父类 / 子类 | generic | 泛型 |
| type parameter | 类型参数 | wildcard | 通配符（?）|
| upper bound | 上界（extends）| lower bound | 下界（super）|
| type erasure | 类型擦除 | PECS | 生产者 extends / 消费者 super |

### 1.3 集合框架

| 单词 | 中文 | 单词 | 中文 |
|------|------|------|------|
| ArrayList | 动态数组 | LinkedList | 链表 |
| HashMap | 哈希映射 | TreeMap | 有序映射 |
| HashSet | 哈希集合 | Iterator | 迭代器 |
| fail-fast | 快速失败 | immutable | 不可变 |
| unmodifiable | 不可修改 | entry | 条目（键值对）|
| key / value | 键 / 值 | hash | 哈希 |
| collision | 冲突 | capacity | 容量 |

### 1.4 Stream / 函数式

| 单词 | 中文 | 单词 | 中文 |
|------|------|------|------|
| stream | 流 | pipeline | 管道（链式操作）|
| intermediate | 中间操作 | terminal | 终结操作 |
| collect | 收集 | map | 映射（转换）|
| filter | 过滤 | reduce | 归约（汇总）|
| flatMap | 拍平嵌套 | groupingBy | 分组 |
| partitioningBy | 分区 | joining | 拼接 |
| lambda | 匿名函数（->）| functional interface | 函数式接口 |
| Predicate | 谓词（返回布尔）| Consumer | 消费者（有参无返）|
| Supplier | 提供者（无参有返）| Function | 函数（有参有返）|
| Optional | 可选值（可能为空）| lazy | 惰性 / 懒加载 |

### 1.5 异常

| 单词 | 中文 | 单词 | 中文 |
|------|------|------|------|
| checked | 受检（编译期强制）| unchecked | 非受检（运行期）|
| runtime | 运行时 | throw | 抛出（动作）|
| throws | 声明可能抛 | cause | 原因 / 根因 |
| stack trace | 堆栈信息 | wrap | 包裹 |
| unwrap | 解包 | NullPointerException | 空指针异常 |
| IllegalArgumentException | 非法参数异常 | IllegalStateException | 非法状态异常 |
| IndexOutOfBounds | 下标越界 | ArrayIndexOutOfBounds | 数组越界 |

### 1.6 并发（较难，按需）

| 单词 | 中文 | 单词 | 中文 |
|------|------|------|------|
| thread-safe | 线程安全 | race condition | 竞态条件 |
| lock | 锁 | reentrant | 可重入 |
| deadlock | 死锁 | livelock | 活锁 |
| starvation | 饥饿（等不到）| mutex | 互斥锁 |
| condition | 条件变量 | await / signal | 等待 / 唤醒 |
| atomic | 原子（不可分割）| CAS | 比较并交换 |
| executor | 执行器 | thread pool | 线程池 |
| queue | 队列 | future | 异步结果 |
| callable | 可调用（有返回）| runnable | 可运行（无返回）|
| virtual thread | 虚拟线程 | platform thread | 平台线程 |
| pin | 钉住（阻塞让位失效）| semaphore | 信号量 |
| latch | 门闩（计数等待）| barrier | 屏障 |

### 1.7 IO / NIO

| 单词 | 中文 | 单词 | 中文 |
|------|------|------|------|
| input / output | 输入 / 输出 | stream | 流（字节流）|
| reader / writer | 读字符 / 写字符 | buffer | 缓冲 |
| channel | 通道 | path | 路径 |
| directory | 目录 | file | 文件 |
| encoding | 编码 | decode / encode | 解码 / 编码 |
| read / write | 读 / 写 | flush | 刷盘 |
| ByteBuffer | 字节缓冲 | flip | 翻转（读写切换）|
| position / limit | 位置 / 界限 | capacity | 容量 |

### 1.8 反射 / 注解（IoC 课核心）

| 单词 | 中文 | 单词 | 中文 |
|------|------|------|------|
| reflection | 反射 | field | 字段 |
| method | 方法 | constructor | 构造器 |
| invoke | 调用 | setAccessible | 设可访问（破私有）|
| getDeclared | 取声明的（含私有）| annotation | 注解 |
| target | 作用目标 | retention | 保留周期 |
| runtime | 运行期 | proxy | 代理 |
| dynamic proxy | 动态代理 | isAnnotationPresent | 是否标了某注解 |

### 1.9 现代特性（JDK 16+）

| 单词 | 中文 | 单词 | 中文 |
|------|------|------|------|
| record | 记录（不可变载体）| sealed | 密封（限定继承）|
| permits | 允许（列出子类）| pattern | 模式 |
| matching | 匹配 | switch expression | 开关表达式 |
| arrow（->）| 箭头分支 | text block | 文本块（多行字符串）|

### 1.10 本项目注释高频词（读笔记/代码时对照）

| 单词 | 中文 | 单词 | 中文 |
|------|------|------|------|
| lazy loading | 懒加载 | eager | 饿汉（提前创建）|
| register | 登记 | getBean | 取 Bean |
| inject | 注入 | dependency | 依赖 |
| singleton | 单例 | component | 组件 |
| container | 容器 | circular | 循环（依赖）|
| cache | 缓存 | creating | 创建中 |
| guard | 守卫（入口判断）| bean | 容器管理的对象 |

### 1.11 背词小技巧（减少打断）

1. **先猜词性**：`-er/-or` 结尾多是"做某事的人/东西"（consumer=消费者）；`-ing` 常是进行中动作（creating=创建中）；`-able` 是可被…的（accessible=可访问的）。
2. **只背"拦路词"**：注释里反复出现的生词记下来，一次背 5 个即可，别贪多。
3. **动词优先**：看懂 get/add/remove/put/contains 这组，一半方法名就懂了。
4. **用英文关键词搜**：卡壳时直接搜英文（如 "java lazy loading"），比先翻译再搜少一步。

---

## 二、读懂 Javadoc（API 文档）

### 2.1 Javadoc 长什么样（结构）

```java
/**
 * 第一句：这个方法/类是干嘛的（最重要，先读它）
 *
 * @param x      参数 x 是什么
 * @param y      参数 y 是什么
 * @return       返回值是什么
 * @throws IOException 什么情况会抛这个异常
 * @see AnotherClass 关联的其他类（顺藤摸瓜）
 * @since 17    从 JDK 17 开始才有这个方法
 * @deprecated  已废弃，别用了
 */
```

### 2.2 常用标签速查

| 标签 | 中文含义 | 备注 |
|------|---------|------|
| `@param` | 参数说明 | 每个参数一行 |
| `@return` | 返回值说明 | 无返回值就不写 |
| `@throws` | 抛出的异常 + 触发条件 | 看"什么情况抛"最重要 |
| `@see` | 参见（相关类/方法）| 阅读线索 |
| `@since` | 始于（JDK 版本）| **判断能否用**：版本太低别用 |
| `@deprecated` | 已废弃 | 有更优替代，别用 |
| `@Override` | 重写父类方法 | 注解不是 Javadoc，但常见 |
| `@SuppressWarnings("x")` | 压制某类警告 | 慎用 |
| `{@code xxx}` | 里面的 xxx 按代码样式显示 | 也避免 `<` 被当 HTML |
| `{@link Xxx}` | 链接到 Xxx | 可点跳转 |
| `<p>` | 换段 | 正文分段 |

### 2.3 读 Javadoc 三步法（省时间）

1. **只读第一句**——"这个方法干嘛的"，90% 的需求这句就够
2. **看 `@param` / `@return`**——我要传什么、能得到什么
3. **需要时才看 `@throws` / `@since` / `@see`**——踩坑才关心

> 反例（错误做法）：从头到尾把大段说明读完 → 浪费时间。先扫第一句和 @param/@return。

### 2.4 常见句式翻译

| 英文句式 | 中文意思 |
|---------|---------|
| Returns the number of ... | 返回……的个数 |
| Throws X if the ... is null | 当……为 null 时抛 X |
| This implementation ... | 这个实现……（默认实现说明）|
| If ... , this method does nothing | 如果……，本方法什么都不做 |
| Note that ... | 注意……（重要提示）|
| The returned list is unmodifiable | 返回的列表不可修改（改了会抛异常）|
| This method may block indefinitely | 本方法可能无限阻塞 |

---

## 三、读懂报错信息

### 3.1 报错三段式结构

```
IllegalStateException: 检测到循环依赖: learning.pra.ioc.ServiceA
	at MiniContainer.getBean(MiniContainer.java:31)
	at MiniContainer.injectDependencies(MiniContainer.java:64)
	at ServiceA ...(MiniContainerTest.java:99)
Caused by: ...
```

- **第一行** `异常类型: 消息` —— 这是什么类型的错（最重要）
- **栈帧** `at 类.方法(文件.java:行号)` —— 从上往下是调用链；**找第一个"自己写的类"** 就是出错源头
- **`Caused by:`** —— 更深层的根因（有就往下看）

### 3.2 本项目真实高频报错（含中文 + 修复方向）

| 报错 | 中文理解 | 通常原因 / 修复方向 |
|------|---------|---------------------|
| `Prohibited package name: java.pra` | 包名违规 | JDK 9+ 禁止 `java.*` 开头包名，改用 `learning.*` |
| `NullPointerException` | 空指针 | 调用了 null 对象的方法/字段 → 找哪个变量是 null |
| `IllegalArgumentException` | 非法参数 | 传入值不合法（如 getBean 未登记的类型）→ 看参数 |
| `IllegalStateException` | 非法状态 | 对象状态不对（如循环依赖、忘 unlock、await 没持锁）→ 看调用顺序 |
| `NoSuchMethodException` | 找不到方法 | 反射拿方法名/签名写错；`getDeclaredConstructor()` 括号里别传参 |
| `NoSuchElementException` | 没有元素 | 空集合取元素、Scanner 游标错位（hasNextInt 配 nextLine）|
| `IndexOutOfBoundsException` | 下标越界 | 访问了数组/List 不存在的位置 |
| `ClassCastException` | 类型转换失败 | 强转错了类型 → 检查泛型/实际类型 |
| `UnsupportedOperationException` | 不支持的操作 | 改了不可变集合（List.of）→ 用 ArrayList 或接收返回值 |
| `StackOverflowError` | 栈溢出 | 无限递归（如方法自调用）→ 检查递归出口 |
| `FileNotFoundException` | 文件不存在 | 路径写错 / 目录没建 |
| `IOException` | IO 错误 | 读写失败（磁盘/编码/句柄泄漏）|
| `InaccessibleObjectException` | 反射被拒 | JDK 模块强封装，别反射 JDK 内部类 |
| `ExecutionException` | 异步任务异常包装 | `Future.get()` 抛的 → 用 `getCause()` 解包看真实原因 |
| `CompletionException` | 同上（CF 版）| `CompletableFuture.join()` 抛的 → `getCause()` 解包 |
| `BufferUnderflowException` | Buffer 读超了 | ByteBuffer `getInt()` 前没 `flip()` 或剩余量不够 |
| 编译错：`未报告的异常...必须对其进行捕获或声明以便抛出` | 受检异常没处理 | 方法加 `throws Xxx` 或用 try-catch |
| 编译错：`无法将类 X中的构造器应用到给定类型` | 构造器签名不匹配 | 构造器参数写错 / 没有无参构造器 |

### 3.3 读报错三步法

1. **第一行类型** → 属于哪类问题（空指针/参数/状态/IO…）
2. **找第一个自己写的 `at` 帧** → 出错位置（JDK 内部的帧跳过）
3. **有 `Caused by:` 往下追** → 真正的根因

### 3.4 编译错 vs 运行错（先分清再动手）

| | 编译错误 | 运行错误（测试失败）|
|---|---|---|
| 什么时候出现 | `gradle` 构建时（BUILD FAILED）| 程序跑起来之后 |
| 关键词 | `错误:` / `Compilation failed` | 测试里 `FAILED` + 异常栈 |
| 本质 | 语法/类型不对，**编译器拦住** | 逻辑/状态不对，**运行时暴露** |
| 修法 | 看 `错误:` 行和行号，改类型/签名 | 看异常类型 + 第一个自己写的帧 |

---

## 四、日常提升水平的小习惯（每天 10-20 分钟）

1. **读报错先看类型**：异常类型本身就是答案的一半（NPE=有个 null、IllegalArgument=参数不对）。
2. **任何 API 先看 `@since`**：判断版本能不能用，避免"照抄了但编译错"。
3. **看 javadoc 只读第一句 + @param/@return**：快速建立"入参→出参"心智，别陷进长文。
4. **报错加参数看细节**：`gradle test --stacktrace --info`，能看到更完整调用链。
5. **每天随机读一个 JDK 类**：如 `java.util.List` / `java.util.Map` 的 javadoc，把生词记进词汇表。
6. **报错英文猜词性**：`-tion/-sion` 名词（exception）、`-able` 形容词（accessible）、动词开头的方法名（get/set/add）。
7. **"先猜再验证"**：看到生词/报错先猜意思，再查证——主动回忆比被动翻译记得牢 10 倍。

---

## 五、实战练法（可选）

下次遇到看不懂的报错或 Javadoc，**直接把报错信息 / API 名贴给 agent**：
- 报错 → 它带你指认类型、定位帧、找根因，并告诉你下次自己怎么看
- Javadoc → 它翻译成"一句话人话"，并标注哪些标签该重点看

练几次后就能自己独立读了。
