package learning.pra.modern;

sealed interface VehicleLab permits Car, Truck, Bike {

    /** 用 switch 描述任意车型（sealed 保证穷尽，可不写 default） */
    static String describe(VehicleLab v) {
        return switch (v) {
            case Car c -> "汽车: " + c.brand();
            case Pickup p -> "皮卡: " + p.loadKg(); // Pickup 是 Truck 子类，先匹配
            case Truck t -> "卡车";
            case Bike b -> "单车: " + b.gears();
        };
    }
}

final record Car(String brand) implements VehicleLab {
}

sealed class Truck implements VehicleLab permits Pickup {
}

final class Pickup extends Truck {
    final int loadKg;

    Pickup(int loadKg) {
        this.loadKg = loadKg;
    }

    int loadKg() {
        return loadKg;
    }
}

non-sealed class Bike implements VehicleLab {
    final int gears;

    Bike(int gears) {
        this.gears = gears;
    }

    int gears() {
        return gears;
    }
}
