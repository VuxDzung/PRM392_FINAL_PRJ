package com.example.prm392_final_prj.dao;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.prm392_final_prj.dao.*;
import com.example.prm392_final_prj.entity.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {TourEntity.class, UserEntity.class, TourScheduleEntity.class,
        BookingOrderEntity.class, ReviewEntity.class, NotificationEntity.class},
        version = 4,
        exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    // Khai báo abstract cho tất cả DAO
    public abstract TourDao tourDao();
    public abstract UserDao userDao();
    public abstract TourScheduleDao tourScheduleDao();
    public abstract BookingOrderDao bookingOrderDao();
    public abstract ReviewDao reviewDao();
    public abstract NotificationDao notificationDao();

    // Singleton Pattern
    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;

    // ExecutorService để chạy các tác vụ DB trên background thread
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "online_tour_db") // Đặt tên file DB
                            .fallbackToDestructiveMigration() // Thêm nếu bạn muốn DB tự xóa khi nâng cấp version
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
