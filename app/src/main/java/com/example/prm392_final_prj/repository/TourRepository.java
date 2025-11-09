package com.example.prm392_final_prj.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.prm392_final_prj.dao.AppDatabase;
import com.example.prm392_final_prj.dao.TourDao;
import com.example.prm392_final_prj.dao.TourScheduleDao;
import com.example.prm392_final_prj.entity.TourEntity;
import com.example.prm392_final_prj.entity.TourScheduleEntity;

import java.util.List;

public class TourRepository {

    private final TourDao mTourDao;
    private final TourScheduleDao mTourScheduleDao;

    public interface OnTourInsertedListener {
        void onTourInserted(long id);
    }

    public TourRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mTourDao = db.tourDao();
        mTourScheduleDao = db.tourScheduleDao();
    }

    // --- Tour ---
    public void insertTour(TourEntity tour, OnTourInsertedListener listener) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            long newId = mTourDao.insert(tour);
            if (listener != null) listener.onTourInserted(newId);
        });
    }

    public void updateTour(TourEntity tour) {
        AppDatabase.databaseWriteExecutor.execute(() -> mTourDao.update(tour));
    }

    public void deleteTour(TourEntity tour) {
        AppDatabase.databaseWriteExecutor.execute(() -> mTourDao.delete(tour));
    }

    public LiveData<List<TourEntity>> getAllTours() {
        return mTourDao.getAllTours();
    }

    public LiveData<TourEntity> getTourById(int id) {
        return mTourDao.getTourById(id);
    }

    public LiveData<List<TourScheduleEntity>> getSchedulesForTour(int tourId) {
        return mTourScheduleDao.getSchedulesForTour(tourId);
    }
    public void insertTourSchedule(TourScheduleEntity schedule) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mTourScheduleDao.insert(schedule);
        });
    }

    public void syncSchedulesForTour(int tourId, List<TourScheduleEntity> newList) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<TourScheduleEntity> oldList = mTourScheduleDao.getSchedulesNow(tourId);

            for (TourScheduleEntity old : oldList) {
                boolean stillExists = false;
                for (TourScheduleEntity newer : newList) {
                    if (old.getId() == newer.getId() && old.getId() != 0) {
                        stillExists = true;
                        break;
                    }
                }
                if (!stillExists) {
                    mTourScheduleDao.delete(old);
                }
            }

            for (TourScheduleEntity newItem : newList) {
                newItem.setTourId(tourId);
                if (newItem.getId() == 0) {
                    mTourScheduleDao.insert(newItem);
                } else {
                    mTourScheduleDao.update(newItem);
                }
            }
        });
    }
}
