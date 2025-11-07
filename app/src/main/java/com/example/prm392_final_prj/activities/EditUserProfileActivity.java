package com.example.prm392_final_prj.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.example.prm392_final_prj.R;
import com.example.prm392_final_prj.dao.AppDatabase;
import com.example.prm392_final_prj.dao.UserDao;
import com.example.prm392_final_prj.entity.UserEntity;
import com.example.prm392_final_prj.repository.UserRepository;
import com.example.prm392_final_prj.utils.*;

public class EditUserProfileActivity extends NavigationBaseActivity {
    private EditText etFirstName, etLastName, etPhone;
    private TextView tvEmail;
    private Button btnSave, btnChangePassword;
    private ImageButton btnBack, btnChangeImage;
    private UserRepository userRepository;
    private SessionManager sessionManager;
    private int currentUserId;
    private UserEntity currentUser;
    private static final int PICK_IMAGE_REQUEST = 1001;
    private Bitmap selectedBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_user_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userRepository = new UserRepository(getApplication());
        sessionManager = new SessionManager(this);

        // Get current user ID from SessionManager
        currentUserId = sessionManager.getUserId();

        // Check if user is logged in
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        initViews();

        // Setup navigation
        setupBottomNavigation(R.id.nav_profile);

        // Load user data
        loadUserData();

        // Setup listeners
        setupListeners();
    }

    private void initViews() {
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etPhone = findViewById(R.id.etPhone);
        tvEmail = findViewById(R.id.tvEmail);
        btnSave = findViewById(R.id.btnSave);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnBack = findViewById(R.id.btnBack);
        btnChangeImage = findViewById(R.id.btnChangeImage);
    }

    private void loadUserData() {
        if (currentUserId == -1) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Observe user data from database
        userRepository.getUserById(currentUserId).observe(this, new Observer<UserEntity>() {
            @Override
            public void onChanged(UserEntity user) {
                if (user != null) {
                    currentUser = user;
                    displayUserData(user);
                } else {
                    Toast.makeText(EditUserProfileActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void displayUserData(UserEntity user) {
        etFirstName.setText(user.getFirstname());
        etLastName.setText(user.getLastname());
        etPhone.setText(user.getPhone());
        tvEmail.setText(user.getEmail());
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> saveUserProfile());

        btnChangePassword.setOnClickListener(v -> {
            // TODO: Implement change password dialog or navigate to change password activity
            Toast.makeText(this, "Change password feature coming soon", Toast.LENGTH_SHORT).show();
        });

        btnChangeImage.setOnClickListener(v -> openImagePicker());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                selectedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                btnChangeImage.setImageBitmap(selectedBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveUserProfile() {
        // Get input values
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(firstName)) {
            etFirstName.setError("First name is required");
            etFirstName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(lastName)) {
            etLastName.setError("Last name is required");
            etLastName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Phone is required");
            etPhone.requestFocus();
            return;
        }

        // Validate phone format (basic validation)
        if (!phone.matches("^[0-9+\\-\\s()]+$")) {
            etPhone.setError("Invalid phone number format");
            etPhone.requestFocus();
            return;
        }

        // Check if phone is already used by another user
        userRepository.checkPhoneAvailability(phone, currentUserId, new UserRepository.CheckPhoneCallback() {
            @Override
            public void onResult(boolean isAvailable, UserEntity existingUser) {
                runOnUiThread(() -> {
                    if (!isAvailable) {
                        etPhone.setError("Phone number already in use");
                        etPhone.requestFocus();
                    } else {
                        // Update user data
                        updateUser(firstName, lastName, phone);
                    }
                });
            }
        });
    }

    private void updateUser(String firstName, String lastName, String phone) {
        if (currentUser == null) {
            Toast.makeText(this, "Error: User data not loaded", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update user entity
        currentUser.setFirstname(firstName);
        currentUser.setLastname(lastName);
        currentUser.setPhone(phone);
        if (selectedBitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            selectedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            currentUser.setAvatar(stream.toByteArray());
        }

        // Save to database using repository
        userRepository.updateUser(currentUser, new UserRepository.UpdateUserCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    Toast.makeText(EditUserProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Return to profile view
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    Toast.makeText(EditUserProfileActivity.this, "Failed to update profile: " + message, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}