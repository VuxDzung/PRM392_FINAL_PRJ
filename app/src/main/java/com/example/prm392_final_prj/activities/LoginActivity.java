package com.example.prm392_final_prj.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prm392_final_prj.MainActivity;
import com.example.prm392_final_prj.R;
import com.example.prm392_final_prj.dto.request.LoginRequest;
import com.example.prm392_final_prj.dto.response.LoginResponse;
import com.example.prm392_final_prj.repository.AuthRepository;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername, edtPass;
    private Button btnLogin;
    private AuthRepository authRepository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edtUsername = findViewById(R.id.edtUsername);
        edtPass = findViewById(R.id.edtPass);
        btnLogin = findViewById(R.id.btnLogin);
        authRepository = new AuthRepository(this);

        View.OnFocusChangeListener clearErrorFocus = (v, hasFocus) -> {
            if (hasFocus) ((EditText) v).setError(null);
        };
        edtUsername.setOnFocusChangeListener(clearErrorFocus);
        edtPass.setOnFocusChangeListener(clearErrorFocus);

        TextWatcher clearErrorTextWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                // clear error while typing
                edtUsername.setError(null);
                edtPass.setError(null);
            }
            @Override public void afterTextChanged(Editable s) {}
        };

        edtUsername.addTextChangedListener(clearErrorTextWatcher);
        edtPass.addTextChangedListener(clearErrorTextWatcher);

        btnLogin.setOnClickListener(v -> doLogin());

        // Click for Register now
        TextView tvRegister = findViewById(R.id.tvRegister);
        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        // Click for Reset password
        TextView tvReset = findViewById(R.id.tvReset);
        tvReset.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
        });

    }
    private void doLogin() {
        String username = edtUsername.getText().toString().trim();
        String pass = edtPass.getText().toString().trim();

        if (username.isEmpty()) {
            edtUsername.setError("Vui lòng nhập email");
            edtUsername.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
            edtUsername.setError("Email không hợp lệ");
            edtUsername.requestFocus();
            return;
        }
        if (pass.isEmpty()) {
            edtPass.setError("Vui lòng nhập mật khẩu");
            edtPass.requestFocus();
            return;
        }
        if (pass.length() < 6) {
            edtPass.setError("Mật khẩu ít nhất 6 ký tự");
            edtPass.requestFocus();
            return;
        }

        btnLogin.setEnabled(false);

        authRepository.login(new LoginRequest(username, pass), new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(LoginResponse resp) {
                btnLogin.setEnabled(true);
                String role = authRepository.getSession().getRole(); // single role saved
                if (role != null && (role.equalsIgnoreCase("ROLE_ADMIN") || role.equalsIgnoreCase("admin"))) {
                    startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                } else {
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                }
                finish();
            }

            @Override
            public void onError(int code, String message) {
                btnLogin.setEnabled(true);
                // show server-side auth error on password field (no Toast)
                if (code == 401 || code == 400) {
                    edtPass.setError("Tên đăng nhập hoặc mật khẩu không đúng");
                    edtPass.requestFocus();
                } else {
                    // generic error -> show on username field
                    edtUsername.setError(message != null ? message : "Lỗi kết nối");
                    edtUsername.requestFocus();
                }
            }
        });
    }
}