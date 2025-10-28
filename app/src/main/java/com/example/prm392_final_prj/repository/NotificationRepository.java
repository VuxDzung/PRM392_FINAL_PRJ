package com.example.prm392_final_prj.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.prm392_final_prj.dao.NotificationDao;
import com.example.prm392_final_prj.dao.AppDatabase; // <-- ĐÃ THAY ĐỔI
import com.example.prm392_final_prj.entity.NotificationEntity;
import java.util.List;

public class NotificationRepository {

    private NotificationDao mNotificationDao;

    public NotificationRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mNotificationDao = db.notificationDao();
    }

    public void insertNotification(NotificationEntity notification) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mNotificationDao.insert(notification);
        });
    }

    public LiveData<List<NotificationEntity>> getNotificationsForUser(int userId) {
        return mNotificationDao.getNotificationsForUser(userId);
    }

    public void markNotificationAsRead(int notificationId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mNotificationDao.markAsRead(notificationId);
        });
    }
}