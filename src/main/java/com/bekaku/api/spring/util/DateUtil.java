package com.bekaku.api.spring.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.Date;
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
    public static long MILLS_IN_HOUR = 1000L * 60 * 60;
    public static long MILLS_IN_MINUTE = 1000L * 60;
    public static long MILLS_IN_WEEK = 1000L * 60 * 60 * 24 * 7;

    public static String DATE_FORMAT = "uuuu-MM-dd";
    public static String DATE_FORMAT_2 = "dd/MM/yyyy";
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

    public static LocalDateTime minusLocalDatetimeMinutes(LocalDateTime date, Long minutes) {
        return date.minusMinutes(minutes);
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

    /**
     * @param date
     * @param formatStyle eg FormatStyle.LONG, FormatStyle.MEDIUM, FormatStyle.SHORT, FormatStyle.FULL
     * @return LONG  17 February 2022, MEDUIM 17-Feb-2022, SHORT 17/02/22, FULL //Thursday, 17 February, 2022
     */
    public static String getLocalDateByFormat(LocalDate date, FormatStyle formatStyle) {
        return date.format(DateTimeFormatter.ofLocalizedDate(formatStyle));
    }

    public static String getLocalDateByForMat(LocalDateTime date, String format) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format);
        return dtf.format(date);
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

    public static boolean isAfter(LocalDateTime left, LocalDateTime right) {
        return left.isAfter(right);
    }

    public static boolean isBefore(LocalDateTime left, LocalDateTime right) {
        return left.isBefore(right);
    }

    public static boolean isEqual(LocalDate left, LocalDate right) {
        return left.isEqual(right);
    }

    public static boolean isEqualOrAfter(LocalDate left, LocalDate right) {
        return left.isEqual(right) || left.isAfter(right);
    }

    public static boolean isDateInRange(LocalDate currentDate, LocalDate dateStart, LocalDate dateEnd) {
        return (currentDate.isEqual(dateStart) || currentDate.isAfter(dateStart)) && (currentDate.isEqual(dateEnd) || currentDate.isBefore(dateEnd));
    }

    public static LocalDate getEveryMonthNextDateBy(LocalDate currentDate, int dayNo) {
        LocalDate nextDay = currentDate.plusMonths(1);
        return getDayOfMonthByDayNo(nextDay, dayNo);
    }

    public static LocalDate getEveryTwoMonthNextDateBy(LocalDate currentDate, int dayNo) {
        int monthNo = getMonthNoBy(currentDate);
        int plusMonth = monthNo % 2 == 0 ? 2 : 1;
        LocalDate nextDay = currentDate.plusMonths(plusMonth);
        return getDayOfMonthByDayNo(nextDay, dayNo);
    }

    public static LocalDate getEveryThreeMonthNextDateBy(LocalDate currentDate, int dayNo) {
        int monthNo = getMonthNoBy(currentDate);
        int plusMonth = 3 - (monthNo % 3);
        LocalDate nextDay = currentDate.plusMonths(plusMonth);
        return getDayOfMonthByDayNo(nextDay, dayNo);
    }

    public static LocalDate getDayOfMonthByDayNo(LocalDate currentDate, int dayNo) {
        int dateLenghtInMonth = getLengthOfMonthByDate(currentDate);
        LocalDate nextDate;
        if (dayNo == 0 || dateLenghtInMonth < dayNo) {
            nextDate = currentDate.with(TemporalAdjusters.lastDayOfMonth());
        } else {
            nextDate = currentDate.withDayOfMonth(dayNo);
        }
        return nextDate;
    }

    public static int getMonthNoBy(LocalDate currentDate) {
        return currentDate.getMonthValue();
    }

    public static int getYearNoBy(LocalDate currentDate) {
        return currentDate.getYear();
    }

    public static int getLengthOfMonthByDate(LocalDate currentDate) {
        // Get the current month and year
        int currentYear = currentDate.getYear();
        int currentMonth = currentDate.getMonthValue();
        // Create a YearMonth object for the current year and month
        YearMonth currentYearMonth = YearMonth.of(currentYear, currentMonth);
        // Get the length (number of days) in the current month
        return currentYearMonth.lengthOfMonth();
    }

    public static int checkQuarterLogRound(LocalDate nowDate) {
        int yearNow = DateUtil.getLocalDateYear(nowDate);
        LocalDate quarterOne = DateUtil.parseDate(yearNow + ConstantData.FRIST_APRIL_MONTH_DATE, DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate quarterTwo = DateUtil.parseDate(yearNow + ConstantData.FRIST_JULY_MONTH_DATE, DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate quarterThree = DateUtil.parseDate(yearNow + ConstantData.FRIST_OCTOBER_MONTH_DATE, DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate quarterFour = DateUtil.parseDate(yearNow + ConstantData.FRIST_JANUARY_MONTH_DATE, DateTimeFormatter.ISO_LOCAL_DATE);
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
        LocalDate roundOne = DateUtil.parseDate(yearNow + ConstantData.FRIST_MARCH_MONTH_DATE, DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate roundTwo = DateUtil.parseDate(yearNow + ConstantData.FRIST_MAY_MONTH_DATE, DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate roundThree = DateUtil.parseDate(yearNow + ConstantData.FRIST_JULY_MONTH_DATE, DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate roundFoure = DateUtil.parseDate(yearNow + ConstantData.FRIST_SEPTEMBER_MONTH_DATE, DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate roundFive = DateUtil.parseDate(yearNow + ConstantData.FRIST_NOVEMBER_MONTH_DATE, DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate roundSix = DateUtil.parseDate(yearNow + ConstantData.FRIST_JANUARY_MONTH_DATE, DateTimeFormatter.ISO_LOCAL_DATE);
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

    /**
     * @param year    Change this to the desired year
     * @param quarter Change this to the desired quarter (1, 2, 3, or 4)
     * @return LocalDate
     */
    public static LocalDate getQuarterStartDate(int year, int quarter) {
        return YearMonth.of(year, Month.of((quarter - 1) * 3 + 1)).atDay(1);
    }

    /**
     * @param year    Change this to the desired year
     * @param quarter Change this to the desired quarter (1, 2, 3, or 4)
     * @return LocalDate
     */
    public static LocalDate getQuarterEndDate(int year, int quarter) {
        return YearMonth.of(year, Month.of(quarter * 3)).atEndOfMonth();
    }

    public static LocalDateTime convertDateToLacalDatetime(Date d) {
        if (d == null) {
            return null;
        }
        // Convert Date to Instant
        Instant instant = d.toInstant();
        // Convert Instant to LocalDateTime using the system default time zone
        return instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
