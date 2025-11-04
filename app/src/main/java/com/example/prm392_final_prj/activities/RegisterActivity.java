package com.example.prm392_final_prj.activities;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prm392_final_prj.R;
import com.example.prm392_final_prj.repository.AuthRepository;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText edtFirstName, edtLastName, edtPhone, edtEmail, edtPassword, edtConfirm;
    private TextInputLayout tilFirstName, tilLastName, tilPhone, tilEmail, tilPassword, tilConfirm;
    private Button btnRegister;
    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tilFirstName = findViewById(R.id.tilFirstName);
        tilLastName = findViewById(R.id.tilLastName);
        tilPhone = findViewById(R.id.tilPhone);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirm = findViewById(R.id.tilConfirm);

        edtFirstName = findViewById(R.id.edtFirstName);
        edtLastName = findViewById(R.id.edtLastName);
        edtPhone = findViewById(R.id.edtPhone);
        edtEmail = findViewById(R.id.edtEmail);
    edtPassword = findViewById(R.id.edtPassword);
    edtConfirm = findViewById(R.id.edtConfirm);
    // Set password hint for complexity
    tilPassword.setHelperText("Mật khẩu tối thiểu 6 ký tự, gồm chữ hoa, chữ thường, số và ký tự đặc biệt");

        btnRegister = findViewById(R.id.btnRegister);
        authRepository = new AuthRepository(this);

        btnRegister.setOnClickListener(v -> doRegister());
    }

    private void doRegister() {
        // clear previous errors
        tilFirstName.setError(null);
        tilLastName.setError(null);
        tilPhone.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);
        tilConfirm.setError(null);

        String firstName = edtFirstName.getText().toString().trim();
        String lastName = edtLastName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString();
        String confirm = edtConfirm.getText().toString();

        if (firstName.isEmpty()) {
            tilFirstName.setError("Vui lòng nhập họ");
            edtFirstName.requestFocus();
            return;
        }
        if (lastName.isEmpty()) {
            tilLastName.setError("Vui lòng nhập tên");
            edtLastName.requestFocus();
            return;
        }
        if (phone.isEmpty()) {
            tilPhone.setError("Vui lòng nhập số điện thoại");
            edtPhone.requestFocus();
            return;
        }
        if (phone.length() < 7) {
            tilPhone.setError("Số điện thoại không hợp lệ");
            edtPhone.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            tilEmail.setError("Vui lòng nhập email");
            edtEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Email không hợp lệ");
            edtEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            tilPassword.setError("Vui lòng nhập mật khẩu");
            edtPassword.requestFocus();
            return;
        }
        // Password regex: at least 6 chars, 1 upper, 1 lower, 1 digit, 1 special char
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$";
        if (!password.matches(passwordPattern)) {
            tilPassword.setError("Mật khẩu phải >=6 ký tự, có chữ hoa, chữ thường, số và ký tự đặc biệt");
            edtPassword.requestFocus();
            return;
        }
        if (!password.equals(confirm)) {
            tilConfirm.setError("Mật khẩu không khớp");
            edtConfirm.requestFocus();
            return;
        }
        btnRegister.setEnabled(false);
        authRepository.register(firstName, lastName, phone, email, password, new AuthRepository.RegisterCallback() {
            @Override
            public void onSuccess() {
                btnRegister.setEnabled(true);
                // registration done, finish -> return to login
                finish();
            }

            @Override
            public void onError(int code, String message) {
                btnRegister.setEnabled(true);
                if (code == 409) {
                    tilEmail.setError("Email đã tồn tại");
                    edtEmail.requestFocus();
                } else {
                    tilEmail.setError(message != null ? message : "Lỗi đăng ký");
                }
            }
        });
    }
}