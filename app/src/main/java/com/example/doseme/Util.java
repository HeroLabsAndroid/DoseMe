package com.example.doseme;

import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Locale;

public class Util {


    public static String DateTimeToString(LocalDateTime ldt) {
        String day = String.format(Locale.getDefault(), (ldt.getDayOfMonth() < 10) ? "0%d" : "%d", ldt.getDayOfMonth());
        String month = String.format(Locale.getDefault(), (ldt.getMonth().getValue() < 10) ? "0%d" : "%d", ldt.getMonth().getValue());
        return String.format(Locale.getDefault(),"%s.%s.%4d", day, month, ldt.getYear());
    }

    public static String DateToString(LocalDate ld) {
        String day = String.format(Locale.getDefault(), (ld.getDayOfMonth() < 10) ? "0%d" : "%d", ld.getDayOfMonth());
        String month = String.format(Locale.getDefault(), (ld.getMonth().getValue() < 10) ? "0%d" : "%d", ld.getMonth().getValue());
        return String.format(Locale.getDefault(),"%s.%s.%4d", day, month, ld.getYear());
    }

    public static String DateTimeToShortString(LocalDateTime ldt) {
        String day = String.format(Locale.getDefault(), (ldt.getDayOfMonth() < 10) ? "0%d" : "%d", ldt.getDayOfMonth());
        String month = String.format(Locale.getDefault(), (ldt.getMonth().getValue() < 10) ? "0%d" : "%d", ldt.getMonth().getValue());
        return String.format(Locale.getDefault(),"%s.%s", day, month);
    }

    public static long days_since(LocalDateTime ldt) {
        return ldt.until(LocalDateTime.now(), ChronoUnit.DAYS);
    }


    public static LocalDateTime end_of_day(LocalDateTime ldt) {
        return ldt.withHour(23).withMinute(59).withSecond(59);
    }

    public static LocalDateTime start_of_day(LocalDateTime ldt) {
        return ldt.withHour(0).withMinute(0).withSecond(0);
    }

    public static boolean same_day(LocalDateTime l1, LocalDateTime l2) {
        return (l1.getDayOfYear() == l2.getDayOfYear() && l1.getYear() == l2.getYear());
    }

    public static boolean leap_year(LocalDateTime ldt) {
        LocalDateTime start_of_year = ldt.withDayOfMonth(1).withMonth(1);
        long days_in_year = start_of_year.until(start_of_year.plusYears(1), ChronoUnit.DAYS);
        return (days_in_year > 365);
    }
}
