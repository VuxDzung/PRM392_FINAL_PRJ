package com.example.prm392_final_prj.repository;

import com.example.prm392_final_prj.utils.JwtUtils;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.prm392_final_prj.dao.AppDatabase;
import com.example.prm392_final_prj.dao.UserDao;
import com.example.prm392_final_prj.dto.request.LoginRequest;
import com.example.prm392_final_prj.dto.response.LoginResponse;
import com.example.prm392_final_prj.entity.UserEntity;
import com.example.prm392_final_prj.utils.SessionManager;

import java.security.MessageDigest;
import java.util.concurrent.Executors;

public class AuthRepository {
    // Đăng xuất: xóa token và role khỏi session
    public void logout() {
        session.clear();
    }
    private SessionManager session;
    private UserDao userDao;
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    public AuthRepository(Context ctx) {
        session = new SessionManager(ctx);
        userDao = AppDatabase.getDatabase(ctx).userDao();
    }

    public void login(final LoginRequest request, final AuthCallback callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            String hashed = hashPassword(request.getPassword());
            // adjust getter name if your DTO uses getEmail() instead of getUsername()
            UserEntity user = userDao.findByEmailAndPassword(request.getEmail(), hashed);
            if (user != null) {
                // Tạo JWT
                String token = JwtUtils.createJWT(user.email, user.role);

                session.saveToken(token);
                session.saveRole(user.role);
                session.saveUserId(user.id);

                LoginResponse resp = new LoginResponse();
                resp.setJwtToken(token);

                mainHandler.post(() -> callback.onSuccess(resp));
            } else {
                mainHandler.post(() -> callback.onError(401, "Invalid credentials"));
            }
        });
    }

    public void register(final String firstname, final String lastname, final String phone,
                         final String email, final String password, final RegisterCallback callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // Kiểm tra trùng email
                UserEntity existEmail = userDao.findByEmail(email);
                if (existEmail != null) {
                    mainHandler.post(() -> callback.onError(409, "Email đã tồn tại"));
                    return;
                }
                // Kiểm tra trùng số điện thoại
                UserEntity existPhone = userDao.findByPhone(phone);
                if (existPhone != null) {
                    mainHandler.post(() -> callback.onError(409, "Số điện thoại đã tồn tại"));
                    return;
                }
                // Xác thực số điện thoại mạnh hơn (bắt đầu bằng 0, 10-11 số, chỉ số)
                if (!phone.matches("^0\\d{9,10}$")) {
                    mainHandler.post(() -> callback.onError(400, "Số điện thoại không hợp lệ"));
                    return;
                }

                String hashed = hashPassword(password);
                UserEntity user = new UserEntity();
                user.setFirstname(firstname);
                user.setLastname(lastname);
                user.setPhone(phone);
                user.setEmail(email);
                user.setPassword(hashed);
                user.setRole("ROLE_USER");

                userDao.insert(user);
                mainHandler.post(callback::onSuccess);
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(500, e.getMessage()));
            }
        });
    }

    public void changePassword(String email, String oldPassword, String newPassword, ChangePasswordCallback callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            UserEntity user = userDao.findByEmail(email);
            if (user == null) {
                mainHandler.post(() -> callback.onError(404, "Không tìm thấy tài khoản"));
                return;
            }
            String oldHash = hashPassword(oldPassword);
            if (!user.getPassword().equals(oldHash)) {
                mainHandler.post(() -> callback.onError(401, "Mật khẩu cũ không đúng"));
                return;
            }
            user.setPassword(hashPassword(newPassword));
            userDao.update(user); // update user
            mainHandler.post(callback::onSuccess);
        });
    }

    public interface ChangePasswordCallback {
        void onSuccess();
        void onError(int code, String message);
    }

    private String hashPassword(String plain) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] b = md.digest(plain.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte by : b) sb.append(String.format("%02x", by));
            return sb.toString();
        } catch (Exception e) {
            return plain;
        }
    }

    public interface AuthCallback {
        void onSuccess(LoginResponse resp);

        void onError(int code, String message);
    }

    public interface RegisterCallback {
        void onSuccess();

        void onError(int code, String message);
    }

    public SessionManager getSession() {
        return session;
    }
}