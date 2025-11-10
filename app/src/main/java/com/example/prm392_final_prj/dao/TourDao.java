package com.example.prm392_final_prj.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
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


    @Query("UPDATE tour SET isDeleted = 1 WHERE id = :tourId")
    void softDelete(int tourId);


    @Query("UPDATE tour SET isDeleted = 0 WHERE id = :tourId")
    void restore(int tourId);


    @Query("SELECT * FROM tour WHERE id = :tourId AND isDeleted = 0")
    LiveData<TourEntity> getTourById(int tourId);


    @Query("SELECT * FROM tour WHERE isDeleted = 0 ORDER BY price ASC")
    LiveData<List<TourEntity>> getAllTours();

    @Query("SELECT COUNT(*) FROM tour WHERE isDeleted = 0")
    int getTourCount();
}
