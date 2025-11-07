package com.example.prm392_final_prj.repository;

import android.content.Context;

import com.example.prm392_final_prj.dao.AppDatabase;

import java.util.Date;
import java.util.List;

public class AnalyticsRepository {
    private final AppDatabase db;

    public AnalyticsRepository(Context context) {
        db = AppDatabase.getDatabase(context);
    }

    public int getTotalTours() {
        return db.tourDao().getTourCount();
    }

    public int getTotalCustomers() {
        return db.userDao().getCustomerCount();
    }

    public List<Integer> getMonthlyBookingCounts(Date from, Date to) {
        return db.bookingOrderDao().getBookingCountByMonth(from, to);
    }

    public List<Double> getMonthlyRevenue(Date from, Date to) {
        return db.bookingOrderDao().getRevenueByMonth(from, to);
    }
}
