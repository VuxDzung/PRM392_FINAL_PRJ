package com.example.prm392_final_prj.activities;
import com.example.prm392_final_prj.utils.JwtUtils;
import org.json.JSONObject;


import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prm392_final_prj.R;
import com.example.prm392_final_prj.repository.AuthRepository;
import com.example.prm392_final_prj.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ChangePasswordActivity extends AppCompatActivity {
    private TextInputEditText edtOldPassword, edtNewPassword, edtConfirmPassword;
    private TextInputLayout tilOldPassword, tilNewPassword, tilConfirmPassword;
    private Button btnChangePassword;
    private AuthRepository authRepository;
    private SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Check quyền truy cập bằng JWT
        sessionManager = new SessionManager(this);
        String token = sessionManager.getToken();
        boolean allow = false;
        if (token != null && JwtUtils.verifyJWT(token)) {
            JSONObject payload = JwtUtils.decodePayload(token);
            if (payload != null && payload.has("role")) {
                String role = payload.optString("role", "");
                if ("ROLE_USER".equals(role)) allow = true;
            }
        }
        if (!allow) {
            Toast.makeText(this, "Không có quyền truy cập trang này", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tilOldPassword = findViewById(R.id.tilOldPassword);
        tilNewPassword = findViewById(R.id.tilNewPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);

        edtOldPassword = findViewById(R.id.edtOldPassword);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);

        btnChangePassword = findViewById(R.id.btnChangePassword);
        authRepository = new AuthRepository(this);
        sessionManager = new SessionManager(this);

        btnChangePassword.setOnClickListener(v -> doChangePassword());
    }

    private void doChangePassword() {
        tilOldPassword.setError(null);
        tilNewPassword.setError(null);
        tilConfirmPassword.setError(null);

        String oldPass = edtOldPassword.getText().toString();
        String newPass = edtNewPassword.getText().toString();
        String confirm = edtConfirmPassword.getText().toString();

        if (oldPass.isEmpty()) {
            tilOldPassword.setError("Nhập mật khẩu cũ");
            edtOldPassword.requestFocus();
            return;
        }
        if (newPass.isEmpty()) {
            tilNewPassword.setError("Nhập mật khẩu mới");
            edtNewPassword.requestFocus();
            return;
        }
        if (newPass.length() < 6) {
            tilNewPassword.setError("Mật khẩu mới phải >= 6 ký tự");
            edtNewPassword.requestFocus();
            return;
        }
        if (!newPass.equals(confirm)) {
            tilConfirmPassword.setError("Mật khẩu nhập lại không khớp");
            edtConfirmPassword.requestFocus();
            return;
        }

        String email = sessionManager.getToken(); // hoặc lấy email từ session/user hiện tại
        authRepository.changePassword(email, oldPass, newPass, new AuthRepository.ChangePasswordCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(ChangePasswordActivity.this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                finish();
            }
            @Override
            public void onError(int code, String message) {
                tilOldPassword.setError(message != null ? message : "Đổi mật khẩu thất bại");
                edtOldPassword.requestFocus();
            }
        });
    }
}