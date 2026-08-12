package learning.pra.modern;

sealed interface VehicleLab permits Car, Truck, Bike {

}

final record Car(String brand) implements VehicleLab {}
sealed class Truck implements VehicleLab permits Pickup {}
final class Pickup extends Truck {
    int loadKg;
}

non-sealed class Bike implements VehicleLab {
    int gears;
}
