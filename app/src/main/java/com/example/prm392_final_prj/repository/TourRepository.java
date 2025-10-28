package com.example.prm392_final_prj.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.prm392_final_prj.dao.TourDao;
import com.example.prm392_final_prj.dao.TourScheduleDao;
import com.example.prm392_final_prj.dao.AppDatabase; // <-- ĐÃ THAY ĐỔI
import com.example.prm392_final_prj.entity.TourEntity;
import com.example.prm392_final_prj.entity.TourScheduleEntity;
import java.util.List;

public class TourRepository {

    private TourDao mTourDao;
    private TourScheduleDao mTourScheduleDao;

    public TourRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mTourDao = db.tourDao();
        mTourScheduleDao = db.tourScheduleDao();
    }

    // --- Tour ---
    public void insertTour(TourEntity tour) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mTourDao.insert(tour);
        });
    }

    public LiveData<List<TourEntity>> getAllTours() {
        return mTourDao.getAllTours();
    }

    public LiveData<TourEntity> getTourById(int id) {
        return mTourDao.getTourById(id);
    }

    // --- Tour Schedule ---
    public void insertTourSchedule(TourScheduleEntity schedule) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mTourScheduleDao.insert(schedule);
        });
    }

    public LiveData<List<TourScheduleEntity>> getSchedulesForTour(int tourId) {
        return mTourScheduleDao.getSchedulesForTour(tourId);
    }
}