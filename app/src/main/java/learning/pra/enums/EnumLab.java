package learning.pra.enums;

import java.util.ArrayList;
import java.util.List;


public class EnumLab {
    public static enum TrafficLight {
        RED, GREEN, YELLOW
    }

    public static String action(TrafficLight color) {
        return switch(color) {
            case RED -> "停";
            case GREEN -> "行";
            case YELLOW -> "慢";
        };
    }

    public static List<String> allNames() {
        List<String> list = new ArrayList<>();
        TrafficLight[] values = TrafficLight.values();
        for (TrafficLight trafficLight : values) {
            list.add(trafficLight.toString());
        }
        return list;
    }

    public static TrafficLight parse(String name) {
        try {
            return TrafficLight.valueOf(name);
        }catch(IllegalArgumentException e) {
            return null;
        }
    }
}
