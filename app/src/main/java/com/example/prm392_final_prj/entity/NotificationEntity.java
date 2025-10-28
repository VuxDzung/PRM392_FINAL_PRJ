package com.example.prm392_final_prj.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "notification",
        foreignKeys = @ForeignKey(entity = UserEntity.class,
                parentColumns = "id",
                childColumns = "userId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index(value = {"userId"})}
)
public class NotificationEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "notiType")
    public String notiType;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "message")
    public String message;

    @ColumnInfo(name = "isRead")
    public boolean isRead;

    @ColumnInfo(name = "createdAt")
    public Date createdAt; // Sẽ dùng TypeConverter

    @ColumnInfo(name = "userId")
    public int userId;

    // --- Constructors ---
    public NotificationEntity() {
    }

    public NotificationEntity(String notiType, String title, String message, boolean isRead, Date createdAt, int userId) {
        this.notiType = notiType;
        this.title = title;
        this.message = message;
        this.isRead = isRead;
        this.createdAt = createdAt;
        this.userId = userId;
    }

    // --- Getters and Setters ---
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNotiType() {
        return notiType;
    }

    public void setNotiType(String notiType) {
        this.notiType = notiType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}