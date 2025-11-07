package com.example.prm392_final_prj.mockdata;

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

    private void addWithId(TourEntity tour){
        int id = mockData.size() + 1;
        tour.setId(id);
        mockData.add(tour);
    }

    private void addMockDataForDemo() {
        mockData = new ArrayList<>();

        addWithId(new TourEntity("Ha long Bay", "4 Days - 3 Nights", 120.34, "Ha Noi", "Ha Long", false, "Bus", null, 50));
        addWithId(new TourEntity("Da Nang", "2 Days - 1 Nights", 185.75, "Ha Noi", "Da Nang", true, "Airplane", null, 40));
        addWithId(new TourEntity("Sapa Mist Escape", "3 Days - 2 Nights", 95.00, "Ha Noi", "Sapa", false, "Bus", null, 30)); // Dưới $100
        addWithId(new TourEntity("Hanoi Old Quarter", "1 Day", 49.99, "Ha Noi", "Hanoi", false, "Walking", null, 15)); // Dưới $100
        addWithId(new TourEntity("Nha Trang Mini", "4 Days - 3 Nights", 250.00, "HCM City", "Nha Trang", true, "Airplane", null, 35));


        addWithId(new TourEntity("Central Heritage", "5 Days - 4 Nights", 320.50, "Da Nang", "Hue", true, "Airplane", null, 25));
        addWithId(new TourEntity("Da Lat Flower", "6 Days - 5 Nights", 410.75, "HCM City", "Da Lat", true, "Airplane", null, 20));
        addWithId(new TourEntity("Mekong River Tour", "7 Days - 6 Nights", 499.00, "HCM City", "Can Tho", false, "Cruise", null, 18));


        addWithId(new TourEntity("Vietnam Grand Tour", "10 Days - 9 Nights", 980.00, "Ha Noi", "HCM City", true, "Airplane", null, 12)); // Trên $500
        addWithId(new TourEntity("Southeast Asia", "14 Days - 13 Nights", 1500.00, "Ha Noi", "Bangkok", true, "Airplane", null, 10)); // Trên $500
        addWithId(new TourEntity("Highlands Trek", "8 Days - 7 Nights", 550.00, "HCM City", "Dalat", false, "Bus", null, 15)); // Trên $500


        addWithId(new TourEntity("Ninh Binh Karst", "Weekend Getaway", 150.00, "Ha Noi", "Ninh Binh", false, "Bus", null, 45)); // Weekend Getaway
        addWithId(new TourEntity("Vung Tau Relax", "Weekend Getaway", 180.00, "HCM City", "Vung Tau", false, "Car", null, 50)); // Weekend Getaway

    }
}
