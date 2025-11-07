package com.example.prm392_final_prj.utils;

import androidx.room.TypeConverter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeConverter {

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
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }
    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}