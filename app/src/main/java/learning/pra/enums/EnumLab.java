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
}
