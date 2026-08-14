# Java 学习笔记 - Day 15（2026-08-14）

> 学习项目：java-pra（JDK 25 + Gradle 9.6.1 + JUnit Jupiter 6.0.1）
> 进度：第十七课（并发进阶）——本课全部 5 个概念点 + 4 个 Lab 完成 ✅
> 说明：本课是学习路线中最硬核的一课，本笔记为**详尽版**（含对话中实际踩的坑 + 补充的经典并发坑）

---

## 〇、本课总览

| 概念点 | 主题 | 对应 Lab | 测试数 |
|--------|------|---------|:---:|
| 1 | `ReentrantLock` vs `synchronized` | ReentrantLockLab（BankAccount） | 4 |
| 2 | `Condition`（替代 wait/notify） | ConditionLab（有界阻塞队列） | 4 |
| 3 | `CompletableFuture`（异步编排） | AsyncAggregateLab（并发聚合） | 2 |
| 4 | `ExecutorService` / 线程池 | ExecutorPoolLab（复用 + 取结果） | 2 |
| 5 | Virtual Threads（JDK 21+） | VirtualThreadLab（万级任务） | 1 |

**一条主线**：这五节课回答一个问题——"多线程到底怎么组织"。
`synchronized`/`Lock` 管"怎么锁"，`Condition` 管"怎么等"，`CompletableFuture` 管"怎么异步编排"，线程池管"怎么复用"，虚拟线程管"怎么开海量"。

---

## 一、概念点 1：`ReentrantLock` vs `synchronized`

### 本质
`ReentrantLock` = 手动版的 `synchronized`。名字 "Reentrant"（可重入）= 同一线程可重复获取同一把锁（有持有计数），跟 `synchronized` 的可重入一致。

| 维度 | `synchronized` | `ReentrantLock` |
|------|:---:|:---:|
| 加锁方式 | 自动（进块加，出块释放） | 手动 `lock()` / `unlock()` |
| 忘记释放 | 不可能 | **死锁**（必须 `finally`） |
| 等待可超时 | ❌ | ✅ `tryLock(timeout, unit)` |
| 等待可中断 | ❌ | ✅ `lockInterruptibly()` |
| 公平锁 | ❌ | ✅ `new ReentrantLock(true)` |
| 多条件变量 | 一个 wait set | 多个 `Condition` |
| 性能 | 竞争时 JVM 优化 | 手动，灵活但易错 |

### 最小代码
```java
private final ReentrantLock lock = new ReentrantLock();
public void increment() {
    lock.lock();
    try {
        count++;
    } finally {
        lock.unlock();   // 必须释放，忘了解锁 = 死锁
    }
}
```

### 超时拿锁（synchronized 做不到）
```java
if (lock.tryLock(2, TimeUnit.SECONDS)) {
    try { /* 临界区 */ } finally { lock.unlock(); }
} else {
    // 2 秒没拿到，走兜底，不阻塞干等
}
```

### 关键坑（重点）
1. **【实际踩坑】TOCTOU 竞态（锁外检查）**：把"余额检查"放在拿锁**之前**，检查-扣款被拆成两步，锁没保护整个过程 → 两线程同时通过检查、都扣款，余额变负。**检查必须发生在拿到锁之后**，让"检查→修改"整段在锁内原子执行。这是并发最经典的竞态之一。
2. **【经典坑】忘 `unlock` 死锁**：`lock()` 后**任何路径**都要释放。方法里有提前 `return` / 抛异常时最容易漏。稳妥写法：拿锁后立刻 `try { ... } finally { unlock(); }`。
3. **`tryLock()` 超时单位**：`tryLock(2, SECONDS)` 是 2 秒，别想当然写 `MILLISECONDS`（呼应 AsyncLab 的 `orTimeout` 单位坑）。
4. **`lock()` 不响应中断**：`lock()` 阻塞等锁时，别的线程 interrupt 它**没用**；要响应中断得用 `lockInterruptibly()`。
5. **公平锁有代价**：`fair=true` 让等待最久的线程先拿锁，但吞吐更低，默认用非公平。`tryLock()` 即使公平锁也**不排队**（直接插队尝试）。
6. **`synchronized` 什么时候够用**：不需要超时/中断/公平/多条件时，优先 `synchronized`（代码简单、JVM 优化好）。

---

## 二、概念点 2：`Condition`（替代 `wait/notify`）

### 本质
`Condition` = **可分的 `wait/notify`**。一个锁配多个等待队列，唤醒只影响目标队列，消灭"广播后大部分人是无效唤醒"。

| 维度 | `synchronized` + `wait/notify` | `ReentrantLock` + `Condition` |
|------|:---:|:---:|
| 等待集数量 | 1 个 | 多个（`lock.newCondition()`） |
| 等待 | `wait()` | `await()` |
| 唤醒单个 | `notify()` | `signal()` |
| 唤醒全部 | `notifyAll()` | `signalAll()` |
| 针对性 | ❌ 全对象广播 | ✅ 各 Condition 各管各的 |
| 超时等待 | 无 | `await(timeout, unit)` |

### 有界阻塞队列核心（今日实战）
```java
private final ReentrantLock lock = new ReentrantLock();
private final Condition notFull  = lock.newCondition();   // "不满"队列
private final Condition notEmpty = lock.newCondition();   // "不空"队列
private final Object[] buffer;
private int head = 0, tail = 0, count = 0;

public void put(E item) throws InterruptedException {
    lock.lock();
    try {
        while (count == buffer.length) { notFull.await(); }   // 满 → 等"不满"
        buffer[tail] = item;
        tail = (tail + 1) % buffer.length;                    // 环形推进
        count++;
        notEmpty.signal();                                     // 叫一个"不空"消费者
    } finally {
        lock.unlock();
    }
}

public E take() throws InterruptedException {
    lock.lock();
    try {
        while (count == 0) { notEmpty.await(); }              // 空 → 等"不空"
        E item = (E) buffer[head];
        head = (head + 1) % buffer.length;
        count--;
        notFull.signal();                                      // 叫一个"不满"生产者
        return item;
    } finally {
        lock.unlock();
    }
}
```

### 环形缓冲三要素（今日重点理解）
- `buffer` 是**定长格子**；`head` = 下一个要取的格子，`tail` = 下一个要放的格子
- **环形**：到末尾用 `(i + 1) % buffer.length` 绕回 0（格子循环复用）
- **满/空判断用 `count`**（元素计数）：`count == buffer.length` 满、`count == 0` 空。**不能靠 `head == tail` 判断**——空和满时指针都可能撞上，分不清

### 关键坑（重点）
1. **【实际踩坑】环形取模基数写错**：`tail = (tail + 1) % count` 是错的，`count` 是元素数不是格子总数。capacity=3、count=2 时 `(2+1)%2=1` 会覆盖已有元素。**必须 `% buffer.length`（容量）**。
2. **【经典坑】条件检查必须 `while` 不能 `if`**：两个原因——(a) **虚假唤醒**：`await` 可能被系统无故唤醒（Javadoc 明确建议 while）；(b) **多线程竞争**：被 `signal` 唤醒的瞬间，条件可能已被别的线程改掉。醒后必须**重新检查**。
3. **【经典坑】`signal` vs `signalAll`**：单条件多等待者时，`signal` 只唤醒一个，可能唤醒错对象导致**活锁/饿死**。有界队列用**两个条件**正是为了"生产者只叫消费者、消费者只叫生产者"，各唤醒对的。
4. **`await` 内部自动释放锁**：`await` 会释放当前持有的锁、挂起、被唤醒后**重新抢到锁**才继续（所以 `await` 返回后 `count` 读起来还是安全的）。这也是为什么 `await` 必须在持锁时调用，否则抛 `IllegalMonitorStateException`。
5. **`signal` 只是"解除阻塞"**：唤醒后线程要**重新抢锁**才能继续执行（和 `notify` 一样）。

---

## 三、概念点 3：`CompletableFuture`（异步编排）

### 快速回顾（第五课已学）
| API | 作用 |
|------|------|
| `supplyAsync(fn)` | 异步跑一个有返回值的任务 |
| `thenApply(fn)` | 上一个结果**同步**转换（套 1 层） |
| `thenCompose(fn)` | 上一个结果再起异步任务（CF 的 flatMap，保持 1 层） |
| `allOf(...)` | 多个都完成（无结果，自己遍历 join 取值） |
| `exceptionally(fn)` | 异常时给兜底值 |
| `join()` / `get()` | 阻塞拿结果 |

### 今日新增：`thenCombine`（并行合并）
```java
CompletableFuture<Integer> price = CompletableFuture.supplyAsync(() -> 100);
CompletableFuture<Integer> count = CompletableFuture.supplyAsync(() -> 200);
CompletableFuture<String> total = price.thenCombine(count, (a, b) -> "合计" + (a + b));
total.join();   // "合计300"
```
- 三个参数：另一个 CF、合并 lambda（收两个结果）
- 让**互不依赖**的任务并行跑、最后汇总 = "并发聚合"核心

### 三路合并要链两次
`thenCombine` 一次只收两个 CF，三路 = 先合两路得到一个 CF，再跟第三路合：
```java
CompletableFuture<String> up = userF.thenCombine(productF, (u, p) -> u + "|" + p);
CompletableFuture<String> full = up.thenCombine(logisticsF, (up2, l) -> up2 + "|" + l);
```

### 关键坑（重点）
1. **【实际踩坑】合并 lambda 漏分隔符**：`(a, b) -> a + b` 拼出"用户1商品2"，测试期望"用户1|商品2"。**合并 lambda 只负责把两个结果变成你要的东西，分隔符得自己写**。
2. **【实际踩坑】耗时打印重复减 `start`**：`elapsed` 已是 `now - start`，再 `elapsed - start` 就成负数。
3. **【经典坑】`supplyAsync` 默认线程池是 `ForkJoinPool.commonPool()`**：IO 密集任务会**阻塞公共池**（所有人都受影响）。生产应显式传线程池：`supplyAsync(fn, myPool)`（衔接概念点 4）。
4. **【经典坑】异常传播**：`thenCombine` 里任一 CF 异常，结果 CF 也异常；`join()` 抛 `CompletionException`，要 `getCause()` 解包。纯计算场景用 `join()` 更干净（不抛受检）。
5. **CF 不可变，每步返回新 CF**：链式每步产生新对象，**必须接住返回值**，否则流水线丢失（第五课已踩）。

---

## 四、概念点 4：`ExecutorService` / 线程池

### 本质
复用固定几个线程跑很多任务，避免每次 `new Thread` 的创建/销毁开销（分配栈、系统调用）。

| 工厂方法 | 行为 | 隐患 |
|------|------|------|
| `newFixedThreadPool(n)` | 固定 n 线程 | **无界队列**，任务堆积 OOM |
| `newCachedThreadPool()` | 按需建，空闲 60s 回收 | 最大线程数 `Integer.MAX_VALUE`，瞬时海量任务会爆 |
| `newSingleThreadExecutor()` | 单线程 | 同上（无界队列） |
| `new ThreadPoolExecutor(...)` | 精确配：核心/最大/队列/饱和策略 | —— 生产推荐 |

### 提交与取结果
| 方法 | 任务类型 | 返回 |
|------|---------|------|
| `execute(Runnable)` | 无返回值 | `void` |
| `submit(Callable)` | 有返回值 | `Future<T>`，`get()` 阻塞拿 |

```java
ExecutorService pool = Executors.newFixedThreadPool(4);
Future<Integer> f = pool.submit(() -> { Thread.sleep(100); return 42; });
Integer result = f.get();      // 阻塞拿结果
pool.shutdown();               // 不再接新任务，等已提交的跑完
```

### 两个 Lab 验证的两件事（今日重点理解）
- **方法 1（`countDistinctWorkers`）验证"复用"**：20 个任务记录"干活线程的名字"去重 → 应 ≤ 4 个。用 `ConcurrentHashMap.newKeySet()`（线程安全 Set，自动去重）+ `Thread.currentThread().getName()`。
- **方法 2（`sumResults`）验证"能拿回结果"**：`new Thread` 的 `run()` 返回 `void` 拿不回结果；`submit(Callable)` + `Future.get()` 逐个取回，验证一个不丢。

### 关键坑（重点）
1. **【经典坑】`Executors` 工厂的隐患（阿里 Java 规范明确禁止）**：
   - `newFixedThreadPool`/`newSingleThreadExecutor` 用**无界** `LinkedBlockingQueue` → 任务无限堆积，内存耗尽 OOM
   - `newCachedThreadPool` 最大线程数 `Integer.MAX_VALUE` → 瞬时海量任务创建海量线程，把系统打爆
   - 生产写法：`new ThreadPoolExecutor(core, max, keepAlive, unit, new ArrayBlockingQueue<>(capacity), saturationPolicy)`
2. **【经典坑】饱和策略（队列也满时）**：默认 `AbortPolicy` 抛 `RejectedExecutionException`；`CallerRunsPolicy` 让提交线程自己跑（削峰）；`DiscardPolicy` 静默丢弃。
3. **`shutdown` vs `shutdownNow`**：`shutdown` 优雅（等已提交的跑完）；`shutdownNow` 立即中断所有并返回未执行任务列表。
4. **`shutdown` 后再 `submit` 抛 `RejectedExecutionException`**。
5. **`Future.get()` 的异常包装**：`Callable` 内部抛异常 → `get()` 抛 `ExecutionException`，要 `getCause()` 解包（呼应第八课反射的 `InvocationTargetException`）。
6. **线程命名**：默认 `pool-1-thread-N`，生产用 `ThreadFactory` 自定义前缀（如 `order-worker-`），日志排查必备。
7. **线程数经验值**：CPU 密集 ≈ 核数 + 1；IO 密集可更多（取决于阻塞比例）。JDK 25 下 IO 密集优先考虑虚拟线程（概念点 5）。

---

## 五、概念点 5：Virtual Threads（JDK 21+）

### 本质
虚拟线程 = JVM 实现的轻量级线程，不直接映射 OS 线程。**阻塞（sleep/IO）时自动让出底层平台线程**，所以能开几十万个也不怕——"一个任务一个线程"成为可能。

| 维度 | 平台线程 | 虚拟线程 |
|------|:---:|:---:|
| 创建开销 | 大（MB 栈） | 极小（KB 级） |
| 能开多少 | 几千 ~ 万级 | **数十万级** |
| 底层 | 1:1 映射 OS 线程 | JVM 调度到少量平台线程 |
| 阻塞时 | 占着 OS 线程 | **自动让出** |
| 适合 | 计算密集 | IO 密集 / 大量并发等待 |

### 最小代码（JDK 21+）
```java
Thread.ofVirtual().start(() -> System.out.println("虚拟线程"));

// 主流：每任务一个虚拟线程的执行器 + try-with-resources
try (ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor()) {
    pool.submit(() -> 干点活());
}   // 退出自动 close()（JDK 21+ ExecutorService 是 AutoCloseable）
```

### 关键坑（重点）
1. **【经典坑】虚拟线程不是万能的**：**计算密集**任务用虚拟线程没收益（不阻塞就不让位），照样占满平台线程；收益在**阻塞等待**（IO/sleep）上。
2. **【经典坑】pin 问题（钉住）**：虚拟线程在 **`synchronized` 块内阻塞**时会"钉住"底层平台线程，导致让位机制失效（高并发 + synchronized = 瓶颈）。JDK 21 已改进多数场景，但**用 `ReentrantLock` 更配虚拟线程**（衔接概念点 1）。这是"虚拟线程 + synchronized 别混用"的经典坑。
3. **栈深有限**：虚拟线程栈浅，很深递归会 `StackOverflowError`。
4. **`newVirtualThreadPerTaskExecutor` 每任务新建线程**：适合大量短任务/IO 任务，不适合需要"复用状态"的场景（那用固定池）。
5. **try-with-resources 关闭**：`close()` 等价 `shutdown` + 无限期等待；JVM 退出前必须关，否则虚拟线程非守护会拖住进程。

---

## 六、今日踩坑汇总（对话中实际发生）

| # | 坑 | 错在哪 | 正确 |
|---|-----|-------|------|
| 1 | 方法名拼写 | `getBlance()` | `getBalance()` |
| 2 | `Int` 不是类型 | `Int orderId` | `int orderId` |
| 3 | 复制粘贴没改文案 | `fetchProduct` 返回"用户" | 应返回"商品"/"物流" |
| 4 | **TOCTOU 锁外检查** | `tryWithdraw` 锁前查余额 | 先拿锁，锁内检查 |
| 5 | **环形取模基数** | `% count` | `% buffer.length` |
| 6 | **lambda 漏分隔符** | `a + b` | `a + "|" + b` |
| 7 | 耗时打印重复减 start | `elapsed - start` | 打 `elapsed` |

其中 4/5/6 是**真正的并发/函数式思维坑**（不是打字错误），值得反复看。

---

## 七、知识掌握度自评（本课后更新）

| 主题 | 掌握度 | 备注 |
|------|-------|------|
| `ReentrantLock` | ⭐⭐⭐⭐ | 超时/可重入/公平锁已实战；缺：`lockInterruptibly` 实战、公平锁实测 |
| `Condition` | ⭐⭐⭐⭐ | 有界队列完整实现；已理解虚假唤醒/多条件 |
| `CompletableFuture` | ⭐⭐⭐⭐ | 补了 `thenCombine`；缺：`applyToEither`/`handle` 实战 |
| 线程池 | ⭐⭐⭐⭐ | 复用 + Future 取结果；缺：自定义 `ThreadPoolExecutor` + 饱和策略实战 |
| 虚拟线程 | ⭐⭐⭐ | 概念 + 万级任务验证；缺：真实 IO 场景、pin 现象复现 |

## 八、下一步

- 第十八课：综合实战——迷你 IoC 框架（反射 + 注解 + 泛型 + 设计模式），理解 Spring 的 `@Component`/`@Inject`
- 本课难点（TOCTOU / 环形缓冲 / 线程池工厂隐患 / 虚拟线程 pin）建议过两天回看，都是面试高频题
