package com.fision.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateTimeHelper {
    public static Date getFirstDayOfTheMonth(String monthYear) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM-yyyy", Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();

        Date startDate = dateFormat.parse(monthYear);
        calendar.setTime(startDate);
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        return calendar.getTime();
    }

    public static Date getLastDayOfTheMonth(String monthYear) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM-yyyy", Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();

        Date endDate = dateFormat.parse(monthYear);
        calendar.setTime(endDate);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));

        return calendar.getTime();
    }

    public static String getJakartaDate(Date paramDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID"));
        return "Jakarta, " + dateFormat.format(paramDate);
    }

    public static Date addOneDay(Date paramDate) {
        LocalDate localStartDate = paramDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().plusDays(1);
        return Date.from(localStartDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static Date stringToDate(String paramDate) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.parse(paramDate);
    }

    public static String nowToString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddhhss");
        return dateFormat.format(new Date());
    }
}
