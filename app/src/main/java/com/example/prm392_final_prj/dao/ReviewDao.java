package com.example.prm392_final_prj.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.prm392_final_prj.entity.ReviewEntity;
import java.util.List;

@Dao
public interface ReviewDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(ReviewEntity review);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    void update(ReviewEntity review);

    @Delete
    void delete(ReviewEntity review);

    @Query("SELECT * FROM review WHERE tourId = :tourId")
    LiveData<List<ReviewEntity>> getReviewsForTour(int tourId);
}