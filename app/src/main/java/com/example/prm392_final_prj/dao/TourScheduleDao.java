package com.example.prm392_final_prj.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.example.prm392_final_prj.entity.TourScheduleEntity;
import java.util.List;

@Dao
public interface TourScheduleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TourScheduleEntity schedule);

    @Update
    void update(TourScheduleEntity schedule);


    @Query("UPDATE tour_schedule SET isDeleted = 1 WHERE id = :scheduleId")
    void softDelete(int scheduleId);


    @Query("UPDATE tour_schedule SET isDeleted = 0 WHERE id = :scheduleId")
    void restore(int scheduleId);


    @Query("SELECT * FROM tour_schedule WHERE tourId = :tourId AND isDeleted = 0 ORDER BY departTime ASC")
    LiveData<List<TourScheduleEntity>> getSchedulesForTour(int tourId);


    @Query("SELECT * FROM tour_schedule WHERE tourId = :tourId AND isDeleted = 0 ORDER BY departTime ASC")
    List<TourScheduleEntity> getSchedulesNow(int tourId);
}
