package com.example.prm392_final_prj.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.prm392_final_prj.entity.UserEntity;

import java.util.List;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(UserEntity user);

    @Query("SELECT * FROM user")
    LiveData<List<UserEntity>> getAllUsers();

    @Query("SELECT * FROM user WHERE email = :email")
    LiveData<UserEntity> getUserByEmail(String email);

    @Query("SELECT * FROM user WHERE id = :id")
    LiveData<UserEntity> getUserById(int id);

    @Query("SELECT * FROM user WHERE email = :email AND password = :password LIMIT 1")
    UserEntity findByEmailAndPassword(String email, String password);

    @Query("SELECT * FROM user WHERE email = :email LIMIT 1")
    UserEntity findByEmail(String email);

    @Query("SELECT * FROM user WHERE phone = :phone LIMIT 1")
    UserEntity findByPhone(String phone);

    @Update
    void update(UserEntity user);

    @Query("SELECT * FROM user")
    List<UserEntity> getAllUsersSync();
    @Delete
    void delete(UserEntity user);

    @Query("SELECT COUNT(*) FROM user")
    int getTotalUsersSync();

    @Query("SELECT COUNT(*) FROM user WHERE role = 'customer'")
    int getCustomerCount();
}