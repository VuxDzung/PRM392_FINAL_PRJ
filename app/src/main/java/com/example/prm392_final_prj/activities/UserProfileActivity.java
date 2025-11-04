package com.example.prm392_final_prj.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prm392_final_prj.R;
import com.example.prm392_final_prj.entity.UserEntity;
import com.example.prm392_final_prj.repository.*;
import com.example.prm392_final_prj.utils.*;

import androidx.lifecycle.Observer;

public class UserProfileActivity extends NavigationBaseActivity {
    private TextView tvFullName, tvEmail, tvFirstName, tvLastName, tvPhone, tvEmailDetail;
    private ImageButton btnEdit;
    private UserRepository userRepository;
    private SessionManager sessionManager;
    private int currentUserId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Initialize repository
        userRepository = new UserRepository(getApplication());
        sessionManager = new SessionManager(this);
        currentUserId = sessionManager.getUserId();

        // Check if user is logged in
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            // TODO: Redirect to login activity
            finish();
            return;
        }
        // Initialize views
        initViews();

        // Setup navigation
        setupBottomNavigation(R.id.nav_profile);

        // Load user data
        loadUserProfile();

        // Setup listeners
        setupListeners();
    }

    private void initViews() {
        tvFullName = findViewById(R.id.tvFullName);
        tvEmail = findViewById(R.id.tvEmail);
        tvFirstName = findViewById(R.id.tvFirstName);
        tvLastName = findViewById(R.id.tvLastName);
        tvPhone = findViewById(R.id.tvPhone);
        tvEmailDetail = findViewById(R.id.tvEmailDetail);
        btnEdit = findViewById(R.id.btnEdit);
    }

    private void loadUserProfile() {
        if (currentUserId == -1) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            // Redirect to login
            return;
        }

        // Observe user data from database
        userRepository.getUserById(currentUserId).observe(this, new Observer<UserEntity>() {
            @Override
            public void onChanged(UserEntity user) {
                if (user != null) {
                    displayUserData(user);
                } else {
                    Toast.makeText(UserProfileActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void displayUserData(UserEntity user) {
        String fullName = user.getFirstname() + " " + user.getLastname();
        tvFullName.setText(fullName);
        tvEmail.setText(user.getEmail());
        tvFirstName.setText(user.getFirstname());
        tvLastName.setText(user.getLastname());
        tvPhone.setText(user.getPhone());
        tvEmailDetail.setText(user.getEmail());
    }

    private void setupListeners() {
        btnEdit.setOnClickListener(v -> {
            // Navigate to edit profile activity
            Intent intent = new Intent(UserProfileActivity.this, EditUserProfileActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload user data when returning from edit screen
        if (currentUserId != -1) {
            loadUserProfile();
        }
    }
}