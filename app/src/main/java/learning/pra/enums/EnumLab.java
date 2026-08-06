package learning.pra.enums;

import java.util.ArrayList;
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
}
