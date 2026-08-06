package learning.pra.enums;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;

public class EnumLab {
    public static enum TrafficLight {
        RED("红灯", 30),
        GREEN("绿灯", 25),
        YELLOW("黄灯", 5);

        private final String displayName;
        private final int durationSeconds;

        TrafficLight(String displayName, int durationSeconds) {
            this.displayName = displayName;
            this.durationSeconds = durationSeconds;
        }

        @Override
        public String toString() {
            return displayName;
        }

        public String getDisplayName() {
            return this.displayName;
        }

        public int getDurationSeconds() {
            return this.durationSeconds;
        }

        public static TrafficLight byDuration(int seconds) {
            for (TrafficLight itm : TrafficLight.values()) {
                if (itm.getDurationSeconds() == seconds) {
                    return itm;
                }
            }
            return null;
        }

    }

    public static String action(TrafficLight color) {
        return switch (color) {
            case RED -> "停";
            case GREEN -> "行";
            case YELLOW -> "慢";
        };
    }

    public static List<String> allNames() {
        List<String> list = new ArrayList<>();
        TrafficLight[] values = TrafficLight.values();
        for (TrafficLight trafficLight : values) {
            list.add(trafficLight.getDisplayName());
        }
        return list;
    }

    public static TrafficLight parse(String name) {
        try {
            return TrafficLight.valueOf(name);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public enum Operation {
        PLUS {
            @Override
            public double apply(double a, double b) {
                return a + b;
            }
        },
        MINUS {
            @Override
            public double apply(double a, double b) {
                return a - b;
            }
        },
        TIMES {
            @Override
            public double apply(double a, double b) {
                return a * b;
            }
        },
        DIVIDE {
            @Override
            public double apply(double a, double b) {
                return a / b;
            }
        };

        public abstract double apply(double a, double b);
    }

    // 新增枚举 Permission
    public enum Permission {
        READ, WRITE, DELETE, EXECUTE
    }

    // 1. 用 EnumSet 表示用户权限集合
    public static EnumSet<Permission> adminPermissions() {
        return EnumSet.allOf(Permission.class); // 用 allOf 工厂：管理员拥有所有权限
    }

    public static EnumSet<Permission> guestPermissions() {
        return EnumSet.of(Permission.READ); // 用 of 工厂：访客只有 READ
    }

    // 2. 用 EnumMap 给每个权限配中文描述
    public static EnumMap<Permission, String> permissionDescriptions() {
        EnumMap<Permission, String> desc = new EnumMap<>(Permission.class); // new EnumMap<>(Permission.class)
        desc.put(Permission.READ, "读取");
        desc.put(Permission.WRITE, "写入");
        desc.put(Permission.DELETE, "删除");
        desc.put(Permission.EXECUTE, "执行");
        return desc;
    }

    // 3. 检查用户是否有某权限
    public static boolean hasPermission(EnumSet<Permission> userPerms, Permission required) {
        return userPerms.contains(required); // EnumSet.contains 即可
    }

    // 4. 给用户加权限（返回新 Set，不改原 Set）
    public static EnumSet<Permission> grantPermission(EnumSet<Permission> userPerms, Permission newPerm) {
        EnumSet<Permission> copy = EnumSet.copyOf(userPerms); // 先拷贝
        copy.add(newPerm); // 再加
        return copy;
    }

    public enum AppConfig {
        INSTANCE;                              // 单例常量

        private final java.util.Map<String, String> settings = new java.util.HashMap<>();

        AppConfig() {                           // 隐式 private
            settings.put("app.name", "java-pra");
            settings.put("app.version", "1.0");
        }

        public String get(String key) {
            return settings.get(key);
        }

        public void set(String key, String value) {
            settings.put(key, value);
        }
    }

    // §10.6 策略模式实战：支付方式

    // 支付结果（record，JDK 16+ 不可变数据载体）
    public record PayResult(boolean success, String message, double actualAmount) {}

    // 支付方式枚举：每个常量是一个具体策略，独立实现 pay 抽象方法
    public enum PaymentMethod {
        ALIPAY("支付宝", 0.01, 50000, "即时到账") {
            @Override
            public PayResult pay(double amount) {
                if (amount > getLimit()) {
                    return new PayResult(false, "超过单笔限额 " + getLimit(), 0);
                }
                double actual = amount * (1 - getFeeRate());
                return new PayResult(true, getDisplayName() + " " + getArrivalTime(), actual);
            }
        },
        WECHAT("微信", 0.005, 30000, "即时到账") {
            @Override
            public PayResult pay(double amount) {
                if (amount > getLimit()) {
                    return new PayResult(false, "超过单笔限额 " + getLimit(), 0);
                }
                double actual = amount * (1 - getFeeRate());
                return new PayResult(true, getDisplayName() + " " + getArrivalTime(), actual);
            }
        },
        BANK_CARD("银行卡", 0.0, 100000, "T+1 到账") {  // feeRate=0，用固定 2 元手续费
            @Override
            public PayResult pay(double amount) {
                if (amount > getLimit()) {
                    return new PayResult(false, "超过单笔限额 " + getLimit(), 0);
                }
                double actual = amount - 2;              // 固定 2 元
                return new PayResult(true, getDisplayName() + " " + getArrivalTime(), actual);
            }
        },
        CREDIT_CARD("信用卡", 0.03, 20000, "T+3 到账") {
            @Override
            public PayResult pay(double amount) {
                if (amount > getLimit()) {
                    return new PayResult(false, "超过单笔限额 " + getLimit(), 0);
                }
                double actual = amount * (1 - getFeeRate());
                return new PayResult(true, getDisplayName() + " " + getArrivalTime(), actual);
            }
        };

        private final String displayName;
        private final double feeRate;
        private final double limit;
        private final String arrivalTime;

        PaymentMethod(String displayName, double feeRate, double limit, String arrivalTime) {
            this.displayName = displayName;
            this.feeRate = feeRate;
            this.limit = limit;
            this.arrivalTime = arrivalTime;
        }

        public String getDisplayName() { return displayName; }
        public double getFeeRate() { return feeRate; }
        public double getLimit() { return limit; }
        public String getArrivalTime() { return arrivalTime; }

        // 抽象方法：每常量独立实现（策略模式核心）
        public abstract PayResult pay(double amount);
    }

    // 策略上下文：持有策略引用，委托给具体策略
    public static class PaymentContext {
        private PaymentMethod method;

        public PaymentContext(PaymentMethod method) {
            this.method = method;
        }

        public void setMethod(PaymentMethod method) {
            this.method = method;
        }

        public PaymentMethod getMethod() {
            return method;
        }

        public PayResult checkout(double amount) {
            return method.pay(amount);   // 不关心具体是哪个，委托给策略
        }
    }
}
