package com.example.prm392_final_prj.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.prm392_final_prj.entity.UserEntity;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(UserEntity user);

    @Query("SELECT * FROM user WHERE email = :email")
    LiveData<UserEntity> getUserByEmail(String email);

    @Query("SELECT * FROM user WHERE id = :id")
    LiveData<UserEntity> getUserById(int id);
}