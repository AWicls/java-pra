package learning.pra.time;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * java.time 日期时间练习（第十一课）：LocalDate/LocalTime/LocalDateTime、时区、时长周期、格式化。
 *
 * <p>覆盖 today、预约提醒、跨时区会议时间、工作时长、加工作日、
 * 自定义 DateTimeFormatter 格式化/解析、分期日期（账单计算器）。
 */
public class DateLab {

    public static LocalDate today() {
        return LocalDate.now();
    }

    public static LocalDateTime appointmentReminder(LocalDate starDate, int daysLater) {
        return starDate.plusDays(daysLater).atTime(LocalTime.of(9,0));
    }

    public static String meetingTimeAcrossZones(LocalDateTime localDateTime) {
        String as = "Asia/Shanghai";
        String an = "America/New_York";
        String el = "Europe/London";
        ZonedDateTime asaz = localDateTime.atZone(ZoneId.of(as));
        ZonedDateTime anaz = asaz.withZoneSameInstant(ZoneId.of(an));
        ZonedDateTime elaz = asaz.withZoneSameInstant(ZoneId.of(el));
        return "Beijing=" + asaz + " / " + "NewYork=" + anaz + " / " + "London="  + elaz;
    }

    public static Duration workDuration(LocalTime start, LocalTime end) {
        return Duration.between(start, end);
    }

    public static LocalDate addBusinessDays(LocalDate start, int days) {
        return start.plus(Period.ofDays(days));
    }

    public static String formatCustom(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm"));
    }

    public static LocalDateTime parseCustom(String text) {
        return LocalDateTime.parse(text, DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm"));
    }

    public static List<String> installmentDates(LocalDate starDate, int periods) {
        if (periods < 1) throw new IllegalArgumentException();
        List<String> list = new ArrayList<>();
        for (int i = 0; i < periods; i++) {
            LocalDate newDate = starDate.plus(Period.ofMonths(i));
            String formaeDate = newDate.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日"));
            list.add(formaeDate);
        }
        return list;
    }

}
