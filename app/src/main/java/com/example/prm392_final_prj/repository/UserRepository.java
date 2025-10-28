package com.example.prm392_final_prj.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.prm392_final_prj.dao.UserDao;
import com.example.prm392_final_prj.dao.AppDatabase; // <-- ĐÃ THAY ĐỔI
import com.example.prm392_final_prj.entity.UserEntity;

public class UserRepository {

    private UserDao mUserDao;

    public UserRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mUserDao = db.userDao();
    }

    public void insertUser(UserEntity user) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mUserDao.insert(user);
        });
    }

    public LiveData<UserEntity> getUserById(int id) {
        return mUserDao.getUserById(id);
    }

    public LiveData<UserEntity> getUserByEmail(String email) {
        return mUserDao.getUserByEmail(email);
    }
}