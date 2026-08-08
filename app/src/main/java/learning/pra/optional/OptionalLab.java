package learning.pra.optional;

import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

public class OptionalLab {

    /** 验证三种创建方式：of 有值、ofNullable(null)/empty 为空 */
    public static boolean isPresent(Optional<String> opt) {
        return opt.isPresent();
    }

    /** orElse(默认值)：空时返回默认值（默认值会预先算好） */
    public static String orElseDefault(Optional<String> opt) {
        return opt.orElse("default");
    }

    /** orElseThrow(Supplier)：空时抛自定义异常，用于"空是程序错误"场景 */
    public static String orElseThrow(Optional<String> opt) {
        return opt.orElseThrow(() -> new IllegalArgumentException());
    }

    /** map 变换 + orElse 兜底：有值算长度，空返回 0 */
    public static int mapLength(Optional<String> opt) {
        return opt.map(s -> s.length()).orElse(0);
    }

    /** flatMap：lambda 返回 Optional，用 flatMap 避免嵌套成 Optional<Optional> */
    public static Optional<String> flatMapWrap(Optional<String> opt) {
        if (opt.isEmpty()) {
            return Optional.empty();
        }
        return opt.flatMap(s -> Optional.of(s + "-wrapped"));
    }

    /** filter：满足条件保留值，否则变空 Optional */
    public static Optional<String> filterLong(Optional<String> opt) {
        if (opt.isEmpty()) {
            return Optional.empty();
        }
        return opt.filter(s -> s.length() >= 5);
    }

    // ===== 以下为 12.4 反模式练习的规范改法 =====

    /** 反模式 1 修正：字段直接声明即可，"没有"用 null 表达，不用 Optional */
    private String address;

    /** 反模式 2 修正：集合用空集合表达"没有"，不包 Optional */
    public static List<String> getTags() {
        return List.of();
    }

    /** 反模式 3 修正：可选参数直接传（可传 null 或重载），不包 Optional */
    public void setName(String name) {
    }

    /** 合法用法：方法返回值可用 Optional 声明"可能没有" */
    public Optional<String> findNickname(long id) {
        return Optional.of(String.valueOf(id));
    }

    /** 合法用法：基本类型用专用类 OptionalDouble（避免装箱） */
    OptionalDouble avg = OptionalDouble.of(12.345);

    /** 地址值对象。静态内部类：不依赖外部实例，可独立构造 */
    public static class Address {
        private final String city;

        public Address(String city) {
            this.city = city;
        }

        public String getCity() {
            return city;
        }
    }

    /** 用户值对象，构造参数可传 null 的 Address（正是要防护的中间层为空） */
    public static class User {
        private final Address address;

        public User(Address address) {
            this.address = address;
        }

        public Address getAddress() {
            return address;
        }
    }

    /**
     * 防嵌套 NPE：Optional.ofNullable 兜住 user 本身为 null 的情况；
     * 每层 map 的 getter 若返回 null，链式自动变空，全程无判空。
     */
    public static String safeGetCity(User user) {
        return Optional.ofNullable(user)
                .map(User::getAddress)
                .map(Address::getCity)
                .orElse("unknown");
    }

}
