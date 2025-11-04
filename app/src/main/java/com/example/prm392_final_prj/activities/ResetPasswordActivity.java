package com.example.prm392_final_prj.activities;


import android.content.Intent;
import android.os.Bundle;

import android.widget.Toast;

import com.example.prm392_final_prj.repository.UserRepository;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.button.MaterialButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prm392_final_prj.R;

import com.example.prm392_final_prj.utils.MailSender;
import com.example.prm392_final_prj.utils.ResetPasswordUtils;

import java.io.InputStream;
import java.util.Properties;

public class ResetPasswordActivity extends AppCompatActivity {

    private ResetPasswordUtils resetPasswordUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        resetPasswordUtils = new ResetPasswordUtils();

        TextInputLayout tilEmail = findViewById(R.id.tilEmail);
        TextInputEditText edtEmail = findViewById(R.id.edtEmail);
        MaterialButton btnSendMail = findViewById(R.id.btnSendMail);

        btnSendMail.setOnClickListener(v -> {
            String toEmail = edtEmail.getText() != null ? edtEmail.getText().toString().trim() : "";
            if (toEmail.isEmpty()) {
                tilEmail.setError("Vui lòng nhập email");
                edtEmail.requestFocus();
                return;
            } else {
                tilEmail.setError(null);
            }

            String yourEmail = "";
            String yourAppPassword = "";
            try {
                Properties properties = new Properties();
                InputStream inputStream = getAssets().open("mail.properties");
                properties.load(inputStream);
                yourEmail = properties.getProperty("MAIL_USER", "");
                yourAppPassword = properties.getProperty("MAIL_PASS", "");
                inputStream.close();
            } catch (Exception e) {
                Toast.makeText(this, "Không đọc được mail.properties", Toast.LENGTH_LONG).show();
                return;
            }

            // Generate new password
            String newPassword = resetPasswordUtils.generatePassword();
            String subject = "Reset password";
            String body = "Mật khẩu mới của bạn là: " + newPassword;

            // Update password in DB (Room) bất đồng bộ
            UserRepository userRepository = new UserRepository(getApplication());
            final String finalYourEmail = yourEmail;
            final String finalYourAppPassword = yourAppPassword;
            final String finalSubject = subject;
            final String finalBody = body;
            final String finalToEmail = toEmail;
            userRepository.updatePasswordByEmail(toEmail, newPassword, success -> {
                runOnUiThread(() -> {
                    if (!success) {
                        Toast.makeText(this, "Email không tồn tại trong hệ thống", Toast.LENGTH_LONG).show();
                        return;
                    }
                    // Gửi mail ở background, không cần đợi
                    new Thread(() -> {
                        try {
                            MailSender sender = new MailSender(finalYourEmail, finalYourAppPassword);
                            sender.sendMail(finalSubject, finalBody, finalYourEmail, finalToEmail);
                        } catch (Exception ignored) {}
                    }).start();

                    // Chuyển về LoginActivity và show Toast ngay
                    Toast.makeText(this, "Đã gửi mail!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                });
            });
        });
    }
}