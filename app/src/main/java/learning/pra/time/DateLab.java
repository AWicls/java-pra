package learning.pra.time;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DateLab {

    public static LocalDate today() {
        return LocalDate.now();
    }

    public static LocalDateTime appointmentReminder(LocalDate starDate, int daysLater) {
        return starDate.plusDays(daysLater).atTime(LocalTime.of(9,0));
    }

}
