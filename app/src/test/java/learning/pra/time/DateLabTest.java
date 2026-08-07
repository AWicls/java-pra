package learning.pra.time;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class DateLabTest {

    @Test
    void todayReturnsCurrentDate() {
        LocalDate result = DateLab.today();
        assertNotNull(result);
        assertEquals(LocalDate.now(), result);
    }

    @Test
    void appointmentReminderComputesFutureDateTime() {
        LocalDate start = LocalDate.of(2026, 8, 7);

        LocalDateTime result = DateLab.appointmentReminder(start, 3);

        assertEquals(LocalDateTime.of(2026, 8, 10, 9, 0), result);
    }

    @Test
    void appointmentReminderDoesNotMutateInput() {
        LocalDate start = LocalDate.of(2026, 8, 7);
        LocalDate snapshot = start;

        DateLab.appointmentReminder(start, 3);

        // 不可变性：传入对象引用未变，值仍是原始日期
        assertEquals(LocalDate.of(2026, 8, 7), snapshot);
        assertEquals(LocalDate.of(2026, 8, 7), start);
    }
}
