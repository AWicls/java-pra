package learning.pra.time;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class DateLabTest {

    @Test
    void todayReturnsCurrentDate() {
        LocalDate result = DateLab.today();
        assertNotNull(result);
        assertEquals(LocalDate.now(), result);
    }
}
