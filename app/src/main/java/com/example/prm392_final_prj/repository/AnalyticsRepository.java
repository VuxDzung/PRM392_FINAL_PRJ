package com.example.prm392_final_prj.repository;

import android.content.Context;

import com.example.prm392_final_prj.dao.AppDatabase;
import com.example.prm392_final_prj.dao.MonthlyBookingStat;
import com.example.prm392_final_prj.dao.MonthlyRevenueStat;

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

    public List<MonthlyBookingStat> getMonthlyBookingCounts(Date from, Date to) {
        return db.bookingOrderDao().getBookingCountByMonth(from, to);
    }

    public List<MonthlyRevenueStat> getMonthlyRevenue(Date from, Date to) {
        return db.bookingOrderDao().getRevenueByMonth(from, to);
    }
}
