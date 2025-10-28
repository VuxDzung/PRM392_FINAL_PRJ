package com.example.prm392_final_prj.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "booking_order",
        foreignKeys = {
                @ForeignKey(entity = TourEntity.class,
                        parentColumns = "id",
                        childColumns = "tourId",
                        onDelete = ForeignKey.SET_NULL),
                @ForeignKey(entity = UserEntity.class,
                        parentColumns = "id",
                        childColumns = "userId",
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index(value = {"tourId"}), @Index(value = {"userId"})}
)
public class BookingOrderEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "startTime")
    public Date startTime; // Sẽ dùng TypeConverter

    @ColumnInfo(name = "adultAmount")
    public int adultAmount;

    @ColumnInfo(name = "childAmount")
    public int childAmount;

    @ColumnInfo(name = "status")
    public int status;

    @ColumnInfo(name = "tourId")
    public Integer tourId;

    @ColumnInfo(name = "userId")
    public int userId;

    // --- Constructors ---
    public BookingOrderEntity() {
    }

    public BookingOrderEntity(Date startTime, int adultAmount, int childAmount, int status, Integer tourId, int userId) {
        this.startTime = startTime;
        this.adultAmount = adultAmount;
        this.childAmount = childAmount;
        this.status = status;
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

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public int getAdultAmount() {
        return adultAmount;
    }

    public void setAdultAmount(int adultAmount) {
        this.adultAmount = adultAmount;
    }

    public int getChildAmount() {
        return childAmount;
    }

    public void setChildAmount(int childAmount) {
        this.childAmount = childAmount;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Integer getTourId() {
        return tourId;
    }

    public void setTourId(Integer tourId) {
        this.tourId = tourId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}