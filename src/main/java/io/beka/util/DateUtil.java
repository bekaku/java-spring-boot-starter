package io.beka.util;

import java.time.*;
import java.time.format.DateTimeFormatter;

/*
https://mkyong.com/java/java-how-to-get-current-date-time-date-and-calender/
 */
public class DateUtil {
    public static long MILLS_IN_YEAR = 1000L * 60 * 60 * 24 * 365;
    public static long MILLS_IN_MONTH = 1000L * 60 * 60 * 24 * 30;
    public static long MILLS_IN_DAY = 1000L * 60 * 60 * 24;
    public static long MILLS_IN_WEEK = 1000L * 60 * 60 * 24 * 7;

    public static String DATE_FORMAT = "uuuu-MM-dd";
    public static String CDN_YEAR_MONTH_FORMAT = "uuuuMM";
    public static String TIME_FORMAT = "HH:mm:ss";

    public static long getCurrentMilliTimeStamp() {
        return Instant.now().toEpochMilli();
    }

    public static long getCurrentSecondTimeStamp() {
        return Instant.now().getEpochSecond();
    }

    public static LocalDateTime getLocalDateTimeNow() {
        return LocalDateTime.now();
    }

    public static String getLocalDateTimeByFormat(LocalDateTime date, String format) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format);
        return dtf.format(date); //  2021/03/22 16:37:15
    }

    public static LocalDate getLocalDateNow() {
        return LocalDate.now();
    }

    public static String getLocalDateByForMat(LocalDate date, String format) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format);
        return dtf.format(date); // 2021/03/22
    }

    public static LocalTime getLocalTimeNow() {
        return LocalTime.now(); // 16:37:15
    }

    public static String getLocalTimeByForMat(LocalTime time, String format) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format);
        return dtf.format(time);
    }

    public static ZonedDateTime getCurrentTimeZone() {
//        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss");
//        ZonedDateTime now = ZonedDateTime.now();
//        System.out.println(dtf.format(now));                  // 2021/03/22 16:37:15
//        System.out.println(now.getOffset());                  // +08:00
        return ZonedDateTime.now();
    }

    public static ZonedDateTime getCurrentDateTimeByTimeZone(String zoneId, String format) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format);

        // Get default time zone
//        System.out.println(ZoneOffset.systemDefault());         // Asia/Kuala_Lumpur
//        System.out.println(OffsetDateTime.now().getOffset());   // +08:00

        // get current date time, with +08:00
        ZonedDateTime now = ZonedDateTime.now();
//        System.out.println(dtf.format(now));                    // 2021/03/22 16:37:15
//        System.out.println(now.getOffset());                    // +08:00

        // get get current date time, with +09:00
//        ZonedDateTime japanDateTime = now.withZoneSameInstant(ZoneId.of(zoneId));//Asia/Tokyo
//        System.out.println(dtf.format(japanDateTime));          // 2021/03/22 17:37:15
//        System.out.println(japanDateTime.getOffset());          // +09:00

        return now.withZoneSameInstant(ZoneId.of(zoneId));
    }

}
