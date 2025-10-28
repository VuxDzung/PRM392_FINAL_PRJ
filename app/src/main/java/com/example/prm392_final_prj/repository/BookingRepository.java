package com.example.prm392_final_prj.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.prm392_final_prj.dao.BookingOrderDao;
import com.example.prm392_final_prj.dao.AppDatabase; // <-- ĐÃ THAY ĐỔI
import com.example.prm392_final_prj.entity.BookingOrderEntity;
import java.util.List;

public class BookingRepository {

    private BookingOrderDao mBookingOrderDao;

    public BookingRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mBookingOrderDao = db.bookingOrderDao();
    }

    public void insertBooking(BookingOrderEntity bookingOrder) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mBookingOrderDao.insert(bookingOrder);
        });
    }

    public LiveData<List<BookingOrderEntity>> getBookingsForUser(int userId) {
        return mBookingOrderDao.getBookingsForUser(userId);
    }
}