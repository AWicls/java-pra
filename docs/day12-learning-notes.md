# Java 学习笔记 - Day 12（2026-08-11）

> 学习项目：java-pra（JDK 25 + Gradle 9.6.1 + JUnit Jupiter 6.0.1）
> 进度：第十五课（NIO 与现代 IO）-- 五个概念点 + 实战，已全部完成
> 代码：[app/src/main/java/learning/pra/nio/NioLab.java](app/src/main/java/learning/pra/nio/NioLab.java)
> 测试：[app/src/test/java/learning/pra/nio/NioLabTest.java](app/src/test/java/learning/pra/nio/NioLabTest.java)
> 总计：8 个测试全绿（NioLabTest）

---

## 一、第十五课：NIO 与现代 IO

### 15.1 旧 IO（流式）vs 新 IO（NIO）——块 vs 流

| 维度 | 旧 IO（`java.io`） | 新 IO / NIO（`java.nio`） |
|------|------|------|
| 数据单位 | 字节/字符流（`InputStream`/`Reader`） | 块 / Buffer（`ByteBuffer`） |
| 核心概念 | 流（Stream）单向、阻塞 | 通道（Channel）+ 缓冲区（Buffer） |
| 文件操作 | `File`（仅元信息） | `Path` + `Files`（读写删搬一步到位） |
| 阻塞 | 阻塞式 | 支持非阻塞（`Selector`） |
| 现代度 | 旧，日常少用 | 现代项目主流 |

**日常最该掌握**：`Path` + `Files`（一行读写整个文件），`Buffer`/`Channel`/`Selector` 了解即可。

### 15.2 Path + Files（便捷层，日常主角）

```java
Path p = Path.of("build/tmp/nio-lab/data.txt");   // 造路径（替代 new File(...)）
Files.writeString(p, "你好 NIO");                  // 一行写（默认 UTF-8）
Files.readString(p);                              // 一行读
Files.readAllLines(p);                            // 一次读所有行
Files.createDirectories(p.getParent());           // 建目录（含父级，存在不报错）
Files.exists(p); Files.size(p);                   // 判断存在 / 字节大小
Files.copy(src, dst, StandardCopyOption.REPLACE_EXISTING); // 复制
Files.move(src, dst, StandardCopyOption.REPLACE_EXISTING); // 移动/重命名
Files.deleteIfExists(p);                          // 删除（不存在不抛异常）
Files.list(dir)                                   // 列目录条目（返回 Stream，要关）
Files.mismatch(a, b)                              // 比对内容，-1 表示相同
```

**分工记忆**：`Path` 负责"路径的构建与分解"（`getFileName`/`getParent`/`resolve`），`Files` 负责"文件系统操作"。两者分工，别混。

### 15.3 Buffer + Channel（底层机制，心智模型）

**生活例子**：`Channel` 像输油管道（连接文件与程序），`Buffer` 像管道中间的油桶（先接满一桶再取）——批量搬运，系统调用少，所以快。

**ByteBuffer 四指针**：

| 指针 | 作用 |
|------|------|
| `capacity` | 桶的总容量（固定） |
| `position` | 下一个要写/读的位置 |
| `limit` | 可写 / 可读的上限 |
| `mark` | 标记位（先用不上） |

**核心四步（放→翻→取→清）**：
```java
ByteBuffer buf = ByteBuffer.allocate(1024);
buf.put((byte) 65);     // ① 放：写模式，position 后移
buf.flip();             // ② 翻：写→读 切换
buf.get();              // ③ 取：读模式，position 后移
buf.clear();            // ④ 清：读→写 复位
```

**flip 的设计意义（关键）**：一个 Buffer 读写共用同一块数组和 position，所以分两种模式靠 flip 切换。`flip()` = `limit = position; position = 0`，用 limit 记住"刚才写了多少"，从而**只读真实写入的数据，不读桶里残留的空字节**（等价第七课 `write(buffer, 0, n)` 的截断，省得自己记长度）。

**`getXxx()` 宽度坑**：`get()` 读 1 字节、`getInt()` 读 4 字节、`getLong()` 读 8 字节；读宽度超过剩余可用量抛 `BufferUnderflowException`。

### 15.4 Selector（选择器）——网络专属，理解即可

**生活例子**：传统服务器像"一连接一线程"的银行（每个顾客占一个前台，连接多了线程爆）；`Selector` 像总台接待，**轮询所有通道，谁有数据就喊谁**，一个线程管所有连接。

**关键三步**：注册 → 选择 → 处理
```java
Selector selector = Selector.open();
channel.configureBlocking(false);   // 必须非阻塞
channel.register(selector, SelectionKey.OP_READ);  // 登记关心的读事件

while (true) {
    selector.select();                               // 阻塞等有通道就绪
    Set<SelectionKey> keys = selector.selectedKeys();
    for (SelectionKey k : keys) {
        if (k.isReadable()) { /* 读 */ }
        selector.selectedKeys().remove(k);           // 处理完要移除，否则重复
    }
}
```

> `Selector` 是 Netty / 高并发网络服务器的基石，日常文件读写用不到，理解心智模型即可。

### 15.5 实战：用 NIO 重写 IoLab（对比流式 vs 便捷式）

第七课要手写 `readLine()` 循环 + `PrintWriter`；现在 `Files` 一两行搞定——这就是 NIO 便捷层对日常开发的意义。

```java
// 逐行写（拼接 + 写 + 读回）
String content = String.join(System.lineSeparator(), lines);
Files.writeString(path, content);
return Files.readString(path);

// 比较两个文件内容
Files.mismatch(pa, pb) == -1   // -1 相同，否则是第一个不同字节的下标
```

---

## 二、本课踩坑速记（重点）

1. **`Files.writeString(path, content, null)` 传 null 触发重载歧义**：第三参位置既可是 `Charset` 也可是 `OpenOption...`，传 null 被按 `Charset` 解析 → charset=null 运行 NPE。写默认 UTF-8 直接不传第三参。
2. **`Files.list(p)` 返回 `Stream<Path>` 持有目录句柄**，必须 try-with-resources 关闭（呼应第七课 `Files.lines` 坑）。
3. **catch 里 `e.printStackTrace()` 吞异常**后返回兜底值，会让统计静默算错，应 `throw new RuntimeException(e)`。
4. **Stream 一次性**：`.count()` 后 stream 已消费，不能再用 `.filter().count()`；要么一次链式聚合，要么多次 `Files.list`。
5. **`get()` vs `getInt()`**：读宽度不同，`getInt()` 读 4 字节，剩余不足抛 `BufferUnderflowException`；读单字节用 `get()`。
6. **`Path.of(a)` 笔误**：`filesIdentical` 两个参数都写 `a`，等于拿同一文件和自己比，永远 true。复制粘贴时注意第二个参数用 `b`。
7. **返回值别搞混**：`writeLines` 要求返回"读回的内容"，写成返回路径字符串是错的。

---

## 三、NioLab 完整方法清单

| 成员 | 知识点 | 测试覆盖 |
|------|------|------|
| `writeAndRead` | Path.of + createDirectories + writeString + readString | ✅ |
| `readAllLines` | Files.readAllLines | ✅ |
| `fileCensus` | Files.list + count + filter + mapToLong+size+sum | ✅ |
| `bufferFlipDemo` | ByteBuffer 放→翻→取 + hasRemaining 循环 | ✅ |
| `writeLines` | String.join + writeString + readString | ✅ |
| `filesIdentical` | Files.mismatch 比对内容 | ✅ |

---

## 四、命名约定备忘（小驼峰）

- 方法/变量/参数名 = **小驼峰**：首单词小写，后续单词首字母大写（`fileName`、`maxSize`）
- 类名 = **大驼峰**：首单词也大写（`NioLab`）
- 参数用**名词性**描述"传入的东西"，集合用复数，避免单字母（`a`/`b` 能加语义就加，如 `fileA`/`fileB`）

---

## 五、下一步

- **第十六课：现代特性速览（JDK 16-25）** —— Records / Sealed Classes / Pattern Matching / Switch Pattern / Text Blocks，用 Records + Sealed + Switch Pattern 重构表达式树
- 待补强：`EnumSet.range` / `EnumMap` 高级用法 / `Map.computeIfAbsent` / `Collections.unmodifiableXxx` vs `List.of`（自评表里遗留项，可穿插补强）
