package learning.pra.modern;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * VehicleLab 密封类（Sealed Classes）的单元测试（第十六课）。
 *
 * <p>验证 Car / Pickup / Bike 的 {@code describe} 各自包含关键信息
 * （车名 / 载重 / 座位数），确认 sealed 继承层级穷尽。
 *
 * @see VehicleLab
 */
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
