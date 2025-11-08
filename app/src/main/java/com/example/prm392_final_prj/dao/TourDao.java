package com.example.prm392_final_prj.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete; // Thêm import
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.example.prm392_final_prj.entity.TourEntity;
import java.util.List;

@Dao
public interface TourDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(TourEntity tour);

    @Update
    void update(TourEntity tour);

    @Delete // [THÊM MỚI]
    void delete(TourEntity tour);

    @Query("SELECT * FROM tour WHERE id = :tourId")
    LiveData<TourEntity> getTourById(int tourId);

    @Query("SELECT * FROM tour ORDER BY price ASC")
    LiveData<List<TourEntity>> getAllTours();

    @Query("SELECT COUNT(*) FROM tour")
    int getTourCount();
}