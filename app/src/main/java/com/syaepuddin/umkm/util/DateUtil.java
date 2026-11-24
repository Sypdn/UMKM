package com.syaepuddin.umkm.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {
    private static final Locale ID_LOCALE = new Locale("id", "ID");

    public static String formatDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", ID_LOCALE);
        return sdf.format(new Date(timestamp));
    }

    public static String formatDateTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", ID_LOCALE);
        return sdf.format(new Date(timestamp));
    }

    public static String formatTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", ID_LOCALE);
        return sdf.format(new Date(timestamp));
    }

    public static long getStartOfDay(long timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    public static long getEndOfDay(long timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTimeInMillis();
    }

    public static long getSevenDaysAgoStart() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -6);
        return getStartOfDay(cal.getTimeInMillis());
    }

    public static String getGreeting() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour >= 4 && hour < 11) {
            return "Selamat Pagi";
        } else if (hour >= 11 && hour < 15) {
            return "Selamat Siang";
        } else if (hour >= 15 && hour < 18) {
            return "Selamat Sore";
        } else {
            return "Selamat Malam";
        }
    }
}
