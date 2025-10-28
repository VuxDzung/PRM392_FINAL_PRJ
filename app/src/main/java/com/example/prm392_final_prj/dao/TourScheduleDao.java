package com.example.prm392_final_prj.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.prm392_final_prj.entity.TourScheduleEntity;
import java.util.List;

@Dao
public interface TourScheduleDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(TourScheduleEntity schedule);

    @Query("SELECT * FROM tour_schedule WHERE tourId = :tourId ORDER BY departTime ASC")
    LiveData<List<TourScheduleEntity>> getSchedulesForTour(int tourId);
}