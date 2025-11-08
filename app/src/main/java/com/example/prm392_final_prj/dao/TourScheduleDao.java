package com.example.prm392_final_prj.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.prm392_final_prj.entity.TourScheduleEntity;

import java.util.List;

@Dao
public interface TourScheduleDao {

    // ✅ Thêm mới hoặc ghi đè nếu trùng ID
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TourScheduleEntity schedule);

    // ✅ Update các schedule đã tồn tại
    @Update
    void update(TourScheduleEntity schedule);

    // ✅ Xóa một schedule cụ thể
    @Delete
    void delete(TourScheduleEntity schedule);

    // ✅ Lấy danh sách LiveData để hiển thị
    @Query("SELECT * FROM tour_schedule WHERE tourId = :tourId ORDER BY departTime ASC")
    androidx.lifecycle.LiveData<List<TourScheduleEntity>> getSchedulesForTour(int tourId);

    // ✅ Lấy danh sách ngay lập tức (dùng khi sync)
    @Query("SELECT * FROM tour_schedule WHERE tourId = :tourId ORDER BY departTime ASC")
    List<TourScheduleEntity> getSchedulesNow(int tourId);
}
