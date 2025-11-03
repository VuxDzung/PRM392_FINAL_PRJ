package com.example.prm392_final_prj.mockdatas;

import com.example.prm392_final_prj.entity.TourEntity;

import java.util.ArrayList;
import java.util.List;

public class TourMockData {
    private List<TourEntity> mockData;

    public TourMockData() {
        addMockDataForDemo();
    }

    public List<TourEntity> getAllTours() {
        return mockData;
    }

    public TourEntity getTourById(int id) {
        for (TourEntity tour : mockData) {
            if (tour.getId() == id) {
                return tour;
            }
        }
        return null;
    }

    private void addMockDataForDemo() {
        mockData = new ArrayList<>();

        mockData.add(new TourEntity("Ha long Bay", "4 Days - 3 Nights", 120.34, "Ha Noi", "Ha Long", false, "Bus", null, 50, 20));
        mockData.add(new TourEntity("Da Nang", "2 Days - 1 Nights", 185.75, "Ha Noi", "Da Nang", true, "Airplane", null, 40, 15));
        mockData.add(new TourEntity("Sapa Mist Escape", "3 Days - 2 Nights", 95.00, "Ha Noi", "Sapa", false, "Bus", null, 30, 10)); // Dưới $100
        mockData.add(new TourEntity("Hanoi Old Quarter", "1 Day", 49.99, "Ha Noi", "Hanoi", false, "Walking", null, 15, 5)); // Dưới $100
        mockData.add(new TourEntity("Nha Trang Mini", "4 Days - 3 Nights", 250.00, "HCM City", "Nha Trang", true, "Airplane", null, 35, 12));


        mockData.add(new TourEntity("Central Heritage", "5 Days - 4 Nights", 320.50, "Da Nang", "Hue", true, "Airplane", null, 25, 8));
        mockData.add(new TourEntity("Da Lat Flower", "6 Days - 5 Nights", 410.75, "HCM City", "Da Lat", true, "Airplane", null, 20, 7));
        mockData.add(new TourEntity("Mekong River Tour", "7 Days - 6 Nights", 499.00, "HCM City", "Can Tho", false, "Cruise", null, 18, 5));


        mockData.add(new TourEntity("Vietnam Grand Tour", "10 Days - 9 Nights", 980.00, "Ha Noi", "HCM City", true, "Airplane", null, 12, 4)); // Trên $500
        mockData.add(new TourEntity("Southeast Asia", "14 Days - 13 Nights", 1500.00, "Ha Noi", "Bangkok", true, "Airplane", null, 10, 2)); // Trên $500
        mockData.add(new TourEntity("Highlands Trek", "8 Days - 7 Nights", 550.00, "HCM City", "Dalat", false, "Bus", null, 15, 6)); // Trên $500


        mockData.add(new TourEntity("Ninh Binh Karst", "Weekend Getaway", 150.00, "Ha Noi", "Ninh Binh", false, "Bus", null, 45, 20)); // Weekend Getaway
        mockData.add(new TourEntity("Vung Tau Relax", "Weekend Getaway", 180.00, "HCM City", "Vung Tau", false, "Car", null, 50, 25)); // Weekend Getaway

    }
}
