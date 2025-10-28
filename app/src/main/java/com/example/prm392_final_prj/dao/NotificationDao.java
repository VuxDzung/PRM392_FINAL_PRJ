package com.example.prm392_final_prj.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.prm392_final_prj.entity.NotificationEntity;
import java.util.List;

@Dao
public interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(NotificationEntity notification);

    @Query("SELECT * FROM notification WHERE userId = :userId ORDER BY createdAt DESC")
    LiveData<List<NotificationEntity>> getNotificationsForUser(int userId);

    @Query("UPDATE notification SET isRead = 1 WHERE id = :notificationId")
    void markAsRead(int notificationId);
}