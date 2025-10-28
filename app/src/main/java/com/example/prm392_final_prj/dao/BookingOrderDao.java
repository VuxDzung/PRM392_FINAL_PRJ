package com.example.prm392_final_prj.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.prm392_final_prj.entity.BookingOrderEntity;
import java.util.List;

@Dao
public interface BookingOrderDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(BookingOrderEntity bookingOrder);

    @Query("SELECT * FROM booking_order WHERE userId = :userId ORDER BY startTime DESC")
    LiveData<List<BookingOrderEntity>> getBookingsForUser(int userId);
}