package com.grandats.api.givedeefive.util;

import com.grandats.api.givedeefive.enumtype.NotifyPeriod;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/*
https://mkyong.com/java/java-how-to-get-current-date-time-date-and-calender/
 */
public class DateUtil {
    public static long MILLS_IN_YEAR = 1000L * 60 * 60 * 24 * 365;
    public static long MILLS_IN_MONTH = 1000L * 60 * 60 * 24 * 30;
    public static long MILLS_IN_DAY = 1000L * 60 * 60 * 24;
    public static long MILLS_IN_WEEK = 1000L * 60 * 60 * 24 * 7;

    public static String DATE_FORMAT = "uuuu-MM-dd";
    public static String MONT_FORMAT = "MM";
    public static String DATE_TIME_FORMAT = "uuuu-MM-dd HH:mm:ss";
    public static String DATE_TIME_WITH_FACTION_OR_SECOND_FORMAT = "uuuu-MM-dd HH:mm:ss.SSS";//2022-04-25 14:42:26 702
    public static String DATE_TIME_WITH_AM_PM_FORMAT = "uuuu-MM-dd HH:mm:ss a";//2022-04-25 14:42:26 02:42:26 PM
    public static String AM_PM_FORMAT = "a";//PM, AM
    public static String DATE_TIME_WITH_TIME_ZONE_FORMAT = "uuuu-MM-dd HH:mm:ss z";//2022-04-25 14:42:26 UTC-04:00
    public static String CDN_YEAR_MONTH_FORMAT = "uuuuMM";
    public static String TIME_FORMAT = "HH:mm:ss";

    public static long getCurrentMilliTimeStamp() {
        return Instant.now().toEpochMilli();
    }

    public static long getCurrentSecondTimeStamp() {
        return Instant.now().getEpochSecond();
    }

    public static LocalDate getLocalDateNow() {
        return LocalDate.now();
    }

    public static YearMonth getYearMonthFromDate(LocalDate d) {
        if (d == null) {
            return null;
        }
        return YearMonth.from(d);
    }

    public static LocalDate getFirstDayOfWeek(int weekNumber, int year, Locale locale) {
        return LocalDate
                .of(year, 12, 31)
                .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, weekNumber)
                .with(DayOfWeek.MONDAY); // day of week;
    }

    public static List<LocalDate> getAllDaysOfTheWeek(int weekNumber, int year, Locale locale) {
        LocalDate firstDayOfWeek = getFirstDayOfWeek(weekNumber, year, locale);
        return IntStream
                .rangeClosed(0, 6)
                .mapToObj(firstDayOfWeek::plusDays)
                .collect(Collectors.toList());
    }

    public static int getCurrentWeekOfYear() {
        return LocalDate.now().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
    }

    public static int getWeekOfYearByDate(LocalDate d) {
        return d.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
    }


    public static LocalDate getLocalDateNow(ZoneId zoneId) {
        return LocalDate.now(zoneId);
    }

    public static LocalDateTime getLocalDateTimeNow() {
        return LocalDateTime.now();
    }

    public static LocalDateTime getLocalDateTimeNow(ZoneId zoneId) {
        return LocalDateTime.now(zoneId);
    }

    public static LocalDate plusLocalDate(LocalDate date, Long days) {
        return date.plusDays(days);
    }

    public static LocalDate minusLocalDate(LocalDate date, Long days) {
        return date.minusDays(days);
    }

    public static long datetimeDiffMinutes(LocalDateTime fromDate, LocalDateTime toDate) {
        if (fromDate == null || toDate == null) {
            return 0;
        }
        return ChronoUnit.MINUTES.between(fromDate, toDate);
    }

    public static String getLocalDateTimeByFormat(LocalDateTime date, String format) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format);
        return dtf.format(date); //  2021/03/22 16:37:15
    }


    public static String getLocalDateByForMat(LocalDate date, String format) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format);
        return dtf.format(date); // 2021/03/22
    }

    public static String getLocalDateByForMat(LocalDateTime date, String format) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format);
        return dtf.format(date); // 2021/03/22
    }

    public static String getLocalDateMonth(LocalDate date) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(MONT_FORMAT);
        return dtf.format(date); // 03
    }

    public static int getLocalDateYear(LocalDate date) {
        return date.getYear(); // 2022
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

    /**
     * DateUtil.isValidDate("2022-06-06", DateTimeFormatter.ISO_LOCAL_DATE)
     *
     * @param dateStr
     * @param dateFormatter
     * @return
     */
    public static boolean isValidDate(String dateStr, DateTimeFormatter dateFormatter) {
        try {
            LocalDate.parse(dateStr, dateFormatter);
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }

    /**
     * DateUtil.parseDate("2022-06-06", DateTimeFormatter.ISO_LOCAL_DATE)
     *
     * @param dateStr
     * @param dateFormatter
     * @return
     */
    public static LocalDate parseDate(String dateStr, DateTimeFormatter dateFormatter) {
        LocalDate date;
        try {
            date = LocalDate.parse(dateStr, dateFormatter);
        } catch (DateTimeParseException e) {
            return null;
        }
        return date;
    }

    /**
     * DateUtil.isValidDateTime("2022-06-06 10:30:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
     *
     * @param dateStr
     * @param dateFormatter
     * @return
     */
    public static boolean isValidDateTime(String dateStr, DateTimeFormatter dateFormatter) {
        try {
            LocalDateTime.parse(dateStr, dateFormatter);
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }

    public static boolean isBefore(LocalDate left, LocalDate right) {
        return left.isBefore(right);
    }

    public static boolean isAfter(LocalDate left, LocalDate right) {
        return left.isAfter(right);
    }

    public static boolean isEqual(LocalDate left, LocalDate right) {
        return left.isEqual(right);
    }

    public static boolean isEqualOrAfter(LocalDate left, LocalDate right) {
        return left.isEqual(right) || left.isAfter(right);
    }

    public static LocalDate calculateNextNotifyDate(NotifyPeriod notifyPeriod, LocalDate fromDate) {
        if (notifyPeriod == null || notifyPeriod == NotifyPeriod.NO_PROGRESS) {
            return null;
        }
        if (fromDate == null) {
            fromDate = getLocalDateNow();
        }
        int progressFrequency = notifyPeriod.ordinal();
        return DateUtil.plusLocalDate(fromDate, (progressFrequency * 7L));
    }

    public static int checkQuarterLogRound(LocalDate nowDate) {
        int yearNow = DateUtil.getLocalDateYear(nowDate);
        LocalDate quarterOne = DateUtil.parseDate(yearNow + "" + ConstantData.FRIST_APRIL_MONTH_DATE, DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate quarterTwo = DateUtil.parseDate(yearNow + "" + ConstantData.FRIST_JULY_MONTH_DATE, DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate quarterThree = DateUtil.parseDate(yearNow + "" + ConstantData.FRIST_OCTOBER_MONTH_DATE, DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate quarterFour = DateUtil.parseDate(yearNow + "" + ConstantData.FRIST_JANUARY_MONTH_DATE, DateTimeFormatter.ISO_LOCAL_DATE);
        if (nowDate.equals(quarterOne)) {
            return 1;
        } else if (nowDate.equals(quarterTwo)) {
            return 2;
        } else if (nowDate.equals(quarterThree)) {
            return 3;
        } else if (nowDate.equals(quarterFour)) {
            return 4;
        }
        return 0;
    }

    public static int checkTwoMonthLogRound(LocalDate nowDate) {
        int yearNow = DateUtil.getLocalDateYear(nowDate);
        LocalDate roundOne = DateUtil.parseDate(yearNow + "" + ConstantData.FRIST_MARCH_MONTH_DATE, DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate roundTwo = DateUtil.parseDate(yearNow + "" + ConstantData.FRIST_MAY_MONTH_DATE, DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate roundThree = DateUtil.parseDate(yearNow + "" + ConstantData.FRIST_JULY_MONTH_DATE, DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate roundFoure = DateUtil.parseDate(yearNow + "" + ConstantData.FRIST_SEPTEMBER_MONTH_DATE, DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate roundFive = DateUtil.parseDate(yearNow + "" + ConstantData.FRIST_NOVEMBER_MONTH_DATE, DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate roundSix = DateUtil.parseDate(yearNow + "" + ConstantData.FRIST_JANUARY_MONTH_DATE, DateTimeFormatter.ISO_LOCAL_DATE);
        if (nowDate.equals(roundOne)) {
            return 1;
        } else if (nowDate.equals(roundTwo)) {
            return 2;
        } else if (nowDate.equals(roundThree)) {
            return 3;
        } else if (nowDate.equals(roundFoure)) {
            return 4;
        } else if (nowDate.equals(roundFive)) {
            return 5;
        } else if (nowDate.equals(roundSix)) {
            return 6;
        }
        return 0;
    }

    public static int checkCalculateUserLevelRound(LocalDate nowDate, int userLevelFrequency) {
        if (userLevelFrequency == 2) {
            return checkTwoMonthLogRound(nowDate);
        } else if (userLevelFrequency == 3) {
            return checkQuarterLogRound(nowDate);
        } else if (userLevelFrequency == 1) {
            return 1;
        }
        return 0;
    }

    public static List<String> getMonthForUserLevel(LocalDate logDate, int userLevelFrequency) {

        if (userLevelFrequency == 2) {
            return monthForTwoMontRound(logDate);
        } else if (userLevelFrequency == 3) {
            return monthForQuaterRound(logDate);
        }
        YearMonth yearMonthNow = DateUtil.getYearMonthFromDate(logDate);
        return List.of(yearMonthNow.toString());
    }

    private static List<String> monthForQuaterRound(LocalDate logDate) {
        YearMonth yearMonthNow = DateUtil.getYearMonthFromDate(logDate);
        YearMonth yearMonthOne = yearMonthNow.minusMonths(2);
        YearMonth yearMonthTwo = yearMonthNow.minusMonths(1);
        return Arrays.asList(yearMonthOne.toString(), yearMonthTwo.toString(), yearMonthNow.toString());
    }

    private static List<String> monthForTwoMontRound(LocalDate logDate) {
        YearMonth yearMonthNow = DateUtil.getYearMonthFromDate(logDate);
        YearMonth yearMonthTwo = yearMonthNow.minusMonths(1);
        return Arrays.asList(yearMonthTwo.toString(), yearMonthNow.toString());
    }

    public static LocalDate convertDatetimeToDate(LocalDateTime dateTime) {
        return dateTime.toLocalDate();
    }
}
