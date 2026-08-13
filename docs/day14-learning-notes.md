# Java 学习笔记 - Day 14（2026-08-13）

> 学习项目：java-pra（JDK 25 + Gradle 9.6.1 + JUnit Jupiter 6.0.1）
> 进度：第十六课（现代特性速览）续 Day14 -- 概念点3/4/5 快速掌握 + 第十六课完成 ✅
> 说明：概念点1 Records / 概念点2 Sealed 于 Day13（08-12）完成，见 [day13-learning-notes.md](day13-learning-notes.md)；今日概念点3-5 用户熟悉已跳过（快速掌握记录），实战用户自练跳过

---

## 一、第十六课（续）：现代特性速览（JDK 16-25）

### 概念点 3：Pattern Matching for instanceof（快速掌握，已跳过）

**本质**：`instanceof` 判断类型 + 绑定变量一步完成，省去手动强转。JDK 16 正式。

```java
if (obj instanceof String s) {   // 条件为真，s 自动绑定，类型已是 String
    System.out.println(s.toUpperCase());   // 无需 (String) obj 强转
}
```

#### 绑定变量作用域（flow scoping）—— 唯一的坑
| 写法 | s 可用？ | 原因 |
|------|:---:|------|
| `if (obj instanceof String s) { }` | ✅ | 条件为真必绑定 |
| `if (obj instanceof String s && s.length() > 3)` | ✅ | `&&` 短路，右侧时 s 已绑定 |
| `if (obj instanceof String s \|\| ...)` | ❌ 编译错 | `\|\|` 左侧 false 时 s 未绑定 |
| `if (!(obj instanceof String s))` | ❌ | 取反后 s 不保证绑定 |

**一句话**："类型检查 + 变量绑定"合成一步，变量仅在编译器确认"绑定必然成立"的代码区可用（`&&` 右侧可用，`||` 右侧不可用）。

#### 适用场景
- 开放式类型体系（第三方类、无法 sealed 限制）的零星类型判断，配 `else` 兜底
- 与 Sealed 互补：sealed 体系用 switch pattern，开放体系用 instanceof pattern

### 概念点 4：Switch Pattern（快速掌握，已跳过）

**本质**：switch 从"按常量匹配"升级为"按类型匹配 + 绑定变量 + when 守卫 + case null"，JDK 21 正式。

```java
static String classify(Object input) {
    return switch (input) {
        case null                            -> "空输入";
        case String s when s.length() <= 3   -> "短字符串";   // when 守卫
        case String s                        -> "长字符串";
        case Point p                         -> "坐标(" + p.x() + ")";
        case Object o                        -> "未知: " + o.getClass().getSimpleName();  // 总类型兜底
    };
}
```

#### 语法演进
| 阶段 | 能力 |
|------|------|
| 传统 switch 语句 | 只按值匹配，无结果值 |
| switch 表达式（JDK 17 正式） | 有返回值、`->` 箭头、无 fall-through |
| 模式匹配 switch（JDK 21 正式） | 类型匹配 + 绑定变量 + when + case null |

#### 五个核心点
| 点 | 说明 |
|----|------|
| 类型模式 | `case String s` 匹配类型并绑定变量 |
| 守卫条件 | `when` 后接布尔表达式，可用绑定变量；false 落下一个同类型分支 |
| `case null` | JDK 17+ 可显式处理 null；不写则 null 进 switch 直接 NPE |
| 总类型模式 `case Object o` | 兜底一切，替代 default；**必须放最后** |
| 穷尽性 | sealed 体系可省兜底；开放体系必须有 `case Object`/`default` |

#### 坑（重点）
- **支配规则（dominance）**：`case Object o` 能匹配一切，更具体的模式（`case String`）必须在其**之前**，顺序反了编译报"标签被前面的标签支配"
- **case null 必须显式**：不写 null 分支，null 进来直接 NPE
- **守卫顺序**：`case String s when ...` 与 `case String s` 的先后决定守卫 false 时落哪个分支
- **兜底必须存在**：开放类型体系漏 `case Object`/`default` 编译错"switch 表达式不包含所有可能的输入值"

### 概念点 5：Text Blocks（快速掌握，已跳过）

**本质**：用 `"""..."""` 写多行字符串，免去 `\n` + `+` 拼接。JDK 15 正式（预览于 13/14）。

```java
String html = """
        <div>
            <h2>%s</h2>
        </div>
        """.formatted(title);   // formatted() JDK 15+，替代 String.format
```

#### 核心行为规则
| 规则 | 说明 |
|------|------|
| 开头换行 | `"""` 后紧跟的换行**被忽略**，内容从下一行开始 |
| **缩进** | 结束 `"""` 的位置决定公共缩进移除量——结束三引号顶到哪，内容行就统一往前挪到哪；更靠右的行保留额外缩进 |
| 行尾空白 | 每行末尾空白被忽略（想保留用 `\s`） |
| 末尾换行 | 结束 `"""` 前有换行 → 内容以 `\n` 结尾；紧跟最后一行 → 无末尾换行 |

#### 转义与配合
- `\`（行尾）= **续行符**，下一行内容接上来不换行
- `\s` = 空格（保留行尾空格）；`\"""` = 内容里的三个引号；内部单双引号**不用转义**
- `formatted(...)`（JDK 15+，替代 String.format）/ `stripIndent()` / `indent(n)`

#### 坑
- **缩进由结束 `"""` 决定**：内容行缩进少于它会被当公共缩进抹掉
- **末尾换行陷阱**：结束三引号换不换行，决定结果末尾带不带 `\n`——断言等值字符串前先想清楚
- **行尾空格悄悄消失**：要保留必须 `\s`

---

## 二、完成情况与下一步

- 第十六课概念点 3/4/5：快速掌握，已跳过（用户熟悉）
- 实战重构表达式树：跳过（用户有自己的项目练习）
- **第十六课（现代特性速览）完成** ✅：Records / Sealed / Pattern Matching for instanceof / Switch Pattern / Text Blocks 五个概念点全部过完（概念点1/2 在 Day13，3/4/5 在 Day14）
- 下一步：第十七课 并发进阶（ReentrantLock / Condition / CompletableFuture / ExecutorService / Virtual Threads）——明天看
