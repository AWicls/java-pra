package learning.pra.modern;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class VehicleLabTest {

    @Test
    void describeCar() {
        String result = VehicleLab.describe(new Car("丰田"));
        assertTrue(result.contains("丰田"));
    }

    @Test
    void describePickup() {
        String result = VehicleLab.describe(new Pickup(2000));
        assertTrue(result.contains("2000"));
    }

    @Test
    void describeBike() {
        String result = VehicleLab.describe(new Bike(21));
        assertTrue(result.contains("21"));
    }
}
