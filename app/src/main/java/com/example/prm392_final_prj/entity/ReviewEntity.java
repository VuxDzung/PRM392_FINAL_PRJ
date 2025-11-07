package com.example.prm392_final_prj.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "review",
        foreignKeys = {
                @ForeignKey(entity = TourEntity.class,
                        parentColumns = "id",
                        childColumns = "tourId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = UserEntity.class,
                        parentColumns = "id",
                        childColumns = "userId",
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index(value = {"tourId"}), @Index(value = {"userId"})}
)
public class ReviewEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "rate")
    public float rate;

    @ColumnInfo(name = "content")
    public String content;

    @ColumnInfo(name = "departTime")
    public Date timeStamp;

    @ColumnInfo(name = "tourId")
    public int tourId;

    @ColumnInfo(name = "userId")
    public int userId;

    // --- Constructors ---
    public ReviewEntity() {
    }

    public ReviewEntity(int id, float rate, String content, Date timeStamp, int tourId, int userId) {
        this.id = id;
        this.rate = rate;
        this.content = content;
        this.timeStamp = timeStamp;
        this.tourId = tourId;
        this.userId = userId;
    }
    @Ignore
    public ReviewEntity(float rate, String content, Date timeStamp, int tourId, int userId) {
        this.rate = rate;
        this.content = content;
        this.timeStamp = timeStamp;
        this.tourId = tourId;
        this.userId = userId;
    }

    // --- Getters and Setters ---
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getTourId() {
        return tourId;
    }

    public void setTourId(int tourId) {
        this.tourId = tourId;
    }

    public int getUserId() {
        return userId;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}