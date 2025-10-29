package com.example.prm392_final_prj.repository;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.prm392_final_prj.dao.UserDao;
import com.example.prm392_final_prj.dao.AppDatabase; // <-- ĐÃ THAY ĐỔI
import com.example.prm392_final_prj.entity.UserEntity;

public class UserRepository {
    // Hash password bằng SHA-256
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

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

    public interface UpdatePasswordCallback {
        void onResult(boolean success);
    }

    public void updatePasswordByEmail(String email, String newPassword, UpdatePasswordCallback callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            UserEntity user = mUserDao.findByEmail(email);
            if (user == null) {
                if (callback != null) callback.onResult(false);
                return;
            }
            String hashed = hashPassword(newPassword);
            user.setPassword(hashed);
            mUserDao.update(user);
            if (callback != null) callback.onResult(true);
        });
    }
}