package com.example.prm392_final_prj.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "tour_schedule",
        foreignKeys = @ForeignKey(entity = TourEntity.class,
                parentColumns = "id",
                childColumns = "tourId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index(value = {"tourId"})}
)
public class TourScheduleEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "place")
    public String place;

    @ColumnInfo(name = "departTime")
    public Date departTime; // Sẽ dùng TypeConverter

    @ColumnInfo(name = "address")
    public String address;

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB, name = "image")
    public byte[] image;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "tourId")
    public int tourId;

    // --- Constructors ---
    public TourScheduleEntity() {
    }
    @Ignore
    public TourScheduleEntity(String place, Date departTime, String address, byte[] image, String description, int tourId) {
        this.place = place;
        this.departTime = departTime;
        this.address = address;
        this.image = image;
        this.description = description;
        this.tourId = tourId;
    }

    // --- Getters and Setters ---
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Date getDepartTime() {
        return departTime;
    }

    public void setDepartTime(Date departTime) {
        this.departTime = departTime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTourId() {
        return tourId;
    }

    public void setTourId(int tourId) {
        this.tourId = tourId;
    }
}