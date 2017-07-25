package it.mad8.expenseshare.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by giaco on 03/04/2017.
 */

public class CalendarUtils {

    public static boolean onSameDay(Calendar date1, Calendar date2) {
        return (date1.get(Calendar.DAY_OF_MONTH) == date2.get(Calendar.DAY_OF_MONTH) &&
                date1.get(Calendar.MONTH) == date2.get(Calendar.MONTH) &&
                date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR));
    }

    public static long difference(int type, Calendar date1, Calendar date2) {

        long milliseconds = date1.getTimeInMillis() - date2.getTimeInMillis();
        if (type == Calendar.MILLISECOND)
            return milliseconds;

        long seconds = milliseconds / 1000;
        if (type == Calendar.SECOND)
            return seconds;

        long minutes = seconds / 60;
        if (type == Calendar.MINUTE)
            return minutes;

        long hours = minutes / 60;
        if (type == Calendar.HOUR)
            return hours;

        long days = hours / 24;
        if (type == Calendar.DAY_OF_YEAR)
            return days;
        /*long months = 0;
        if(type == Calendar.MONTH)
            return months;
        long years = 0;
        if (type == Calendar.YEAR)
            return years;*/
        throw new IllegalArgumentException("Type not allowed for this operation");
    }

    /**
     * Get current date and time formatted as ISO 8601 string.
     */
    public static String now() {
        return mapCalendarToISOString(GregorianCalendar.getInstance());
    }

    private static final java.lang.String ISO8601_MILLISEC = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    private static final java.lang.String ISO8601_MIN = "yyyy-MM-dd'T'HH:mmZ";
    private static final java.lang.String ISO8601 = "yyyy-MM-dd'T'HH:mm:ssZ";

    public static Calendar mapISOStringToCalendar(String ISOString) {

        java.util.Calendar calendar = java.util.Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
        SimpleDateFormat dateformatMilli = new SimpleDateFormat(ISO8601_MILLISEC, Locale.getDefault());
        SimpleDateFormat dateformatMin = new SimpleDateFormat(ISO8601_MIN, Locale.getDefault());
        SimpleDateFormat dateformat = new SimpleDateFormat(ISO8601, Locale.getDefault());
        //  dateformat.setTimeZone(TimeZone.getTimeZone("GMT"));
        String s = ISOString.replace("Z", "+00:00");
        try {
            Date date = dateformat.parse(s);
            date.setHours(date.getHours() - 1);
            calendar.setTime(date);
        } catch (ParseException e) {

            try {
                Date date = dateformatMilli.parse(s);
                date.setHours(date.getHours() - 1);
                calendar.setTime(date);
            } catch (ParseException e1) {
                try {
                    Date date = dateformatMin.parse(s);
                    date.setHours(date.getHours() - 1);
                    calendar.setTime(date);
                } catch (ParseException e2) {
                    e.printStackTrace();
                    e1.printStackTrace();
                    e2.printStackTrace();
                }
            }
        }
        return calendar;
    }

    public static String mapCalendarToISOString(final Calendar date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        return df.format(date.getTime());
    }
}
