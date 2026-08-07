# Java 学习笔记 - Day 8（2026-08-07）

> 学习项目：java-pra（JDK 25 + Gradle 9.6.1 + JUnit Jupiter 6.0.1）
> 进度：第十一课（java.time 日期时间）-- 全 6 节完成
> 代码：[app/src/main/java/learning/pra/time/DateLab.java](app/src/main/java/learning/pra/time/DateLab.java)
> 测试：[app/src/test/java/learning/pra/time/DateLabTest.java](app/src/test/java/learning/pra/time/DateLabTest.java)
> 总计：13 个测试全绿

---

## 一、第十一课：java.time 日期时间

### 11.1 为什么需要 java.time -- 旧 API 的三大坑

#### 11.1.1 旧 API 槽点对比

| 坑 | 旧 API 表现 | 实际后果 |
|---|------|------|
| 月份从 0 | `Calendar.JANUARY == 0` | 填 1 月得写 `0`，写 `1` -> 2 月 |
| 年份减 1900 | `new Date().getYear()` -> `126` | 已废弃，直觉错乱 |
| 可变对象 | `date.setYear(2025)` 改原对象 | 多线程共享被改，数据错乱 |
| 格式化线程不安全 | `SimpleDateFormat` 共享解析错乱 | 经典并发坑 |

```java
// java.util.Date 构造器：年份减 1900，月份减 1
java.util.Date d = new java.util.Date(125, 0, 1);   // 125+1900=2025年, 0+1=1月
System.out.println(d.getYear());                    // 125（已废弃，返回 年份-1900）
```

#### 11.1.2 java.time 三剑客登场

- **不可变**：改一个日期返回新对象，原对象不变（天生线程安全）
- **月份从 1**：`Month.JANUARY == 1`，符合直觉
- **职责分离**：`LocalDate`（日期）/ `LocalTime`（时间）/ `LocalDateTime`（日期+时间），不混用

#### 11.1.3 `LocalDate` 常用方法

| 方法 | 作用 |
|------|------|
| `LocalDate.now()` | 当前日期 |
| `LocalDate.of(年, 月, 日)` | 构造指定日期 |
| `getYear()` / `getMonthValue()` / `getDayOfMonth()` | 取字段 |
| `plusDays(n)` / `minusMonths(n)` | 加减，返回**新对象**（不可变） |
| `isBefore(other)` / `isAfter(other)` | 比较 |

---

### 11.2 时间三剑客的分工 -- `LocalTime` / `LocalDateTime`

#### 11.2.1 分工对比

| 类 | 表示什么 | 精度 | 例 |
|---|------|------|------|
| `LocalDate` | 日期（无时间无时区） | 日 | `2026-08-07` |
| `LocalTime` | 时间（无日期无时区） | 纳秒 | `19:30:00` |
| `LocalDateTime` | 日期+时间（无时区） | 纳秒 | `2026-08-07T19:30:00` |

> 三者都带 `Local` 前缀 = 不含时区信息。"本地"意味着"在某地看就是这时间"。

#### 11.2.2 关键事实

1. 三者都**不可变**（改字段返回新对象）
2. 互相组合/拆分：
   - `localDate.atTime(localTime)` -> `LocalDateTime`
   - `localDateTime.toLocalDate()` -> `LocalDate`
3. `LocalTime` **不支持 plusMonths/plusYears**（无月年概念），只有 `plusHours/plusMinutes/plusSeconds/plusNanos`
4. 默认 `toString()` 用 `T` 分隔日期和时间（ISO 8601 标准）

```java
LocalDate date = LocalDate.of(2026, 8, 7);          // 2026-08-07
LocalTime time = LocalTime.of(19, 30);               // 19:30
LocalDateTime dateTime = date.atTime(time);          // 2026-08-07T19:30
// atTime：LocalDate 的实例方法，把日期和时间拼成 LocalDateTime（"at" 命名惯例 = 在...时刻）

LocalDateTime nextDay = dateTime.plusDays(1);        // 返回新对象 2026-08-08T19:30
System.out.println(dateTime);                        // 原 2026-08-07T19:30（未变）

LocalDate extractedDate = dateTime.toLocalDate();    // 2026-08-07
LocalTime extractedTime = dateTime.toLocalTime();   // 19:30
```

---

### 11.3 时区与时间戳 -- `ZonedDateTime` / `Instant`

#### 11.3.1 四类时间表示

| 类 | 含义 | 是否带时区 | 典型用途 |
|---|------|----------|------|
| `LocalDateTime` | 本地日期+时间 | ❌ | "8 月 7 日 19:00"（不含地点） |
| `ZonedDateTime` | 带时区的日期+时间 | ✅（如 `Asia/Shanghai`）| "北京 8 月 7 日 19:00" |
| `Instant` | UTC 时间戳（自 1970-01-01 起的秒/纳秒）| 隐含 UTC | 服务器日志、数据库存储、跨时区比较 |
| `OffsetDateTime` | 带 UTC 偏移量的日期+时间 | ✅（如 `+08:00`）| 协议字段（带偏移不带地区名）|

#### 11.3.2 关键事实

1. `Instant` 是**绝对时间**（UTC 时间轴上的一个点），全球同一时刻 `Instant` 值相同
2. `ZonedDateTime` = `LocalDateTime` + `ZoneId`（地区）+ `ZoneOffset`（偏移）
3. 同一 `Instant` 在不同 `ZoneId` 下显示不同的"本地时间"
4. 时区 ID 用地区名：`ZoneId.of("Asia/Shanghai")`（不是 `GMT+8`，地区名能处理夏令时）

```java
Instant now = Instant.now();                                // 绝对时刻（UTC）
ZonedDateTime beijing = now.atZone(ZoneId.of("Asia/Shanghai"));
// ZoneId.of("Asia/Shanghai")：工厂方法，参数是 IANA 时区名（斜杠格式，非 GMT+8）
// now.atZone(zoneId)：把 Instant 转到指定时区的 ZonedDateTime（"在 X 地看是几点"）

ZonedDateTime ny      = now.atZone(ZoneId.of("America/New_York"));
ZonedDateTime london   = now.atZone(ZoneId.of("Europe/London"));

System.out.println(beijing);    // 2026-08-07T19:00+08:00[Asia/Shanghai]
System.out.println(ny);         // 2026-08-07T07:00-04:00[America/New_York]
System.out.println(london);     // 2026-08-07T12:00+01:00[Europe/London]
// 三个 ZonedDateTime 表示同一时刻，.toInstant() 都相等
```

#### 11.3.3 `Instant` 常用方法

| 方法 | 作用 |
|------|------|
| `Instant.now()` | 当前 UTC 时刻 |
| `Instant.ofEpochSecond(seconds)` | 从 Unix 纪元构造 |
| `getEpochSecond()` / `toEpochMilli()` | 取秒数 / 毫秒数 |
| `plusSeconds(n)` / `minusSeconds(n)` | 加减 |
| `atZone(zoneId)` | 转到指定时区 |

---

### 11.4 时长与周期 -- `Duration` vs `Period`

#### 11.4.1 本质区别

| 维度 | `Duration` | `Period` |
|---|------|------|
| 单位 | 时分秒（精确到纳秒）| 年月日（日历单位）|
| 底层 | 总秒数 + 纳秒调整 | `years` + `months` + `days` 三字段 |
| 加到 `LocalTime` | ✅ | ❌ |
| 加到 `LocalDate` | ❌ | ✅ |
| 加到 `LocalDateTime` | ✅ | ✅ |
| 跨夏令时 | 按绝对秒数算 | 按日历推进，墙上钟时数会变 |
| 典型用途 | 计时器、超时、间隔测量 | 合同期、生日、账单周期 |

#### 11.4.2 跨夏令时陷阱

```java
LocalDateTime march = LocalDateTime.of(2026, 3, 1, 10, 0);   // 3 月 1 日 10 点
LocalDateTime april = march.plus(Period.ofMonths(1));        // 4 月 1 日 10 点（日历月）
LocalDateTime byDur  = march.plus(Duration.ofHours(24*30));  // 3 月 31 日 10 点（30 天后，3 月有 31 天）
```
- `Period.ofMonths(1)` 推进**一个日历月** -> 4 月 1 日
- `Duration.ofHours(24*30)` 推进**精确 720 小时** -> 3 月 31 日

#### 11.4.3 最小示例

```java
Duration d = Duration.between(LocalTime.of(9, 0), LocalTime.of(17, 30));
System.out.println(d);            // PT8H30M（ISO 8601 时长格式，PT = Period Time）
System.out.println(d.toMinutes()); // 510

Period p = Period.between(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 8, 7));
System.out.println(p);                       // P7M6D（7 月 6 天）
System.out.println(p.getMonths());          // 7
```

> ISO 8601 时长字符串：`P` 开头（Period 标记），`T` 之后是时间分量（H/M/S），之前是日期分量（Y/M/D）。`P1Y2M3D` = 1 年 2 月 3 天，`PT2H` = 2 小时。
> `Duration.between` 只能用于时刻类（`LocalTime`/`Instant`/`LocalDateTime`），`Period.between` 只能用于日期类（`LocalDate`）。

#### 11.4.4 `Duration` 常用方法

| 方法 | 作用 |
|------|------|
| `ofHours(n)` / `ofMinutes(n)` / `ofSeconds(n)` | 构造 |
| `between(start, end)` | 算两个时刻的差 |
| `toMinutes()` / `toHours()` / `toMillis()` | 转换为单一单位 |
| `plus(other)` / `minus(other)` | 加减 |
| `toHoursPart()` / `toMinutesPart()` | 拆出时分部分（JDK 9+）|

---

### 11.5 格式化与解析 -- `DateTimeFormatter`

#### 11.5.1 新旧对比

| 维度 | `SimpleDateFormat`（旧）| `DateTimeFormatter`（新）|
|---|------|------|
| 线程安全 | ❌ 共享会错乱 | ✅ 不可变，线程安全 |
| 配套类 | `Date` | `LocalDate`/`LocalTime`/`LocalDateTime` |
| 预定义格式 | 需自己传字符串 | `ISO_LOCAL_DATE` 等常量 |
| 解析容错 | 默认宽松 | 默认严格 |

#### 11.5.2 三种用法

```java
LocalDateTime dt = LocalDateTime.of(2026, 8, 7, 19, 30);

// 1. 预定义常量（ISO 格式）
String iso = dt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);  // 2026-08-07T19:30:00

// 2. 自定义模式（最常用）
DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm");
String cn = dt.format(f);                              // 2026年08月07日 19:30

// 3. 解析（反向）
DateTimeFormatter parser = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
LocalDateTime parsed = LocalDateTime.parse("2026-08-07 19:30:00", parser);
```

> `dt.format(formatter)`：实例方法，把 `dt` 按 formatter 转字符串。也支持反向写法 `formatter.format(dt)`。
> `LocalDateTime.parse(text, formatter)`：不传 formatter 默认用 `ISO_LOCAL_DATE_TIME`（要求 `T` 分隔符）。

#### 11.5.3 模式字母速查（大小写敏感）

| 字母 | 含义 | 示例 |
|------|------|------|
| `yyyy` | 4 位年 | `2026` |
| `MM` | 2 位月（补零）| `08` |
| `dd` | 2 位日 | `07` |
| `HH` | 2 位时（24 小时制）| `19` |
| `mm` | 2 位分 | `30` |
| `ss` | 2 位秒 | `00` |
| `MM` vs `mm` | 月 vs 分 -- **大小写不同含义不同** | 经典坑 |

---

### 11.6 实战 -- 账单计算器

#### 11.6.1 实战方法

```java
public static List<String> installmentDates(LocalDate startDate, int periods) {
    if (periods < 1) throw new IllegalArgumentException();
    List<String> list = new ArrayList<>();
    for (int i = 0; i < periods; i++) {
        LocalDate newDate = startDate.plus(Period.ofMonths(i));
        String formatted = newDate.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日"));
        list.add(formatted);
    }
    return list;
}
```

#### 11.6.2 关键点

1. 第 1 期 = `startDate` 当天（`i=0`，`Period.ofMonths(0)` 返回原日期）
2. `plusMonths(n)` 自动处理跨月跨年 + 月末缩到该月最后一天（不抛异常）
3. 用 `Period` 而不是 `plusDays(30)` -- 后者在 2 月会算错

---

## 二、本课踩坑速记（5 条）

1. **`ZoneId.of("Aisa/Shanghai")` 拼写错** -- 编译期不查字符串字面量，运行时抛 `ZoneRulesException: Unknown time-zone ID`。写时区名要核对 IANA 拼写
2. **`addBusinessDays` vs `addBusinessDay`** -- 命名要匹配"方法做什么"。加多个工作日应该是复数
3. **循环边界 `for (int i = 1; i < periods; i++)` 漏第 1 期** -- 第 1 期是 `i=0`，从 1 开始少一项
4. **`periods < 1` 没抛异常** -- for 循环空跑返回空 `List`，静默通过而非报错。边界参数应显式校验
5. **假通过陷阱** -- `BUILD SUCCESSFUL` 只代表代码编译 + 现有测试通过，**不证明新方法被测试覆盖**。校验时若新方法未补测试，会假通过；要看测试数与新增方法是否匹配

---

## 三、DateLab 完整方法清单

| 方法 | 知识点 | 测试覆盖 |
|------|------|------|
| `today()` | `LocalDate.now()` | ✅ |
| `appointmentReminder(start, days)` | `LocalDate` + `LocalTime` -> `LocalDateTime` + 不可变性 | ✅ |
| `meetingTimeAcrossZones(dt)` | `ZonedDateTime` + `withZoneSameInstant` 跨时区 | ✅ |
| `workDuration(start, end)` | `Duration.between` | ✅ |
| `addBusinessDays(start, days)` | `Period.ofDays` 推进 | ✅ |
| `formatCustom(dt)` / `parseCustom(text)` | `DateTimeFormatter` 双向 + 往返一致 | ✅ |
| `installmentDates(start, periods)` | 综合 + 月末缩进 + 异常边界 | ✅ |

---

## 四、待补强基础库

- `java.time` 全套（`LocalDate`/`LocalTime`/`LocalDateTime`/`ZonedDateTime`/`Instant`/`Duration`/`Period`/`DateTimeFormatter`）✅ 本课已补
- `Collectors` 全套 / `Collections` 工具类 / `Arrays` 工具类
- `String` 全套方法 / `Function`-`Predicate`-`Consumer`-`Supplier` 全套
- `Optional` 全套方法（下一课）

---

## 五、下一步

- 第十二课：`Optional` 深入（Stream 配套，工程必备）
- 或第十三课：泛型进阶实战（PECS 实战补强）
- 或第十四课：Stream 进阶（`groupingBy` / `flatMap` / 自定义收集器）
