package com.example.prm392_final_prj.utils;

import androidx.room.TypeConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeConverter {
    private static final long SECOND_MILLIS = 1000;
    private static final long MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final long HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final long DAY_MILLIS = 24 * HOUR_MILLIS;
    private static final long MONTH_MILLIS = 30 * DAY_MILLIS;
    private static final long YEAR_MILLIS = 365 * DAY_MILLIS;
    private static final SimpleDateFormat TIME_HOUR_MIN_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.getDefault());

    private static final SimpleDateFormat DAY_FORMAT =
            new SimpleDateFormat("d", Locale.getDefault());

    public static String getFormattedSchedule(Date date) {
        if (date == null) {
            return "Chưa xác định";
        }
        String timeString = TIME_HOUR_MIN_FORMAT.format(date);
        String dayString = DAY_FORMAT.format(date);

        return timeString + " ngày " + dayString;
    }

    public static String getTimeAgo(Date date) {
        if (date == null) {
            return "Vừa xong";
        }

        long time = date.getTime();
        long now = System.currentTimeMillis();

        long diff = Math.abs(now - time);

        if (diff < MINUTE_MILLIS) {
            return "few seconds ago";
        }

        long[] times = {YEAR_MILLIS, MONTH_MILLIS, DAY_MILLIS, HOUR_MILLIS, MINUTE_MILLIS, SECOND_MILLIS};
        String[] units = {"year", "month", "day", "hour", "minute", "second"};

        StringBuilder sb = new StringBuilder();
        int count = 0;

        long remainingDiff = diff;

        for (int i = 0; i < times.length; i++) {
            long unitMillis = times[i];
            String unitName = units[i];

            long quantity = remainingDiff / unitMillis;

            if (quantity > 0) {
                if (count < 3) {
                    if (count > 0) {
                        sb.append(", ");
                    }
                    sb.append(quantity).append(" ").append(unitName);
                    count++;
                }
                remainingDiff %= unitMillis;
            }

            if (count >= 3 && remainingDiff < unitMillis) {
                break;
            }
        }

        if (sb.length() == 0) {
            return "just now";
        }

        return sb.toString() + " ago";
    }

    public static Date parseDateString(String dateStr){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;

        try {
            date = formatter.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }
    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}