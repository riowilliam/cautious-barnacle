package com.fision.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    public static Date getDayAfterLastDayOfWeek(Date startDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);

        // Set hari pertama minggu ke Senin (optional, default Locale bisa Minggu)
        calendar.setFirstDayOfWeek(Calendar.MONDAY);

        // Maju ke akhir minggu (Minggu)
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int daysUntilSunday = Calendar.SATURDAY - dayOfWeek + 1; // jika minggu = hari ke-7
        calendar.add(Calendar.DAY_OF_MONTH, daysUntilSunday);

        // Tambah 1 hari -> Senin minggu berikutnya
        calendar.add(Calendar.DAY_OF_MONTH, 1);

        return calendar.getTime();
    }

    public static Date getDayAfterLastDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.add(Calendar.DAY_OF_MONTH, 1);
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

    public static Date add14Days(Date paramDate) {
        LocalDate localStartDate = paramDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().plusDays(14);
        return Date.from(localStartDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static Date stringToDate(String paramDate) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.parse(paramDate);
    }

    public static Date stringToDateAddOneDay(String paramDate) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return DateTimeHelper.addOneDay(dateFormat.parse(paramDate));
    }

    public static String nowToString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd-hhmmss");
        return dateFormat.format(new Date());
    }

    public static int getYearFromDate(Date dateParam) {
        LocalDate localDate = dateParam.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return localDate.getYear();
    }

    public static Date adjustToNextMondayIfWeekend(Date tenorDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(tenorDate);

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        if (dayOfWeek == Calendar.SATURDAY) {
            calendar.add(Calendar.DAY_OF_MONTH, 2);
        } else if (dayOfWeek == Calendar.SUNDAY) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        return calendar.getTime();
    }

    public static Date convertLocalDateToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

}
