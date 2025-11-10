package com.example.prm392_final_prj.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "tour")
public class TourEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "location")
    public String location;

    @ColumnInfo(name = "duration")
    public String duration;

    @ColumnInfo(name = "price")
    public double price;

    @ColumnInfo(name = "departure")
    public String departure;

    @ColumnInfo(name = "destination")
    public String destination;

    @ColumnInfo(name = "airway")
    public boolean airway;

    @ColumnInfo(name = "transport")
    public String transport;

    @ColumnInfo(name = "imagePath")
    public String imagePath;

    @ColumnInfo(name = "maxCapacity")
    public int maxCapacity;

    @ColumnInfo(name = "isDeleted")
    public boolean isDeleted = false;

    public TourEntity() {}

    @Ignore
    public TourEntity(String location, String duration, double price, String departure, String destination,
                      boolean airway, String transport, String imagePath, int maxCapacity) {
        this.location = location;
        this.duration = duration;
        this.price = price;
        this.departure = departure;
        this.destination = destination;
        this.airway = airway;
        this.transport = transport;
        this.imagePath = imagePath;
        this.maxCapacity = maxCapacity;
        this.isDeleted = false;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getDeparture() { return departure; }
    public void setDeparture(String departure) { this.departure = departure; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public boolean isAirway() { return airway; }
    public void setAirway(boolean airway) { this.airway = airway; }

    public String getTransport() { return transport; }
    public void setTransport(String transport) { this.transport = transport; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public int getMaxCapacity() { return maxCapacity; }
    public void setMaxCapacity(int maxCapacity) { this.maxCapacity = maxCapacity; }

    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }
}
