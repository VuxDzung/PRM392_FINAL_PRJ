package com.example.prm392_final_prj.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RewriteQueriesToDropUnusedColumns;

import com.example.prm392_final_prj.entity.BookingOrderEntity;

import java.util.Date;
import java.util.List;

@Dao
public interface BookingOrderDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(BookingOrderEntity bookingOrder);

    @Query("SELECT * FROM booking_order WHERE userId = :userId ORDER BY startTime DESC")
    LiveData<List<BookingOrderEntity>> getBookingsForUser(int userId);


    //region Analytics helpers
    // Tổng số booking (sync)
    @Query("SELECT COUNT(*) FROM booking_order")
    int getTotalBookingsSync();

    @Query("SELECT CAST(strftime('%m', startTime) AS INTEGER) AS month, COUNT(*) AS count " +
            "FROM booking_order WHERE startTime BETWEEN :from AND :to GROUP BY month")
    List<MonthlyBookingStat> getBookingCountByMonth(Date from, Date to);

    @Query("SELECT CAST(strftime('%m', bo.startTime) AS INTEGER) AS month, " +
            "SUM(bo.adultAmount * t.price) AS revenue " +
            "FROM booking_order bo JOIN tour t ON bo.tourId = t.id " +
            "WHERE bo.startTime BETWEEN :from AND :to GROUP BY month")
    List<MonthlyRevenueStat> getRevenueByMonth(Date from, Date to);
    //endregion
}