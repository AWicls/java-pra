package learning.pra.time;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    void meetingTimeAcrossZonesFormatsThreeCities() {
        LocalDateTime meeting = LocalDateTime.of(2026, 8, 7, 19, 0);

        String result = DateLab.meetingTimeAcrossZones(meeting);

        // 北京 +08:00（无夏令时）
        assertTrue(result.contains("Beijing=2026-08-07T19:00+08:00[Asia/Shanghai]"),
                "北京段应含 +08:00 偏移: " + result);
        // 纽约夏令时（8 月）-04:00
        assertTrue(result.contains("NewYork=2026-08-07T07:00-04:00[America/New_York]"),
                "纽约段应含 -04:00 夏令时偏移: " + result);
        // 伦敦夏令时（8 月）+01:00
        assertTrue(result.contains("London=2026-08-07T12:00+01:00[Europe/London]"),
                "伦敦段应含 +01:00 夏令时偏移: " + result);
        // 三段用 " / " 分隔
        assertTrue(result.contains(" / "), "段间应以 ' / ' 分隔: " + result);
    }
}
